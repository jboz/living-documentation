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
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.thoughtworks.qdox.model.JavaAnnotatedElement;
import com.thoughtworks.qdox.model.JavaClass;
import com.thoughtworks.qdox.model.JavaField;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;

import java.io.InputStreamReader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static ch.ifocusit.livingdoc.plugin.utils.AsciidocUtil.NEWLINE;
import static org.apache.commons.lang3.StringUtils.EMPTY;

/**
 * Glossary of domain objects in a table representation.
 *
 * @author Julien Boz
 */
@Mojo(name = "glossary", requiresDependencyResolution = ResolutionScope.RUNTIME_PLUS_SYSTEM)
public class GlossaryMojo extends AbstractGlossaryMojo {

    @Parameter(defaultValue = "glossary", required = true)
    private String glossaryOutputFilename;

    @Parameter
    private String glossaryTitle;

    @Parameter(defaultValue = "Object Name|Attribute name|Type|Constraints|Default Value|Description")
    private String glossaryColumnsName;

    @Override
    protected String getOutputFilename() {
        return glossaryOutputFilename;
    }

    @Override
    protected String getTitle() {
        return glossaryTitle;
    }

    @Override
    protected void executeMojo() {

        Map<String, Object> scopes = new HashMap<>();
        scopes.put("classes", getClasses().map(javaClass ->
                ch.ifocusit.livingdoc.plugin.glossary.JavaClass.from(javaClass, this::hasAnnotation)).collect(Collectors.toList()));

        Writer writer = new StringWriter();
        MustacheFactory mf = new DefaultMustacheFactory();
        Mustache mustache = mf.compile(new InputStreamReader(getClass().getResourceAsStream("/default_glossary_template.mustache")), "glossary");
        mustache.execute(writer, scopes);

        asciiDocBuilder.textLine(writer.toString());

//        List<String> rows = new ArrayList<>();
//        getClasses().forEach(clazz -> {
//            // add class entry
//            rows.add(getLinkableName(clazz) + "||" + (clazz.isEnum() ? "Enumeration" : EMPTY) + "|" + getAnnotations(clazz) + "||" + getDescription(clazz));
//
//            // browse fields
//            clazz.getFields().stream()
//                    .filter(this::hasAnnotation) // if annotated
//                    .forEach(field -> {
//                        // add field entry
//                        rows.add("|" + getLinkableName(field) + "|" + getLinkedType(field)
//                                + "|" + getAnnotations(field) + "|" + field.getInitializationExpression()
//                                + "|" + getDescription(field));
//                    });
//        });
//
//        rows.add(0, glossaryColumnsName);
//
//        asciiDocBuilder.tableWithHeaderRow(rows);
    }

    private String getAnnotations(JavaAnnotatedElement model) {
        return model.getAnnotations().stream()
                .filter(annot -> annot.getType().getFullyQualifiedName().startsWith(JAVAX_VALIDATION_CONSTRAINTS)
                        || annot.getType().getFullyQualifiedName().startsWith(HIBERNATE_VALIDATION_CONSTRAINTS))
                .map(annot -> annot.toString().replace(JAVAX_VALIDATION_CONSTRAINTS, "").replace(HIBERNATE_VALIDATION_CONSTRAINTS, ""))
                .collect(Collectors.joining(NEWLINE + NEWLINE));
    }

    private String getLinkableName(JavaClass model) {
        return getAnchor(model.getName()) + getName(model, model.getName());
    }

    private String getLinkableName(JavaField model) {
        return getAnchor(model.getDeclaringClass().getName() + "." + model.getName()) + getName(model, model.getName());
    }

    private String getLinkedType(JavaField javaField) {
        if (javaField.isEnumConstant()) {
            return EMPTY;
        }
        String name = javaField.getType().getName();
        if (getClasses().anyMatch(javaField.getType()::equals)) {
            // type class is in the same domain, create a relative link
            name = "<<glossaryid-" + javaField.getType().getName() + "," + javaField.getType().getName() + ">>";
        }
        if (javaField.getType().isEnum()) {
            name += ", Enumeration";
        }
        return name;
    }

    private String getAnchor(String path) {
        return "anchor:glossaryid-" + path + "[]";
    }
}
