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
package ch.ifocusit.telecom.boundary.repository;

import ch.ifocusit.telecom.domain.Bill;

import javax.enterprise.context.RequestScoped;
import java.time.YearMonth;
import java.util.*;

/**
 * Base on file system bill repository.
 *
 * @author Julien Boz
 */
@RequestScoped
public class ListBillRepository implements BillRepository {

    private final List<Bill> bills = new ArrayList<>();

    public Bill add(Bill bill) {
        bills.add(bill);
        return bill;
    }

    @Override
    public Optional<Bill> getLastBill() {
        return bills.stream().max(Comparator.comparing(Bill::getMonth));
    }

    @Override
    public Optional<Bill> get(YearMonth month) {
        return bills.stream().filter(bill -> Objects.equals(bill.getMonth(), month)).findFirst();
    }

    @Override
    public void clear() {
        bills.clear();
    }
}
