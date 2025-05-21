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
package ch.ifocusit.livingdoc.plugin.publish.confluence.client;

import static ch.ifocusit.livingdoc.plugin.publish.confluence.client.HttpRequestFactory.PagePayloadBuilder.*;
import static ch.ifocusit.livingdoc.plugin.utils.AssertUtils.*;
import static org.apache.commons.lang3.StringUtils.*;
import static org.apache.http.entity.ContentType.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.entity.BasicHttpEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.message.BasicHeader;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.ifocusit.livingdoc.plugin.publish.confluence.client.model.Ancestor;
import ch.ifocusit.livingdoc.plugin.publish.confluence.client.model.Body;
import ch.ifocusit.livingdoc.plugin.publish.confluence.client.model.PagePayload;
import ch.ifocusit.livingdoc.plugin.publish.confluence.client.model.PropertyPayload;
import ch.ifocusit.livingdoc.plugin.publish.confluence.client.model.Space;
import ch.ifocusit.livingdoc.plugin.publish.confluence.client.model.Storage;
import ch.ifocusit.livingdoc.plugin.publish.confluence.client.model.Version;

/**
 * @author Alain Sahli
 * @author Julien Boz
 */
class HttpRequestFactory {

    private final static Header APPLICATION_JSON_UTF8_HEADER = new BasicHeader("Content-Type",
            "application/json;charset=utf-8");
    private static final String REST_API_CONTEXT = "/rest/api";

    private final String rootConfluenceUrl;
    private final String confluenceRestApiEndpoint;

    HttpRequestFactory(String rootConfluenceUrl) {
        assertMandatoryParameter(isNotBlank(rootConfluenceUrl), "rootConfluenceUrl");

        this.rootConfluenceUrl = rootConfluenceUrl;
        this.confluenceRestApiEndpoint = rootConfluenceUrl + REST_API_CONTEXT;
    }

    HttpPost addPageUnderAncestorRequest(String spaceKey, String ancestorId, String title, String content) {
        assertMandatoryParameter(isNotBlank(spaceKey), "spaceKey");
        assertMandatoryParameter(isNotBlank(ancestorId), "ancestorId");
        assertMandatoryParameter(isNotBlank(title), "title");

        PagePayload pagePayload = pagePayloadBuilder()
                .spaceKey(spaceKey)
                .ancestorId(ancestorId)
                .title(title)
                .content(content)
                .build();

        return addPageHttpPost(this.confluenceRestApiEndpoint, pagePayload);
    }

    HttpPut updatePageRequest(String contentId, String ancestorId, String title, String content, int newVersion) {
        assertMandatoryParameter(isNotBlank(contentId), "contentId");
        assertMandatoryParameter(isNotBlank(ancestorId), "ancestorId");
        assertMandatoryParameter(isNotBlank(title), "title");

        PagePayload pagePayload = pagePayloadBuilder()
                .ancestorId(ancestorId)
                .title(title)
                .content(content)
                .version(newVersion)
                .build();

        HttpPut updatePageRequest = new HttpPut(this.confluenceRestApiEndpoint + "/content/" + contentId);
        updatePageRequest.setEntity(httpEntityWithJsonPayload(pagePayload));
        updatePageRequest.addHeader(APPLICATION_JSON_UTF8_HEADER);

        return updatePageRequest;
    }

    HttpDelete deletePageRequest(String contentId) {
        assertMandatoryParameter(isNotBlank(contentId), "contentId");

        return new HttpDelete(this.confluenceRestApiEndpoint + "/content/" + contentId);
    }

    HttpPost addAttachmentRequest(String contentId, String attachmentFileName, InputStream attachmentContent) {
        assertMandatoryParameter(isNotBlank(contentId), "contentId");
        assertMandatoryParameter(isNotBlank(attachmentFileName), "attachmentFileName");
        assertMandatoryParameter(attachmentContent != null, "attachmentContent");

        HttpPost attachmentPostRequest = new HttpPost(
                this.confluenceRestApiEndpoint + "/content/" + contentId + "/child/attachment");
        attachmentPostRequest.addHeader(new BasicHeader("X-Atlassian-Token", "no-check"));

        HttpEntity multipartEntity = multipartEntity(attachmentFileName, attachmentContent);
        attachmentPostRequest.setEntity(multipartEntity);

        return attachmentPostRequest;
    }

