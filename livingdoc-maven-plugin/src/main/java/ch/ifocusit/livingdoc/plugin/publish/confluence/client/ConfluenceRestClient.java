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

import static org.apache.commons.lang3.StringUtils.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.function.Function;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.impl.auth.BasicScheme;
import org.apache.http.impl.client.BasicAuthCache;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.protocol.HttpContext;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ch.ifocusit.livingdoc.plugin.domain.Publish.Header;

/**
 * @author Alain Sahli
 * @author Christian Stettler
 * @author Julien Boz
 */
public class ConfluenceRestClient implements ConfluenceClient {

    private final CloseableHttpClient httpClient;
    private final HttpRequestFactory httpRequestFactory;

    private final String rootConfluenceUrl;
    private final String username;
    private final String password;
    private final Header[] headers;
    private final String authorizationToken;

    private final ObjectMapper objectMapper = new ObjectMapper();

    public ConfluenceRestClient(String rootConfluenceUrl, String username, String password,
            Header[] headers, String authorizationToken) {

        this.rootConfluenceUrl = rootConfluenceUrl;
        this.httpClient = httpClient();
        this.username = username;
        this.password = password;
        this.headers = headers;
        this.authorizationToken = authorizationToken;

        this.httpRequestFactory = new HttpRequestFactory(rootConfluenceUrl);
        configureObjectMapper();
    }

    private static CloseableHttpClient httpClient() {
        RequestConfig requestConfig = RequestConfig.custom()
                .setConnectionRequestTimeout(20 * 1000)
                .setConnectTimeout(20 * 1000)
                .build();

        return HttpClients.custom()
                .setDefaultRequestConfig(requestConfig)
                .build();
    }

    private void configureObjectMapper() {
        this.objectMapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
    }

    @Override
    public String addPageUnderAncestor(String spaceKey, String ancestorId, String title, String content) {
        HttpPost addPageUnderSpaceRequest = this.httpRequestFactory.addPageUnderAncestorRequest(spaceKey, ancestorId,
                title, content);

        return sendRequestAndFailIfNot20x(addPageUnderSpaceRequest,
                (response) -> extractIdFromJsonNode(parseJsonResponse(response)));
    }

    @Override
    public void updatePage(String contentId, String ancestorId, String title, String content, int newVersion) {
        HttpPut updatePageRequest = this.httpRequestFactory.updatePageRequest(contentId, ancestorId, title, content,
                newVersion);
        sendRequestAndFailIfNot20x(updatePageRequest);
    }

    @Override
    public void deletePage(String contentId) {
        HttpDelete deletePageRequest = this.httpRequestFactory.deletePageRequest(contentId);
        sendRequestAndFailIfNot20x(deletePageRequest);
    }

    @Override
    public String getPageByTitle(String spaceKey, String title) throws NotFoundException, MultipleResultsException {
        HttpGet pageByTitleRequest = this.httpRequestFactory.getPageByTitleRequest(spaceKey, title);

        return sendRequestAndFailIfNot20x(pageByTitleRequest, (response) -> {
            JsonNode jsonNode = parseJsonResponse(response);

            int numberOfResults = jsonNode.get("size").asInt();
            if (numberOfResults == 0) {
                throw new NotFoundException();
            }

            if (numberOfResults > 1) {
                throw new MultipleResultsException();
            }

            return extractIdFromJsonNode(jsonNode.withArray("results").elements().next());
        });
    }

    @Override
    public void addAttachment(String contentId, String attachmentFileName, InputStream attachmentContent) {
        HttpPost addAttachmentRequest = this.httpRequestFactory.addAttachmentRequest(contentId, attachmentFileName,
                attachmentContent);
        sendRequestAndFailIfNot20x(addAttachmentRequest, (response) -> {
            closeInputStream(attachmentContent);

            return null;
        });
    }

    @Override
    public void updateAttachmentContent(String contentId, String attachmentId, InputStream attachmentContent) {
        HttpPost updateAttachmentContentRequest = this.httpRequestFactory.updateAttachmentContentRequest(contentId,
                attachmentId, attachmentContent);
        sendRequestAndFailIfNot20x(updateAttachmentContentRequest, (response) -> {
            closeInputStream(attachmentContent);

            return null;
        });
    }

    @Override
    public void deleteAttachment(String attachmentId) {
        HttpDelete deleteAttachmentRequest = this.httpRequestFactory.deleteAttachmentRequest(attachmentId);
        sendRequestAndFailIfNot20x(deleteAttachmentRequest);
    }

