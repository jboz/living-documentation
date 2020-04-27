/*
 * Living Documentation
 *
 * Copyright (C) 2020 Focus IT
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
import ch.ifocusit.livingdoc.annotations.RootAggregate;
import ch.ifocusit.telecom.domain.access.Access;
import ch.ifocusit.telecom.domain.common.AbstractDomain;

import java.time.YearMonth;
import java.util.HashSet;
import java.util.Set;

/**
 * *Monthly* bill.
 * [NOTE]
 * Generate by the system at contract birth date.
 *
 * @since 2017-03-01
 */
@RootAggregate
@UbiquitousLanguage(id = 100)
public class Bill extends AbstractDomain {

    /**
     * Facturation month.
     */
    @UbiquitousLanguage(id = 101)
    private YearMonth month;

    /**
     * Contract concerned by the bill.
     */
    @UbiquitousLanguage(id = 102)
    private Contract contract;

    /**
     * Bill contents.
     */
    @UbiquitousLanguage(id = 103)
    private Set<Access> accesses = new HashSet<>();

    /**
     * Bill payment state
     */
    @UbiquitousLanguage(id = 104)
    private PaymentState paymentState;

    /**
     * Flag that indicate whenever the bill has been sended to the customer.
     */
    private boolean customerInformed;
}