    HttpPost updateAttachmentContentRequest(String contentId, String attachmentId, InputStream attachmentContent) {
        assertMandatoryParameter(isNotBlank(contentId), "contentId");
        assertMandatoryParameter(isNotBlank(attachmentId), "attachmentId");
        assertMandatoryParameter(attachmentContent != null, "attachmentContent");

        HttpPost attachmentPostRequest = new HttpPost(this.confluenceRestApiEndpoint + "/content/" + contentId
                + "/child/attachment/" + attachmentId + "/data");
        attachmentPostRequest.addHeader(new BasicHeader("X-Atlassian-Token", "no-check"));

        HttpEntity multipartEntity = multipartEntity(null, attachmentContent);
        attachmentPostRequest.setEntity(multipartEntity);

        return attachmentPostRequest;
    }

    HttpDelete deleteAttachmentRequest(String attachmentId) {
        assertMandatoryParameter(isNotBlank(attachmentId), "attachmentId");

        return new HttpDelete(this.confluenceRestApiEndpoint + "/content/" + attachmentId);
    }

    HttpGet getPageByTitleRequest(String spaceKey, String title) {
        assertMandatoryParameter(isNotBlank(spaceKey), "spaceKey");
        assertMandatoryParameter(isNotBlank(title), "title");

        String encodedTitle;
        encodedTitle = URLEncoder.encode(title, StandardCharsets.UTF_8);

        String searchQuery = this.confluenceRestApiEndpoint + "/content?spaceKey=" + spaceKey + "&title="
                + encodedTitle;

        return new HttpGet(searchQuery);
    }

    HttpGet getAttachmentByFileNameRequest(String contentId, String attachmentFileName, String expandOptions) {
        assertMandatoryParameter(isNotBlank(contentId), "contentId");
        assertMandatoryParameter(isNotBlank(attachmentFileName), "attachmentFileName");

        URIBuilder uriBuilder = new URIBuilder();
        uriBuilder.setPath(this.confluenceRestApiEndpoint + "/content/" + contentId + "/child/attachment");
        uriBuilder.addParameter("filename", attachmentFileName);

        if (isNotBlank(expandOptions)) {
            uriBuilder.addParameter("expand", expandOptions);
        }

        HttpGet getAttachmentByFileNameRequest;
        try {
            getAttachmentByFileNameRequest = new HttpGet(uriBuilder.build().toString());
        } catch (URISyntaxException e) {
            throw new RuntimeException("Invalid URL", e);
        }

        return getAttachmentByFileNameRequest;
    }

    HttpGet getPageByIdRequest(String contentId, final String expandOptions) {
        assertMandatoryParameter(isNotBlank(contentId), "contentId");

        return new HttpGet(this.confluenceRestApiEndpoint + "/content/" + contentId + "?expand=" + expandOptions);
    }

    HttpGet getChildPagesByIdRequest(String parentContentId, Integer limit, Integer start, String expandOptions) {
        assertMandatoryParameter(isNotBlank(parentContentId), "parentContentId");
        URIBuilder uriBuilder = new URIBuilder();
        uriBuilder.setPath(this.confluenceRestApiEndpoint + "/content/" + parentContentId + "/child/page");

        if (limit != null) {
            uriBuilder.addParameter("limit", limit.toString());
        }
        if (start != null) {
            uriBuilder.addParameter("start", start.toString());
        }
        if (isNotBlank(expandOptions)) {
            uriBuilder.addParameter("expand", expandOptions);
        }

        HttpGet getChildPagesByIdRequest;
        try {
            getChildPagesByIdRequest = new HttpGet(uriBuilder.build().toString());
        } catch (URISyntaxException e) {
            throw new RuntimeException("Invalid URL", e);
        }

        return getChildPagesByIdRequest;
    }

    public HttpGet getAttachmentsRequest(String contentId, Integer limit, Integer start, String expandOptions) {
        assertMandatoryParameter(isNotBlank(contentId), "contentId");
        try {
            URIBuilder uriBuilder = new URIBuilder(
                    this.confluenceRestApiEndpoint + "/content/" + contentId + "/child/attachment");

            if (limit != null) {
                uriBuilder.addParameter("limit", limit.toString());
            }
            if (start != null) {
                uriBuilder.addParameter("start", start.toString());
            }
            if (isNotBlank(expandOptions)) {
                uriBuilder.addParameter("expand", expandOptions);
            }
            return new HttpGet(uriBuilder.build().toString());
        } catch (URISyntaxException e) {
            throw new RuntimeException("Invalid URL", e);
        }
    }

    public HttpGet getAttachmentContentRequest(String relativeDownloadLink) {
        assertMandatoryParameter(isNotBlank(relativeDownloadLink), "relativeDownloadLink");

        return new HttpGet(this.rootConfluenceUrl + relativeDownloadLink);
    }

    public HttpGet getSpaceContentIdRequest(String spaceKey) {
        assertMandatoryParameter(isNotBlank(spaceKey), "spaceKey");

        return new HttpGet(this.confluenceRestApiEndpoint + "/space/" + spaceKey);
    }

