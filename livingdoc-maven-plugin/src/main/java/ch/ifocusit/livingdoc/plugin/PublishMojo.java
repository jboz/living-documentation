/*
 * Living Documentation
 *
 * Copyright (C) 2023 Focus IT
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

import ch.ifocusit.livingdoc.plugin.baseMojo.AbstractAsciidoctorMojo;
import ch.ifocusit.livingdoc.plugin.domain.Publish;
import ch.ifocusit.livingdoc.plugin.publish.HtmlPostProcessor;
import ch.ifocusit.livingdoc.plugin.publish.PublishProvider;
import ch.ifocusit.livingdoc.plugin.publish.confluence.ConfluenceProvider;
import ch.ifocusit.livingdoc.plugin.publish.model.Page;
import org.apache.commons.io.FilenameUtils;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

/**
 * @author Julien Boz
 */
@Mojo(name = "publish", defaultPhase = LifecyclePhase.PACKAGE)
public class PublishMojo extends AbstractAsciidoctorMojo {

    @Parameter(property = "livingdoc.publish")
    private final Publish publish = new Publish();

    @Override
    public void executeMojo() throws MojoExecutionException {
        extractTemplatesFromJar();
        try {
            PublishProvider provider = new ConfluenceProvider(publish.getEndpoint(), publish.getUsername(), publish.getPassword());

            List<Page> pages = readHtmlPages();
            publish(provider, pages);
        } catch (Exception e) {
            throw new MojoExecutionException("Unexpected error", e);
        }
    }

    /**
     * @return html pages
     */
    private List<Page> readHtmlPages() throws IOException {

        List<Page> pages = new ArrayList<>();

        try (Stream<Path> paths = Files.walk(Paths.get(generatedDocsDirectory.getAbsolutePath()))) {
            paths.filter(path -> FilenameUtils.isExtension(path.getFileName().toString(), Format.adoc.name(), Format.asciidoc.name(), Format.html.name()))
                    .forEach(path -> {
                        try {
                            Map<String, String> attachmentCollector = new HashMap<>();

                            HtmlPostProcessor htmlProcessor = getPostProcessor();

                            Page page = new Page();
                            page.setSpaceKey(publish.getSpaceKey());
                            page.setParentId(publish.getAncestorId());
                            page.setTitle(htmlProcessor.getPageTitle(path));
                            page.setFile(path);
                            String content = htmlProcessor.process(path, attachmentCollector);
                            page.setContent(content);

                            attachmentCollector.forEach(page::addAttachment);

                            pages.add(page);
                        } catch (IOException e) {
                            throw new IllegalArgumentException("error reading file", e);
                        }
                    });
        }

        return pages;
    }

    private HtmlPostProcessor getPostProcessor() {
        return new HtmlPostProcessor(createAsciidoctor(), options());
    }

    private void publish(PublishProvider provider, List<Page> pages) {

        pages.stream().sorted().forEach(page -> {
            // check if parent exists
            // check if page exists
            if (provider.exists(page)) {
                getLog().info("Publish goal - update " + page);
                provider.update(page);
            } else {
                getLog().info("Publish goal - insert " + page);
                provider.insert(page);
            }
        });
    }
}
