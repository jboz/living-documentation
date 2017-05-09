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

import ch.ifocusit.livingdoc.annotations.Glossary;
import ch.ifocusit.plantuml.classdiagram.NamesMapper;
import org.simpleflatmapper.csv.CsvParser;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * @author Julien Boz
 */
public class GlossaryNamesMapper<A extends Glossary> implements NamesMapper {

    List<MappingDefinition> mappings;
    private Class<A> annotation;

    public GlossaryNamesMapper(File file, Class<A> annotation) throws IOException {
        this.annotation = annotation;

        mappings = CsvParser.mapTo(MappingDefinition.class)
                .stream(new FileReader(file))
                .map(MappingDefinition::checkName)
                .collect(Collectors.toList());
    }

    private Optional<String> getName(int id) {
        return mappings.stream().filter(def -> def.getId() == id).findFirst().map(def -> def.getName());
    }

    @Override
    public String getClassNameForDiagram(Class aClass) {
        int id = aClass.isAnnotationPresent(annotation) ? ((A) aClass.getAnnotation(annotation)).id() : -1;
        return getName(id).orElse(NamesMapper.super.getClassNameForDiagram(aClass));
    }

    @Override
    public String getFieldNameForDiagram(Field field) {
        int id = field.isAnnotationPresent(annotation) ? ((A) field.getAnnotation(annotation)).id() : -1;
        return getName(id).orElse(NamesMapper.super.getFieldNameForDiagram(field));
    }

}
