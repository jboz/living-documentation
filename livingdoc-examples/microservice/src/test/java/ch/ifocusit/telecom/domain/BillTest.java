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

import com.fasterxml.jackson.databind.ObjectMapper;
import org.fest.assertions.Assertions;
import org.junit.Test;

import java.math.BigDecimal;
import java.time.Duration;

/**
 * Unit testing of {@link Bill} pojo.
 * Created by U803121 on 01.03.2017.
 *
 * @author Julien Boz
 */
public class BillTest {

    private <T> T read(final String filename, final Class<T> aClass) throws Exception {
        final ObjectMapper mapper = new ObjectMapper();
        mapper.findAndRegisterModules();
        return mapper.readValue(BillTest.class.getResourceAsStream(filename), aClass);
    }

    @Test
    public void getBillAmountShouldSumPrices() throws Exception {
        Bill bill = read("/bill.json", Bill.class);
        Assertions.assertThat(bill.getBillAmount()).isEqualTo(new BigDecimal("49.34"));
    }

    @Test
    public void getTotalCallsDurationShouldSumCallsDuration() throws Exception {
        Bill bill = read("/bill.json", Bill.class);
        Assertions.assertThat(bill.getTotalCallsDuration()).isEqualTo(Duration.parse("PT39M"));
    }

    @Test
    public void getNbCallsShouldSumCalls() throws Exception {
        Bill bill = read("/bill.json", Bill.class);
        Assertions.assertThat(bill.getNbCalls()).isEqualTo(2);
    }

    @Test
    public void getNbSmsShouldSumSms() throws Exception {
        Bill bill = read("/bill.json", Bill.class);
        Assertions.assertThat(bill.getNbSms()).isEqualTo(3);
    }

}