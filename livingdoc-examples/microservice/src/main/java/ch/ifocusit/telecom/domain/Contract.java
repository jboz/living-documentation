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
package ch.ifocusit.telecom.domain;

import ch.ifocusit.livingdoc.annotations.UbiquitousLanguage;
import ch.ifocusit.telecom.domain.common.AbstractDomain;
import lombok.AccessLevel;
import lombok.experimental.FieldDefaults;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotNull;
import java.time.LocalDate;
import java.time.MonthDay;

/**
 * Telecom contract
 */
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
@UbiquitousLanguage(id = 200)
public class Contract extends AbstractDomain {

    /**
     * Contract identifier.
     * Generate by the system and communicate to client.
     */
    @NotNull
    @UbiquitousLanguage(id = 201)
    String idContract;

    /**
     * Contract customer.
     */
    @NotNull
    @UbiquitousLanguage(id = 202)
    @ManyToOne
    Customer customer;

    /**
     * Contract effect date.
     */
    @NotNull
    @UbiquitousLanguage(id = 203)
    LocalDate effectDate;

    /**
     * Extract birth day from effect date.
     *
     * @return the contract birth date
     */
    @UbiquitousLanguage(id = 204)
    MonthDay getBirthDay() {
        return MonthDay.from(effectDate);
    }
}

