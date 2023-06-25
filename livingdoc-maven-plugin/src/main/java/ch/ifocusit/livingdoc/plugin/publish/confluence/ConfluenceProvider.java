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
package ch.ifocusit.livingdoc.plugin.publish.confluence;

import ch.ifocusit.livingdoc.plugin.publish.PublishProvider;
import ch.ifocusit.livingdoc.plugin.publish.confluence.client.ConfluencePage;
import ch.ifocusit.livingdoc.plugin.publish.confluence.client.ConfluenceRestClient;
import ch.ifocusit.livingdoc.plugin.publish.confluence.client.NotFoundException;
import ch.ifocusit.livingdoc.plugin.publish.model.Page;

import static ch.ifocusit.livingdoc.plugin.utils.InputStreamUtils.fileInputStream;

/**
 * @author Julien Boz
 */
public class ConfluenceProvider implements PublishProvider {

    final ConfluenceRestClient client;

    public ConfluenceProvider(String endpoint, String username, String password) {
        client = new ConfluenceRestClient(endpoint, username, password);
    }

    @Override
    public boolean exists(final Page page) {
        try {
            client.getPageByTitle(page.getSpaceKey(), page.getTitle());
            return true;
        } catch (NotFoundException e) {
            return false;
        }
    }

    @Override
    public void update(final Page page) {
        String contentId = client.getPageByTitle(page.getSpaceKey(), page.getTitle());
        ConfluencePage existingPage = client.getPageWithContentAndVersionById(contentId);
        String oldContent = existingPage.getContent();

        // TODO check why content even upload
        if (!oldContent.equals(page.getContent())) {
            client.updatePage(contentId, page.getParentId(), page.getTitle(), page.getContent(), existingPage.getVersion() + 1);

            // TODO update attachement if possible
            // TODO remove attachement not present
            // remove all attachement
            client.getAttachments(contentId).forEach(attachment -> client.deleteAttachment(attachment.getId()));
            // add attachements
            page.getAttachments().forEach(attachement ->
                    client.addAttachment(contentId, attachement.getName(), fileInputStream(attachement.getFile()))
            );
        } else {
            // TODO log INFO
            System.out.println("Page with title=" + page.getTitle() + " did not change.");
        }
    }

    @Override
    public void insert(final Page page) {
        // post page to confluence.
        String contentId = client.addPageUnderAncestor(page.getSpaceKey(), page.getParentId(), page.getTitle(), page.getContent());

        // add attachements
        page.getAttachments().forEach(attachement ->
                client.addAttachment(contentId, attachement.getName(), fileInputStream(attachement.getFile()))
        );
    }

}
