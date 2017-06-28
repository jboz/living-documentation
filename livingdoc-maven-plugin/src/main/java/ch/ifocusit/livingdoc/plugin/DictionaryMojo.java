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

import ch.ifocusit.livingdoc.plugin.mapping.MappingDefinition;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.google.common.collect.Lists.newArrayList;

/**
 * @author Julien Boz
 */
@Mojo(name = "dictionary", requiresDependencyResolution = ResolutionScope.RUNTIME_PLUS_SYSTEM)
public class DictionaryMojo extends CommonGlossaryMojoDefinition {

    @Override
    protected String getDefaultFilename() {
        return "dictionary";
    }

    @Override
    protected String getTitle() {
        return "Dictionary";
    }

    @Override
    protected void executeMojo() {
        // regroup all mapping definition
        List<MappingDefinition> definitions = new ArrayList<>();

        getClasses().forEach(javaClass -> {
            // browse fields
            javaClass.getFields().stream()
                    .filter(this::hasAnnotation) // if annotated
                    .forEach(javaField -> {
                        // add field entry
                        definitions.add(map(javaField, javaField.getName(), javaField.getComment()));
                    });
        });

        List<String> rows = definitions.stream()
                // sort
                .sorted()
                // distinct on "business key"
                .filter(distinctByKey())
                // map to List<String>
                .map(def -> newArrayList(defaultString(def.getId(), "UNDEFINED"), def.getName(), def.getDescription()))
                // join List<String> with "|"
                .map(field -> field.stream().collect(Collectors.joining("|")))
                .collect(Collectors.toList());
        // add table header row
        rows.add(0, "id|name|description");

        asciiDocBuilder.tableWithHeaderRow(rows);
    }
}