    public HttpGet getPropertyByKeyRequest(String contentId, String key) {
        assertMandatoryParameter(isNotBlank(contentId), "contentId");
        assertMandatoryParameter(isNotBlank(key), "key");

        return new HttpGet(
                this.confluenceRestApiEndpoint + "/content/" + contentId + "/property/" + key + "?expand=value");
    }

    public HttpDelete deletePropertyByKeyRequest(String contentId, String key) {
        assertMandatoryParameter(isNotBlank(contentId), "contentId");
        assertMandatoryParameter(isNotBlank(key), "key");

        return new HttpDelete(this.confluenceRestApiEndpoint + "/content/" + contentId + "/property/" + key);
    }

    public HttpPost setPropertyByKeyRequest(String contentId, String key, String value) {
        assertMandatoryParameter(isNotBlank(contentId), "contentId");
        assertMandatoryParameter(isNotBlank(key), "key");
        assertMandatoryParameter(isNotBlank(value), "value");

        PropertyPayload propertyPayload = new PropertyPayload();
        propertyPayload.setKey(key);
        propertyPayload.setValue(value);

        HttpPost postRequest = new HttpPost(this.confluenceRestApiEndpoint + "/content/" + contentId + "/property");
        postRequest.setEntity(httpEntityWithJsonPayload(propertyPayload));
        postRequest.addHeader(APPLICATION_JSON_UTF8_HEADER);

        return postRequest;
    }

    private static HttpPost addPageHttpPost(String confluenceRestApiEndpoint, PagePayload pagePayload) {
        HttpPost postRequest = new HttpPost(confluenceRestApiEndpoint + "/content");
        postRequest.setEntity(httpEntityWithJsonPayload(pagePayload));
        postRequest.addHeader(APPLICATION_JSON_UTF8_HEADER);

        return postRequest;
    }

    private static BasicHttpEntity httpEntityWithJsonPayload(Object payload) {
        String jsonPayload = toJsonString(payload);
        BasicHttpEntity entity = new BasicHttpEntity();
        entity.setContent(new ByteArrayInputStream(jsonPayload.getBytes()));

        return entity;
    }

    private static String toJsonString(Object objectToConvert) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);

            return objectMapper.writeValueAsString(objectToConvert);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Error while converting object to JSON", e);
        }
    }

    private static HttpEntity multipartEntity(String attachmentFileName, InputStream attachmentContent) {
        MultipartEntityBuilder multipartEntityBuilder = MultipartEntityBuilder.create();
        multipartEntityBuilder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        multipartEntityBuilder.setCharset(StandardCharsets.UTF_8);

        InputStreamBody inputStreamBody;
        if (isNotBlank(attachmentFileName)) {
            inputStreamBody = new InputStreamBody(attachmentContent, APPLICATION_OCTET_STREAM, attachmentFileName);
        } else {
            inputStreamBody = new InputStreamBody(attachmentContent, APPLICATION_OCTET_STREAM);
        }

        multipartEntityBuilder.addPart("file", inputStreamBody);

        return multipartEntityBuilder.build();
    }

    static class PagePayloadBuilder {

        private String title;
        private String content;
        private String spaceKey;
        private String ancestorId;
        private Integer version;

        public PagePayloadBuilder title(String title) {
            this.title = title;

            return this;
        }

        public PagePayloadBuilder content(String content) {
            this.content = content;

            return this;
        }

        public PagePayloadBuilder spaceKey(String spaceKey) {
            this.spaceKey = spaceKey;

            return this;
        }

        public PagePayloadBuilder ancestorId(String ancestorId) {
            this.ancestorId = ancestorId;

            return this;
        }

        public PagePayloadBuilder version(Integer version) {
            this.version = version;

            return this;
        }

        private PagePayload build() {
            Storage storage = new Storage();
            storage.setValue(this.content);

            Body body = new Body();
            body.setStorage(storage);

            PagePayload pagePayload = new PagePayload();
            pagePayload.setBody(body);
            pagePayload.setTitle(this.title);

            if (isNotBlank(this.spaceKey)) {
                Space space = new Space();
                space.setKey(this.spaceKey);
                pagePayload.setSpace(space);
            }

            if (isNotBlank(this.ancestorId)) {
                Ancestor ancestor = new Ancestor();
                ancestor.setId(this.ancestorId);
                pagePayload.addAncestor(ancestor);
            }

            if (this.version != null) {
                Version versionContainer = new Version();
                versionContainer.setNumber(this.version);
                pagePayload.setVersion(versionContainer);
            }

            return pagePayload;
        }

        static PagePayloadBuilder pagePayloadBuilder() {
            return new PagePayloadBuilder();
        }

    }

}
