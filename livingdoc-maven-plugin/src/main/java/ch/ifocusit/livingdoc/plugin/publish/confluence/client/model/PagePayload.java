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
package ch.ifocusit.livingdoc.plugin.publish.confluence.client.model;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Alain Sahli
 * @author Julien Boz
 */
public class PagePayload {

    private String title;
    private Space space;
    private Body body;
    private final List<Ancestor> ancestors = new ArrayList<>();
    private Version version;

    public void addAncestor(Ancestor ancestor) {
        this.ancestors.add(ancestor);
    }

    public String getType() {
        return "page";
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Space getSpace() {
        return this.space;
    }

    public void setSpace(Space space) {
        this.space = space;
    }

    public Body getBody() {
        return this.body;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    public List<Ancestor> getAncestors() {
        return this.ancestors;
    }

    public Version getVersion() {
        return this.version;
    }

    public void setVersion(Version version) {
        this.version = version;
    }

}
