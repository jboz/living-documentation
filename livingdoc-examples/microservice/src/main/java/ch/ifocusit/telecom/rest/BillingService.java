/*
 * Living Documentation
 *
 * Copyright (C) 2023 Focus IT
 *
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package ch.ifocusit.telecom.rest;

import ch.ifocusit.telecom.domain.Bill;
import ch.ifocusit.telecom.repository.BillRepository;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import lombok.Setter;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.ws.rs.*;
import javax.ws.rs.core.*;
import java.time.YearMonth;
import java.util.Optional;

/**
 * @author Julien Boz
 */
@RequestScoped
@Path("/billing")
@OpenAPIDefinition(info = @Info(title = "Billing REST Endpoint",version = "1.0"))
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Setter
public class BillingService {

    @Inject
    private BillRepository repository;

    @GET
    @Path("/whoami")
    @Operation(summary = "Get user by user name", responses = {
            @ApiResponse(description = "Service description",
                    content = @Content(schema = @Schema(implementation = String.class)))
    })
    public String whoami() {
        return "service facturation";
    }

    @GET
    @Path("/{month}")
    @Operation(summary = "Facturation d'un mois", responses = {
            @ApiResponse(description = "Service description",
                    content = @Content(schema = @Schema(implementation = Bill.class))),
            @ApiResponse(responseCode = "400", description = "Invalid input"),
            @ApiResponse(responseCode = "404", description = "Bill not found")
    })
    public Response getBill(@PathParam("month") final String month, @Context Request request) {
        Optional<Bill> bill = repository.get(YearMonth.parse(month));

        if (!bill.isPresent()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        EntityTag etag = new EntityTag(Integer.toString(bill.get().hashCode()));
        Response.ResponseBuilder preconditions = request == null ? null : request.evaluatePreconditions(etag);

        // cached resource did change -> serve updated content
        if (preconditions == null) {
            preconditions = Response.ok(bill.get()).tag(etag);
        }

        return preconditions.build();
    }
}
