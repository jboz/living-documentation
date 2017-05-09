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
import com.thoughtworks.qdox.model.JavaAnnotatedElement;
import org.apache.maven.plugins.annotations.Mojo;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author Julien Boz
 */
@Mojo(name = "glossary")
public class GlossaryMojo extends CommonGlossaryMojoDefinition {

    @Override
    protected String getDefaultFilename() {
        return "glossary";
    }

    @Override
    protected String getTitle() {
        return "Glossary";
    }

    @Override
    protected void executeMojo() {
        // regroup all mapping definition
        Set<MappingDefinition> definitions = new HashSet<>();

        javaDocBuilder.getClasses().stream()
                .filter(this::hasAnnotation) // if annotated
                .forEach(javaClass -> {
                    // add class entry
                    definitions.add(map(javaClass, javaClass.getName(), javaClass.getComment()));

                    // browse fields
                    javaClass.getFields().stream()
                            .filter(this::hasAnnotation) // if annotated
                            .forEach(javaField -> {
                                // add field entry
                                definitions.add(map(javaField, javaField.getName(), javaField.getComment()));
                            });
                });

        // sort definitions before add asciidoc entries
        definitions.stream().sorted().forEach(this::addGlossarEntry);
    }

    private void addGlossarEntry(MappingDefinition definition) {
        asciiDocBuilder.sectionTitleLevel2((definition.getId() != -1 ? definition.getId() + " - " : "") + definition.getName());
        asciiDocBuilder.textLine("");
        asciiDocBuilder.textLine(definition.getDescription());
        asciiDocBuilder.textLine("");
    }
}
