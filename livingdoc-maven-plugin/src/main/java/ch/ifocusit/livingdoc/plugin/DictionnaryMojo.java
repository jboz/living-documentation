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
package ch.ifocusit.livingdoc.plugin;

import com.google.common.collect.Lists;
import org.apache.maven.plugins.annotations.Mojo;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Julien Boz
 */
@Mojo(name = "dictionnary")
public class DictionnaryMojo extends CommonGlossaryMojoDefinition {

    @Override
    protected String getDefaultFilename() {
        return "dictionnary.adoc";
    }

    @Override
    protected String getTitle() {
        return "Dictionnary";
    }

    @Override
    protected void executeMojo() {
        List<List<String>> fields = new ArrayList<>();

        javaDocBuilder.getClasses().stream()
                .filter(this::hasAnnotation) // if annotated
                .forEach(javaClass -> {
                    // browse fields
                    javaClass.getFields().stream()
                            .filter(this::hasAnnotation) // if annotated
                            .forEach(javaField -> {
                                // add field entry
                                fields.add(Lists.newArrayList(
                                        getGlossaryId(javaField) == -1 ? "UNDEFINED" : String.valueOf(getGlossaryId(javaField)),
                                        getName(javaField, javaField.getName()),
                                        getDescription(javaField, javaField.getComment())));
                            });
                });

        // rows
        List<String> rows = fields
                .stream()
                .map(field -> field.stream().collect(Collectors.joining("|")))
                .collect(Collectors.toList());
        // add table header row
        rows.add(0, "id|name|description");

        asciiDocBuilder.tableWithHeaderRow(rows);
    }
}
