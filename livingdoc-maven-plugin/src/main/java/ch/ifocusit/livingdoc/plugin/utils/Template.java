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
package ch.ifocusit.livingdoc.plugin.utils;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * @author Julien Boz
 */
public class Template {

    @Parameter(required = true)
    private File template;

    @Parameter(required = true, defaultValue = "{value}")
    private String token;

    public static Template create(final URL url) {
        try {
            Template parameter = new Template();
            parameter.template = new File(url.toURI());
            return parameter;
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public String process(String value) throws MojoExecutionException {
        try {
            // read template
            final String content = IOUtils.toString(new FileInputStream(template), Charset.defaultCharset());

            // validate content
            if (StringUtils.isBlank(content)) {
                throw new MojoExecutionException(String.format("Template founded (%s) but empty !", template));
            }
            if (!content.contains(token)) {
                throw new MojoExecutionException(String.format("1Token '%s' is not present in template file !", token));
            }
            // update content
            return content.replace(token, value);
        } catch (FileNotFoundException e) {
            throw new MojoExecutionException(String.format("Remplate file not found (%s) !", template), e);
        } catch (IOException e) {
            throw new MojoExecutionException(String.format("Error during process template file (%s) !", template), e);
        }
    }
}
