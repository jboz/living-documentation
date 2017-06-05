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
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.EMPTY;

/**
 * @author Julien Boz
 */
@Mojo(name = "glossary")
public class GlossaryMojo extends CommonGlossaryMojoDefinition {

    public static final String NEWLINE = System.getProperty("line.separator");
    public static final String HEARDER = "[[glossaryid-{0}]]\n=== #{0}# - {1}";
    public static final String HEARDER_LITE = "[[glossaryid-{0}]]\n=== {1}";

    @Parameter
    private String glossaryTemplate;

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
        List<MappingDefinition> definitions = new ArrayList<>();


        getClasses().forEach(javaClass -> {
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
        definitions.stream().sorted().filter(distinctByKey()).forEach(this::addGlossarEntry);
    }

    private void addGlossarEntry(MappingDefinition def) {
        String text = MessageFormat.format(def.getId() == null ? HEARDER_LITE : HEARDER, idFromName(def), def.getName());

        if (StringUtils.isNotBlank(glossaryTemplate)) {
            text = MessageFormat.format(HEARDER_LITE, defaultString(def.getId(), EMPTY), def.getName());
        }

        asciiDocBuilder.textLine(text);
        asciiDocBuilder.textLine(def.getDescription());
        asciiDocBuilder.textLine("");
    }
}