    @Override
    public ConfluenceAttachment getAttachmentByFileName(String contentId, String attachmentFileName)
            throws NotFoundException, MultipleResultsException {
        HttpGet attachmentByFileNameRequest = this.httpRequestFactory.getAttachmentByFileNameRequest(contentId,
                attachmentFileName, "version");

        return sendRequestAndFailIfNot20x(attachmentByFileNameRequest, (response) -> {
            JsonNode jsonNode = parseJsonResponse(response);

            int numberOfResults = jsonNode.get("size").asInt();
            if (numberOfResults == 0) {
                throw new NotFoundException();
            }

            if (numberOfResults > 1) {
                throw new MultipleResultsException();
            }

            return extractConfluenceAttachment(jsonNode.withArray("results").elements().next());
        });
    }

    @Override
    public ConfluencePage getPageWithContentAndVersionById(String contentId) {
        HttpGet pageByIdRequest = this.httpRequestFactory.getPageByIdRequest(contentId, "body.storage,version");

        return sendRequestAndFailIfNot20x(pageByIdRequest,
                (response) -> extractConfluencePageWithContent(parseJsonResponse(response)));
    }

    @Override
    public InputStream getAttachmentContent(String relativeDownloadLink) {
        HttpGet getAttachmentContentRequest = this.httpRequestFactory.getAttachmentContentRequest(relativeDownloadLink);

        return sendRequestAndFailIfNot20x(getAttachmentContentRequest, (response) -> {
            try {
                return copyInputStream(response);
            } catch (IOException e) {
                throw new RuntimeException("Could not read attachment content", e);
            }
        });
    }

    private static ByteArrayInputStream copyInputStream(HttpResponse response) throws IOException {
        byte[] buffer = new byte[1024];
        int read;
        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        while ((read = response.getEntity().getContent().read(buffer)) != -1) {
            byteArrayOutputStream.write(buffer, 0, read);
        }

        return new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
    }

    private JsonNode parseJsonResponse(HttpResponse response) {
        try {
            return this.objectMapper.readTree(response.getEntity().getContent());
        } catch (IOException e) {
            throw new RuntimeException("Could not read JSON response", e);
        }
    }

    private void sendRequestAndFailIfNot20x(HttpRequestBase httpRequest) {
        sendRequestAndFailIfNot20x(httpRequest, (response) -> null);
    }

    private <T> T sendRequestAndFailIfNot20x(HttpRequestBase request, Function<HttpResponse, T> responseHandler) {
        return sendRequest(request, (response) -> {
            StatusLine statusLine = response.getStatusLine();
            if (statusLine.getStatusCode() < 200 || statusLine.getStatusCode() > 206) {
                throw new RequestFailedException(request, response);
            }

            return responseHandler.apply(response);
        });
    }

