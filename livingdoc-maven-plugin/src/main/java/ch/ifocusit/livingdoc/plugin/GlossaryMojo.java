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
import ch.ifocusit.livingdoc.plugin.glossary.JavaClass;
import ch.ifocusit.livingdoc.plugin.utils.MustacheUtil;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Glossary of domain objects in a table representation.
 *
 * @author Julien Boz
 */
@Mojo(name = "glossary", requiresDependencyResolution = ResolutionScope.RUNTIME_PLUS_SYSTEM)
public class GlossaryMojo extends AbstractGlossaryMojo {

    private static final String DEFAULT_GLOSSARY_TEMPLATE_MUSTACHE = "/default_glossary_template.mustache";
    @Parameter(defaultValue = "glossary", required = true)
    private String glossaryOutputFilename;

    @Parameter
    private String glossaryTitle;

    @Parameter(defaultValue = "Id|Object Name|Attribute name|Type|Description|Constraints|Default Value")
    private String glossaryColumnsName;

    @Parameter
    private File glossaryTemplate;

    @Parameter(defaultValue = "true")
    private boolean glossaryWithLink = true;

    @Override
    protected String getOutputFilename() {
        return glossaryOutputFilename;
    }

    @Override
    protected String getTitle() {
        return glossaryTitle;
    }

    @Override
    protected void executeMojo() throws Exception {

        List<JavaClass> classes = getClasses()
                .map(javaClass -> JavaClass.from(javaClass, this::hasAnnotation, getClasses().collect(Collectors.toList())))
                .collect(Collectors.toList());

        boolean withId = classes.stream().anyMatch(JavaClass::hasId);

        Map<String, Object> scopes = new HashMap<>();
        scopes.put("glossaryColumnsName", withId ? glossaryColumnsName : glossaryColumnsName.replace("|Id", ""));
        scopes.put("classes", classes);
        scopes.put("withLink", glossaryWithLink);

        asciiDocBuilder.textLine(MustacheUtil.execute(glossaryTemplate, DEFAULT_GLOSSARY_TEMPLATE_MUSTACHE, scopes));
    }
}
