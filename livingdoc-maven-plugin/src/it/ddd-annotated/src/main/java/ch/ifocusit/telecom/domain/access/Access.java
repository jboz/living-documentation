/*
 * Living Documentation
 *
 * Copyright (C) 2024 Focus IT
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
package ch.ifocusit.telecom.domain.access;

import ch.ifocusit.livingdoc.annotations.UbiquitousLanguage;
import ch.ifocusit.telecom.domain.common.AbstractDomain;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

/**
 * Use of telecom service.
 */
@UbiquitousLanguage(id = 400)
public abstract class Access extends AbstractDomain {

    /**
     * Phone number used.
     */
    @UbiquitousLanguage(id = 401)
    private String phoneNumber;

    /**
     * Price of use of the service.
     */
    @UbiquitousLanguage(id = 402)
    private BigDecimal price;

    /**
     * Timestamp of use.
     */
    @UbiquitousLanguage(id = 403)
    private ZonedDateTime dateTime = ZonedDateTime.now();
}
