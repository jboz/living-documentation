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

import ch.ifocusit.livingdoc.plugin.confluence.ConfluencePublisher;
import ch.ifocusit.livingdoc.plugin.confluence.client.ConfluenceRestClient;
import ch.ifocusit.livingdoc.plugin.confluence.model.ConfluencePageMetadata;
import ch.ifocusit.livingdoc.plugin.confluence.model.ConfluencePublisherMetadata;
import ch.ifocusit.livingdoc.plugin.domain.Publish;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * @author Julien Boz
 */
@Mojo(name = "publish")
public class PublishMojo extends AbstractMojo {

    private Provider publishProvider = Provider.CONFLUENCE;
    private Publish publish = new Publish();

    private static CloseableHttpClient httpClient() {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(20 * 1000)
                .setConnectTimeout(20 * 1000)
                .build();

        return HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .build();
    }

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        switch (publishProvider) {
            default:
                try {
                    publishConfluence(readMetadata());
                } catch (IOException e) {
                    throw new MojoExecutionException("error reading asciidoc directory", e);
                }
        }
    }

    private ConfluencePublisherMetadata readMetadata() throws IOException {
        ConfluencePublisherMetadata confluencePublisherMetadata = new ConfluencePublisherMetadata();

        confluencePublisherMetadata.setSpaceKey(publish.getSpaceKey());
        confluencePublisherMetadata.setAncestorId(publish.getAncestorId());

        Files.walk(Paths.get(publish.getAsciidocFolder().getAbsolutePath()))
        .filter(path -> path.getFileName().endsWith(".html"))
        .forEach(path -> {
            ConfluencePageMetadata confluencePageMetadata = new ConfluencePageMetadata();
            confluencePageMetadata.setTitle(path.getFileName().toString());
            // TODO convert aasciidoc to html
            // TODO manage attachment
            confluencePageMetadata.setContentFilePath(path.toUri().toString());
            confluencePublisherMetadata.addPage(confluencePageMetadata);
        });

        return confluencePublisherMetadata;
    }

    private void publishConfluence(ConfluencePublisherMetadata confluencePublisherMetadata) {


        ConfluenceRestClient confluenceRestClient = new ConfluenceRestClient(publish.getEndpoint(), httpClient(), publish.getUsername(), publish.getPassword());
        ConfluencePublisher confluencePublisher = new ConfluencePublisher(confluencePublisherMetadata, confluenceRestClient, publish.getAsciidocFolder().getAbsolutePath());
        confluencePublisher.publish();
    }

    enum Provider {
        CONFLUENCE;
    }
}
