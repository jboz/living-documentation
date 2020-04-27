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
package ch.ifocusit.livingdoc.plugin;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

import ch.ifocusit.livingdoc.plugin.baseMojo.AbstractGlossaryMojo;
import ch.ifocusit.livingdoc.plugin.glossary.JavaClass;
import ch.ifocusit.livingdoc.plugin.utils.MustacheUtil;

/**
 * Glossary of domain objects in a title/content representation.
 *
 * @author Julien Boz
 */
@Mojo(name = "dictionary", requiresDependencyResolution = ResolutionScope.RUNTIME_PLUS_SYSTEM, defaultPhase = LifecyclePhase.PROCESS_CLASSES)
public class DictionaryMojo extends AbstractGlossaryMojo {
    private static final String DEFAULT_DICTIONARY_TEMPLATE_MUSTACHE = "/default_dictionary_template.mustache";

    @Parameter(property = "livingdoc.dictionary.output.filename", defaultValue = "dictionary", required = true)
    private String dictionaryOutputFilename;

    @Parameter(property = "livingdoc.dictionary.title")
    private String dictionaryTitle;

    @Parameter(property = "livingdoc.dictionary.link.activate", defaultValue = "true")
    private boolean dictionaryWithLink;

    @Parameter(property = "livingdoc.dictionary.template")
    private File dictionaryTemplate;

    @Override
    protected String getOutputFilename() {
        return dictionaryOutputFilename;
    }

    @Override
    protected String getTitle() {
        return dictionaryTitle;
    }

    @Override
    protected void executeGlossaryMojo() throws Exception {
        List<JavaClass> classes = getClasses().map(javaClass -> JavaClass.from(javaClass, this::hasAnnotation,
                getClasses().collect(Collectors.toList()), this)).sorted().collect(Collectors.toList());

        Map<String, Object> scopes = new HashMap<>();
        scopes.put("classes", classes);
        scopes.put("withLink", dictionaryWithLink);

        asciiDocBuilder
                .textLine(MustacheUtil.execute(dictionaryTemplate, DEFAULT_DICTIONARY_TEMPLATE_MUSTACHE, scopes));

        somethingWasGenerated = !classes.isEmpty();
    }
}