    <T> T sendRequest(HttpRequestBase httpRequest, Function<HttpResponse, T> responseHandler) {
        CloseableHttpResponse response = null;

        try {
            if (isNotBlank(this.username) && this.password != null) {
                final String encodedCredentials = "Basic "
                        + Base64.getEncoder().encodeToString((username + ":" + password).getBytes());
                httpRequest.addHeader(HttpHeaders.AUTHORIZATION, encodedCredentials);
            } else if (authorizationToken != null) {
                httpRequest.addHeader(HttpHeaders.AUTHORIZATION, "Bearer " + authorizationToken);
            }
            if (headers != null) {
                for (Header header : headers) {
                    httpRequest.addHeader(header.getName(), header.getValue());
                }
            }
            httpRequest.addHeader(HttpHeaders.ACCEPT, "application/json");

            response = this.httpClient.execute(httpRequest, httpContext());

            return responseHandler.apply(response);
        } catch (IOException e) {
            throw new RuntimeException("Request could not be sent" + httpRequest, e);
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
            } catch (IOException ignored) {
            }
        }
    }

    private HttpContext httpContext() {
        if (isNotBlank(this.username) && this.password != null) {
            HttpHost httpHost = HttpHost.create(this.rootConfluenceUrl);
            AuthScope authScope = new AuthScope(httpHost);
            BasicCredentialsProvider basicCredentialsProvider = new BasicCredentialsProvider();
            UsernamePasswordCredentials credentials = new UsernamePasswordCredentials(this.username, this.password);
            basicCredentialsProvider.setCredentials(authScope, credentials);

            BasicAuthCache basicAuthCache = new BasicAuthCache();
            basicAuthCache.put(httpHost, new BasicScheme());

            HttpClientContext httpClientContext = HttpClientContext.create();
            httpClientContext.setCredentialsProvider(basicCredentialsProvider);
            httpClientContext.setAuthCache(basicAuthCache);

            return httpClientContext;
        } else {
            return null;
        }
    }

    @Override
    public List<ConfluencePage> getChildPages(String contentId) {
        int start = 0;
        int limit = 25;

        ArrayList<ConfluencePage> childPages = new ArrayList<>();
        boolean fetchMore = true;
        while (fetchMore) {
            List<ConfluencePage> nextChildPages = getNextChildPages(contentId, limit, start);
            childPages.addAll(nextChildPages);

            start++;
            fetchMore = nextChildPages.size() == limit;
        }

        return childPages;
    }

    @Override
    public List<ConfluenceAttachment> getAttachments(String contentId) {
        int start = 0;
        int limit = 25;

        ArrayList<ConfluenceAttachment> attachments = new ArrayList<>();
        boolean fetchMore = true;
        while (fetchMore) {
            List<ConfluenceAttachment> nextAttachments = getNextAttachments(contentId, limit, start);
            attachments.addAll(nextAttachments);

            start++;
            fetchMore = nextAttachments.size() == limit;
        }

        return attachments;
    }

    private List<ConfluencePage> getNextChildPages(String contentId, int limit, int start) {
        List<ConfluencePage> pages = new ArrayList<>(limit);
        HttpGet getChildPagesByIdRequest = this.httpRequestFactory.getChildPagesByIdRequest(contentId, limit, start,
                "version");

        return sendRequestAndFailIfNot20x(getChildPagesByIdRequest, (response) -> {
            JsonNode jsonNode = parseJsonResponse(response);
            jsonNode.withArray("results").forEach((page) -> pages.add(extractConfluencePageWithoutContent(page)));

            return pages;
        });
    }

    private List<ConfluenceAttachment> getNextAttachments(String contentId, int limit, int start) {
        List<ConfluenceAttachment> attachments = new ArrayList<>(limit);
        HttpGet getAttachmentsRequest = this.httpRequestFactory.getAttachmentsRequest(contentId, limit, start,
                "version");

        return sendRequestAndFailIfNot20x(getAttachmentsRequest, (response) -> {
            JsonNode jsonNode = parseJsonResponse(response);
            jsonNode.withArray("results")
                    .forEach(attachment -> attachments.add(extractConfluenceAttachment(attachment)));

            return attachments;
        });
    }

    @Override
    public String getSpaceContentId(String spaceKey) {
        HttpGet getSpaceContentIdRequest = this.httpRequestFactory.getSpaceContentIdRequest(spaceKey);

        return sendRequestAndFailIfNot20x(getSpaceContentIdRequest,
                (response) -> extractIdFromJsonNode(parseJsonResponse(response)));
    }

    @Override
    public void setPropertyByKey(String contentId, String key, String value) {
        HttpPost setPropertyByKeyRequest = this.httpRequestFactory.setPropertyByKeyRequest(contentId, key, value);
        sendRequestAndFailIfNot20x(setPropertyByKeyRequest);
    }

    @Override
    public String getPropertyByKey(String contentId, String key) {
        HttpGet propertyByKeyRequest = this.httpRequestFactory.getPropertyByKeyRequest(contentId, key);

        return sendRequest(propertyByKeyRequest, (response) -> {
            if (response.getStatusLine().getStatusCode() == 200) {
                return extractPropertyValueFromJsonNode(parseJsonResponse(response));
            } else {
                return null;
            }
        });
    }

    @Override
    public void deletePropertyByKey(String contentId, String key) {
        HttpDelete deletePropertyByKeyRequest = this.httpRequestFactory.deletePropertyByKeyRequest(contentId, key);
        sendRequest(deletePropertyByKeyRequest, (ignored) -> null);
    }

    private static ConfluencePage extractConfluencePageWithContent(JsonNode jsonNode) {
        String id = extractIdFromJsonNode(jsonNode);
        String title = extractTitleFromJsonNode(jsonNode);
        String content = jsonNode.path("body").path("storage").get("value").asText();
        int version = extractVersionFromJsonNode(jsonNode);

        return new ConfluencePage(id, title, content, version);
    }

    private static ConfluencePage extractConfluencePageWithoutContent(JsonNode jsonNode) {
        String id = extractIdFromJsonNode(jsonNode);
        String title = extractTitleFromJsonNode(jsonNode);
        int version = extractVersionFromJsonNode(jsonNode);

        return new ConfluencePage(id, title, version);
    }

    private static ConfluenceAttachment extractConfluenceAttachment(JsonNode jsonNode) {
        String id = extractIdFromJsonNode(jsonNode);
        String title = extractTitleFromJsonNode(jsonNode);
        int version = extractVersionFromJsonNode(jsonNode);
        String relativeDownloadLink = jsonNode.path("_links").get("download").asText();

        return new ConfluenceAttachment(id, title, relativeDownloadLink, version);
    }

    private static String extractIdFromJsonNode(JsonNode jsonNode) {
        return jsonNode.get("id").asText();
    }

    private static String extractTitleFromJsonNode(JsonNode jsonNode) {
        return jsonNode.get("title").asText();
    }

    private static int extractVersionFromJsonNode(JsonNode jsonNode) {
        return jsonNode.path("version").get("number").asInt();
    }

    private static String extractPropertyValueFromJsonNode(JsonNode jsonNode) {
        return jsonNode.path("value").asText();
    }

    private static void closeInputStream(InputStream inputStream) {
        try {
            inputStream.close();
        } catch (IOException ignored) {
        }
    }

}
