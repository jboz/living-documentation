/*
 * Living Documentation
 *
 * Copyright (C) 2025 Focus IT
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

import java.time.LocalDate;
import java.time.MonthDay;

import ch.ifocusit.livingdoc.annotations.UbiquitousLanguage;
import ch.ifocusit.telecom.domain.common.AbstractDomain;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Telecom contract
 */
@UbiquitousLanguage(id = 200)
@Data
@EqualsAndHashCode(callSuper = true)
public class Contract extends AbstractDomain {

    /**
     * Contract identifier.
     * Generate by the system and communicate to client.
     */
    @UbiquitousLanguage(id = 201)
    private String id;

    /**
     * Contract customer.
     */
    @UbiquitousLanguage(id = 202)
    private Customer customer;

    /**
     * Contract effect date.
     */
    @UbiquitousLanguage(id = 203)
    private LocalDate effectDate;

    /**
     * Extract birth day from effect date.
     *
     * @return the contract birth date
     */
    @UbiquitousLanguage(id = 204)
    public MonthDay getBirthDay() {
        return MonthDay.from(effectDate);
    }
}
