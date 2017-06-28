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

import java.math.BigDecimal;

/**
 * @author Julien Boz
 */
public class Item {

    public static enum ItemType {
        SMS, CALL
    }

    private ItemType type;
    private String phoneNumber;
    private String dateTime;
    private BigDecimal price;
    private String duration;

    public Item(ItemType type, String phoneNumber, String dateTime, BigDecimal price, String duration) {
        this.type = type;
        this.phoneNumber = phoneNumber;
        this.dateTime = dateTime;
        this.price = price;
        this.duration = duration;
    }

    public ItemType getType() {
        return type;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getDateTime() {
        return dateTime;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public String getDuration() {
        return duration;
    }
}
