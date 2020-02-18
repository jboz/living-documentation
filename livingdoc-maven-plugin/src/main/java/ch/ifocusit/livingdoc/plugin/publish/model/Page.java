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
package ch.ifocusit.livingdoc.plugin.publish.model;

import com.google.common.collect.Ordering;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author Julien Boz
 */
public class Page implements Comparable<Page> {

    private String spaceKey;
    private String parentId;
    private String content;
    private Path file;
    private String title;

    private List<Attachement> attachements = new ArrayList<>();

    public String getSpaceKey() {
        return spaceKey;
    }

    public void setSpaceKey(final String spaceKey) {
        this.spaceKey = spaceKey;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(final String parentId) {
        this.parentId = parentId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(final String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public Path getFile() {
        return file;
    }

    public void setFile(final Path file) {
        this.file = file;
    }

    public String getFileName() {
        return file.getFileName().toString();
    }

    public List<Attachement> getAttachements() {
        return attachements;
    }

    public void addAttachement(String name, String fileName) {
        Attachement attachement = new Attachement();
        attachement.name = name;
        attachement.file = Paths.get(file.getParent().toFile().getAbsolutePath(), fileName);
        attachements.add(attachement);
    }

    public static class Attachement {
        String name;
        Path file;

        public String getName() {
            return name;
        }

        public Path getFile() {
            return file;
        }
    }

    @Override
    public int compareTo(final Page o) {
        return Ordering.from(Comparator.comparing(Page::getFileName)).compare(this, o);
    }
}
