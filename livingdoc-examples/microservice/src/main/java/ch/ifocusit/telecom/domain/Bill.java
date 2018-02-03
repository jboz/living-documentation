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

import ch.ifocusit.livingdoc.annotations.RootAggregate;
import ch.ifocusit.livingdoc.annotations.UbiquitousLanguage;
import ch.ifocusit.telecom.domain.common.AbstractDomain;
import ch.ifocusit.telecom.service.infra.YearMonthToStringConverter;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.time.YearMonth;

/**
 * *Monthly* bill.
 * [NOTE]
 * Generate by the system at contract birth date.
 *
 * @since 2017-03-01
 */
@RootAggregate
@UbiquitousLanguage(id = 100)
@Entity
@NamedQueries({
        @NamedQuery(name = Bill.FIND_ALL, query = "SELECT b FROM Bill b ORDER BY b.month DESC")
})
@FieldDefaults(level = AccessLevel.PRIVATE)
@Getter
@Setter
public class Bill extends AbstractDomain {
    public static final String FIND_ALL = "Bill.findAll";

    /**
     * Facturation month.
     */
    @NotNull
    @UbiquitousLanguage(id = 101)
    @Convert(converter = YearMonthToStringConverter.class)
    YearMonth month;

    /**
     * °°
     * Contract concerned by the bill.
     */
    @NotNull
    @UbiquitousLanguage(id = 102)
    @ManyToOne
    Contract contract;

//    /**
//     * Bill contents.
//     */
//    @UbiquitousLanguage(id = 103)
//    @OneToMany
//    @JoinTable
//    Set<Access> accesses = new HashSet<>();

    /**
     * Flag that indicate whenever the bill has been sended to the customer.
     */
    boolean customerInformed;

}
