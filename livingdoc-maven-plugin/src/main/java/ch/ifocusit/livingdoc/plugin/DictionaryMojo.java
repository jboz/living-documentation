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

import ch.ifocusit.livingdoc.plugin.baseMojo.AbstractGlossaryMojo;
import ch.ifocusit.livingdoc.plugin.mapping.DomainObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

import java.util.ArrayList;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.defaultString;

/**
 * Glossary of domain objects in a title/content representation.
 *
 * @author Julien Boz
 */
@Mojo(name = "dictionary", requiresDependencyResolution = ResolutionScope.RUNTIME_PLUS_SYSTEM)
public class DictionaryMojo extends AbstractGlossaryMojo {

    @Parameter(defaultValue = "dictionary", required = true)
    private String dictionaryOutputFilename;

    @Parameter
    private String dictionaryTitle;

    @Override
    protected String getOutputFilename() {
        return dictionaryOutputFilename;
    }

    @Override
    protected String getTitle() {
        return dictionaryTitle;
    }

    @Override
    protected void executeMojo() {

        // regroup all mapping definition
        List<DomainObject> definitions = new ArrayList<>();

        getClasses().forEach(javaClass -> {
            // add class entry
            definitions.add(map(javaClass));

            // browse fields
            javaClass.getFields().stream()
                    .filter(this::hasAnnotation) // if annotated
                    .forEach(javaField -> {
                        // add field entry
                        definitions.add(map(javaField));
                    });
        });

        // add asciidoc entries
        definitions.stream().sorted().filter(distinctByKey()).forEach(this::addGlossarEntry);
    }

    private void addGlossarEntry(DomainObject domainObject) {
        asciiDocBuilder.textLine(
                formatAndLink(
                        defaultString(glossaryTitleTemplate, domainObject.getId() != null ? GLOSSARY_LINK_TITLE : GLOSSARY_LINK_TITLE_LITE),
                        domainObject));
        asciiDocBuilder.textLine(domainObject.getFullDescription());
        asciiDocBuilder.textLine(StringUtils.EMPTY);

        somethingWasGenerated = true;
    }
}
