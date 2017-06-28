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
package ch.ifocusit.livingdoc.plugin.mapping;

import ch.ifocusit.livingdoc.annotations.UbiquitousLanguage;
import ch.ifocusit.livingdoc.plugin.AnnotationUtils;
import ch.ifocusit.plantuml.classdiagram.NamesMapper;
import ch.ifocusit.plantuml.classdiagram.model.Link;
import org.simpleflatmapper.csv.CsvParser;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Julien Boz
 */
public class GlossaryNamesMapper<A extends UbiquitousLanguage> implements NamesMapper {

    List<MappingDefinition> mappings = new ArrayList<>();
    private Class<A> annotation;

    private String linkTemplate;

    public GlossaryNamesMapper(File file, Class<A> annotation, String linkTemplate) throws IOException {
        this.annotation = annotation;
        this.linkTemplate = linkTemplate;

        if (file != null) {
            mappings = CsvParser.mapTo(MappingDefinition.class)
                    .stream(new FileReader(file))
                    .collect(Collectors.toList());
        }
    }

    private Optional<String> getName(int id) {
        return mappings.stream().filter(def -> def.getId() == id).findFirst().map(MappingDefinition::getName);
    }

    @Override
    public String getClassName(Class aClass) {
        int id = AnnotationUtils.tryFind(aClass, annotation).map(UbiquitousLanguage::id).orElse(-1);
        return getName(id).orElse(NamesMapper.super.getClassName(aClass));
    }

    @Override
    public Optional<Link> getClassLink(Class aClass) {
        return Optional.of(create(getClassName(aClass), AnnotationUtils.tryFind(aClass, annotation)));
    }

    @Override
    public String getFieldName(Field field) {
        int id = AnnotationUtils.tryFind(field, annotation).map(UbiquitousLanguage::id).orElse(-1);
        return getName(id).orElse(NamesMapper.super.getFieldName(field));
    }

    @Override
    public Optional<Link> getFieldLink(Field field) {
        return Optional.of(create(getFieldName(field), AnnotationUtils.tryFind(field, annotation)));
    }

    private Link create(String name, Optional<A> annotation) {
        Link link = new Link();
        link.setLabel(name);
        String anchor = annotation.map(annot -> annot.id() == -1 ? linkFromName(name) : String.valueOf(annot.id()))
                .orElse(linkFromName(name));
        link.setUrl(MessageFormat.format(linkTemplate, anchor));
        return link;
    }

    private String linkFromName(String name) {
        return name.replaceAll(" ", "");
    }
}
