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
package ch.ifocusit.livingdoc.plugin.common;

import java.text.MessageFormat;

import static ch.ifocusit.livingdoc.plugin.common.AsciidocUtil.NEWLINE;
import static ch.ifocusit.livingdoc.plugin.common.StringUtil.defaultString;

/**
 * @author Julien Boz
 */
public class AnchorUtil {

    private static String cleanName(String name) {
        return name.replaceAll(" ", "-").replaceAll("\\.", "_");
    }

    public static String formatLink(String linkTemplate, Integer id, String name) {
        return MessageFormat.format(linkTemplate, defaultString(id, cleanName(name)), name)
                .replace("\\r\\n", NEWLINE).replace("\\n", NEWLINE);
    }
}
