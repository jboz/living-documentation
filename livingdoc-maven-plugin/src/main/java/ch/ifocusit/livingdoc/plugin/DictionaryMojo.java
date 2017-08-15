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
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.ResolutionScope;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.google.common.collect.Lists.newArrayList;
import static org.apache.commons.lang3.StringUtils.EMPTY;

/**
 * Glossary of domain objects in a table representation.
 *
 * @author Julien Boz
 */
@Mojo(name = "dictionary", requiresDependencyResolution = ResolutionScope.RUNTIME_PLUS_SYSTEM)
public class DictionaryMojo extends AbstractGlossaryMojo {

    @Override
    protected String getDefaultFilename() {
        return "dictionary";
    }

    @Override
    protected String getTitle() {
        return "Dictionary";
    }

    @Override
    protected void executeMojo(Stream<DomainObject> domainObjects) {

        List<String> rows = domainObjects
                // map to List<String>
                .map(def -> {
                    // set empty content if no glossary id defined
                    String idColumn = def.getId() == null ? EMPTY : formatAndLink(GLOSSARY_LINK_INLINE_ID, def);
                    return newArrayList(idColumn, formatAndLink(GLOSSARY_LINK_INLINE_NAME, def), def.getDescription());
                })
                // join List<String> with "|"
                .map(field -> field.stream().collect(Collectors.joining("|")))
                .collect(Collectors.toList());
        // add table header row
        rows.add(0, "id|name|description");

        asciiDocBuilder.tableWithHeaderRow(rows);
    }
}
