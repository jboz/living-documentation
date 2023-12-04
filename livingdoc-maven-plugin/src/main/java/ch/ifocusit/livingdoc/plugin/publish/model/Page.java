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
package ch.ifocusit.livingdoc.plugin.publish.model;

import com.google.common.collect.Ordering;
import lombok.Getter;
import lombok.Setter;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * @author Julien Boz
 */
@Getter
@Setter
public class Page implements Comparable<Page> {

    private String spaceKey;
    private String parentId;
    private String content;
    private Path file;
    private String title;

    private final List<Attachment> attachments = new ArrayList<>();

    public String getFileName() {
        return file.getFileName().toString();
    }

    public void addAttachment(String name, String fileName) {
        Attachment attachment = new Attachment();
        attachment.name = name;
        attachment.file = Paths.get(file.getParent().toFile().getAbsolutePath(), fileName);
        attachments.add(attachment);
    }

    public static class Attachment {
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

    @Override
    public String toString() {
        return "Page{" +
                "spaceKey='" + spaceKey + '\'' +
                ", parentId='" + parentId + '\'' +
                ", title='" + title + '\'' +
                ", file='" + file + '\'' +
                '}';
    }
}
