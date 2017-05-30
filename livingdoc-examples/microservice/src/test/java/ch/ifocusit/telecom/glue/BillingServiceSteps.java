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
package ch.ifocusit.telecom.glue;

import ch.ifocusit.telecom.domain.Bill;
import ch.ifocusit.telecom.domain.access.CallAccess;
import ch.ifocusit.telecom.domain.access.SmsAccess;
import ch.ifocusit.telecom.repository.BillRespository;
import ch.ifocusit.telecom.repository.ListBillRespository;
import ch.ifocusit.telecom.rest.BillingService;
import cucumber.api.java.Before;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;

import javax.ws.rs.core.Response;
import java.time.*;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

import static ch.ifocusit.telecom.glue.Commons.*;
import static org.fest.assertions.Assertions.assertThat;

/**
 * @author Julien Boz
 */
public class BillingServiceSteps {

    private static final Random RANDOM = new Random();

    private BillRespository billRespository;
    private BillingService service;

    private Bill currentBill;

    private Response response;

    @Before
    public void setUp() {
        billRespository = new ListBillRespository();
        service = new BillingService();
        service.setBillRespository(billRespository);
        billRespository.clear();
    }

    private CallAccess createCall(String phoneNumber, int minutes, String moment) {
        CallAccess access = new CallAccess();
        access.setPhoneNumber(phoneNumber);
        access.setDuration(Duration.ofMinutes(minutes));
        access.setDateTime(ZonedDateTime.of(LocalDateTime.parse(moment), ZoneId.systemDefault()));
        return access;
    }

    private SmsAccess createSms(String phoneNumber, String moment) {
        SmsAccess access = new SmsAccess();
        access.setPhoneNumber(phoneNumber);
        access.setDateTime(ZonedDateTime.of(LocalDateTime.parse(moment), ZoneId.systemDefault()));
        return access;
    }

    @Given("^a bill of " + MONTH)
    public void createBill(String month) {
        currentBill = new Bill();
        currentBill.setMonth(YearMonth.parse(month));
        currentBill = billRespository.add(currentBill);
    }

    @Given("^a call to " + PHONE + " for (\\d+) minutes the " + MOMENT + "$")
    public void addCall(String phoneNumber, int minutes, String moment) {
        currentBill.addAccess(createCall(phoneNumber, minutes, moment));
    }

    @Given("^a SMS to " + PHONE + " the " + MOMENT)
    public void addSMS(String phoneNumber, String moment) {
        currentBill.addAccess(createSms(phoneNumber, moment));
    }

    @Given("^(\\d+) SMS to the " + PHONE + " phone number during the " + MONTH + " month on different moments$")
    public void addSMSWithRandomDay(int nbSMS, String phoneNumber, String month) {
        for (int i = 0; i < nbSMS; i++) {
            LocalDate date = LocalDate.parse(month + "-01");
            date = date.withDayOfMonth(RANDOM.nextInt(date.lengthOfMonth()) + 1);
            LocalTime time = LocalTime.of(RANDOM.nextInt(23), RANDOM.nextInt(59), RANDOM.nextInt(59));
            addSMS(phoneNumber, LocalDateTime.of(date, time).toString());
        }
    }

    @When("^I get billing of " + MONTH + " month$")
    public void getBill(String month) {
        response = service.getBill(month, null);
    }

    private Bill readReturnedBill() {
        assertThat(response).isNotNull();
        assertThat(response.getStatus()).isEqualTo(Response.Status.OK.getStatusCode());
        assertThat(response.getEntity()).isInstanceOf(Bill.class);
        return (Bill) response.getEntity();
    }

    @Then("^the bill of month " + MONTH + " is returned$")
    public void assertCallPresent(String month) {
        Bill bill = readReturnedBill();
        assertThat(bill.getMonth()).isEqualTo(YearMonth.parse(month));
    }

    @Then("^I can show a call to the " + PHONE + " during (\\d+) minutes the " + MOMENT + "$")
    public void assertCallPresent(String phoneNumber, int minutes, String moment) {
        assertThat(readReturnedBill().getAccesses()).contains(createCall(phoneNumber, minutes, moment));
    }

    @Then("^I show the SMS to the " + PHONE + " the " + MOMENT + "$")
    public void assertSmsPresent(String phoneNumber, String moment) {
        assertThat(readReturnedBill().getAccesses()).contains(createSms(phoneNumber, moment));
    }

    @Then("^I show the (\\d+) SMS the " + PHONE + " in then " + MONTH + " month$")
    public void assertAllSmsPresent(int nbSMS, String phoneNumber, String month) {
        LocalDate date = LocalDate.parse(month + "-01");
        final List<SmsAccess> sms = readReturnedBill().getAccesses().stream().filter(SmsAccess.class::isInstance).map(SmsAccess.class::cast)
                .filter(access -> access.getPhoneNumber().equals(phoneNumber)
                        && access.getDateTime().getMonth().equals(date.getMonth())
                        && access.getDateTime().getYear() == date.getYear()).collect(Collectors.toList());

        assertThat(sms).hasSize(nbSMS);
    }

    @Then("^I show a total call of (\\d+) minutes$")
    public void assertTotalCallsDuration(int totalCallsDuration) {
        assertThat(readReturnedBill().getTotalCallsDuration()).isEqualTo(Duration.ofMinutes(totalCallsDuration));
    }
}