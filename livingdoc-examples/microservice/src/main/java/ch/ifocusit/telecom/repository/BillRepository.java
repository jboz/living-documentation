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
package ch.ifocusit.telecom.repository;

import ch.ifocusit.telecom.domain.Bill;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.transaction.Transactional;
import java.time.YearMonth;
import java.util.Optional;

/**
 * @author Julien Boz
 */
@Transactional
public class BillRepository {

    @PersistenceContext
    private EntityManager em;

    public Optional<Bill> retreiveLastBill() {
        TypedQuery<Bill> query = em.createNamedQuery(Bill.FIND_ALL, Bill.class);
        query.setMaxResults(1);
        return query.getResultList().stream().findFirst();
    }

    public Optional<Bill> get(YearMonth yearMonth) {
        TypedQuery<Bill> query = em.createNamedQuery(Bill.FIND_ALL, Bill.class);
        return query.getResultList().stream().filter((bill) -> bill.getMonth().equals(yearMonth)).findFirst();
    }
}
