/*
 * Living Documentation
 *
 * Copyright (C) 2025 Focus IT
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
import ch.ifocusit.livingdoc.plugin.utils.AnnotationUtil;
import ch.ifocusit.plantuml.classdiagram.NamesMapper;
import org.apache.commons.csv.CSVFormat;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Julien Boz
 */
public class GlossaryNamesMapper<A extends UbiquitousLanguage> implements NamesMapper {

    List<DomainObject> mappings = new ArrayList<>();
    private Class<A> annotation;

    public GlossaryNamesMapper(File file, Class<A> annotation) throws IOException {
        this.annotation = annotation;

        if (file != null) {
            mappings = CSVFormat.RFC4180.builder()
                    .setHeader("id", "name", "description").setSkipHeaderRecord(true).build()
                    .parse(new FileReader(file)).stream()
                    .map(record -> new DomainObject()
                            .setId(Integer.valueOf(record.get("id")))
                            .setName(record.get("name"))
                            .setDescription(record.get("description")))
                    .collect(Collectors.toList());
        }
    }

    public List<DomainObject> getMappings() {
        return Collections.unmodifiableList(mappings);
    }

    private Optional<String> getName(int id) {
        return mappings.stream().filter(def -> def.getId() == id).findFirst().map(DomainObject::getName);
    }

    @Override
    public String getClassName(Class aClass) {
        int id = AnnotationUtil.tryFind(aClass, annotation).map(UbiquitousLanguage::id).orElse(-1);
        return getName(id).orElse(NamesMapper.super.getClassName(aClass));
    }

    /*
     * public Optional<Link> getClassLink(Class aClass) {
     * return Optional.of(create(getClassName(aClass), aClass.getSimpleName(),
     * AnnotationUtil.tryFind(aClass, annotation)));
     * }
     */

    @Override
    public String getFieldName(Field field) {
        int id = AnnotationUtil.tryFind(field, annotation).map(UbiquitousLanguage::id).orElse(-1);
        return getName(id).orElse(NamesMapper.super.getFieldName(field));
    }

    /*    *//**
             * Equilvalent to {@link DomainObject#getFullName()}
             *
             * @param field : java field to treat
             * @return fully classified field name (i.e. ParentClassName.fieldName string)
             *//*
                * public String getFieldFullName(Field field) {
                * return AnchorUtil.glossaryLink(field.getDeclaringClass().getSimpleName(),
                * field.getName());
                * }
                */

    /*
     * public Optional<Link> getFieldLink(Field field) {
     * String fieldLink = getFieldFullName(field);
     * return Optional.of(create(getFieldName(field), fieldLink,
     * AnnotationUtil.tryFind(field, annotation)));
     * }
     * 
     * private Link create(String name, String fullName, Optional<A> annotation) {
     * Link link = new Link();
     * link.setLabel(name);
     * link.setUrl(AnchorUtil.formatLink(linkPage, annotation.map(a -> a.id() == -1
     * ? null : a.id()).orElse(null), fullName));
     * return link;
     * }
     */
}
