/*
 * Living Documentation
 *
 * Copyright (C) 2017 Focus IT
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
import ch.ifocusit.telecom.repository.BillRespository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
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
@Api(description = "Billing REST Endpoint")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Setter
public class BillingService {

    @Inject
    private BillRespository billRespository;

    @GET
    @Path("/whoami")
    @ApiOperation(value = "nom du service", response = String.class)
    public String whoami() {
        return "service facturation";
    }

    @GET
    @Path("/{month}")
    @ApiOperation(value = "Facturation d'un mois", response = Bill.class)
    @ApiResponses(value = {
            @ApiResponse(code = 400, message = "Invalid input"),
            @ApiResponse(code = 404, message = "Billing not found")}
    )
    public Response getBill(@PathParam("month") final String month, @Context Request request) {
        Optional<Bill> bill = billRespository.get(YearMonth.parse(month));

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
