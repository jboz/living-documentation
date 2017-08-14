package com.softwareleaf.confluence.rest;

import com.softwareleaf.confluence.rest.model.*;
import retrofit.Callback;
import retrofit.http.Body;
import retrofit.http.*;

import java.util.Map;

/**
 * Specifies the API paths that are so far supported with this confluence client.
 *
 * @author Jonathon Hope
 */
public interface ConfluenceAPI {

    /**
     * Fetch a results object containing a paginated list of content.
     *
     * @return an instance of {@code getContentResults} wrapping the list of {@code Content} instances obtained from the API call.
     */
    @GET("/rest/api/content")
    ContentResultList getContentResults();

    /**
     * Perform a search for content, by space key and title.
     *
     * @param key
     *            the space key to search under.
     * @param title
     *            the title of the piece of content to search for.
     * @return an instance of {@code getContentResults} wrapping the list of {@code Content} instances obtained from the API call.
     */
    @GET("/rest/api/content")
    ContentResultList getContentBySpaceKeyAndTitle(final @Query("key") String key, final @Query("title") String title);

    /**
     * Fetch the children for a given {@code Content} identified by the {@code parentId}.
     *
     * @param parentId
     *            the {@code id} of the parent {@code Content}.
     * @param type
     *            the {@code Type} of the {@code Content}.
     * @param params
     *            the query parameters mapping.
     * @return a list of all child content, matching {@code params} and {@code parentId}.
     */
    @GET("/rest/api/content/{id}/child/{type}")
    ContentResultList getChildren(final @Path("id") String parentId, final @Path("type") String type,
            final @QueryMap Map<String, String> params);

    /**
     * GET Content
     *
     * @param id
     *            the id of the page or blog post to fetch.
     * @return the Content instance representing the JSON response.
     */
    @GET("/rest/api/content/{id}" + QueryParams.EXPAND_BODY_STORAGE)
    Content getContentById(final @Path("id") String id);

    /**
     * POST Conversion request. Used for converting between storage formats.
     *
     * @param storage
     *            the storage instance to convert.
     * @param convertToFormat
     *            the representation to convert to.
     * @return an instance of {@code Storage} that contains the result of the conversion request.
     */
    @POST("/rest/api/contentbody/convert/{to}")
    Storage postContentConversion(final @Body Storage storage, final @Path("to") String convertToFormat);

    /**
     * POST Content.
     *
     * @param content
     *            the piece of {@code Content} to be included in the body of the request.
     * @return The {@code Content} reference updated (with id) by confluence.
     */
    @POST("/rest/api/content")
    Content postContent(final @Body Content content);

    @PUT("/rest/api/content/{id}")
    Content putContent(final @Path("id") String id, final @Body Content content);

    /**
     * Same as {@link #postContent} but clients can provide a callback with success and failure hooks.
     *
     * @param content
     *            the piece of Content to be included in the body of the request.
     * @param callback
     *            this handle provides a means of inquiring about the success or failure of the invocation.
     */
    @POST("/rest/api/content")
    void postContentWithCallback(final @Body Content content, final Callback<Content> callback);

    /**
     * DELETE Content
     * <p>
     * Trashes or purges a piece of Content, based on its {@literal ContentType} and {@literal ContentStatus}. There are three
     * cases:
     * <ul>
     * <li>If the content is trashable and its status is {@literal ContentStatus#CURRENT}, it will be trashed.</li>
     * <li>If the content is trashable, its status is {@literal ContentStatus#TRASHED} and the "status" query parameter in the
     * request is "trashed", the content will be purged from the trash and deleted permanently.</li>
     * <li>If the content is not trashable it will be deleted permanently without being trashed.</li>
     * </ul>
     *
     * @param id
     *            the id of the page of blog post to be deleted.
     * @return a reference to the message holder, indicating the status of the request.
     */
    @DELETE("/rest/api/content/{id}")
    NoContent deleteContentById(final @Path("id") String id);

    /**
     * Obtain a result list of available spaces.
     *
     * @return an instance of {@code SpaceResultList} that can be used to obtain a list of spaces available on confluence.
     */
    @GET("/rest/api/space")
    SpaceResultList getSpaces();

    /**
     * Creates a new Confluence {@code Space} using {@code key} and {@code name} of the given {@code space}.
     *
     * @param space
     *            the {@code Space} to create.
     * @return the {@code Space} as a confirmation returned by Confluence REST API.
     */
    @POST("/rest/api/space")
    Space createSpace(final @Body Space space);

    /**
     * Creates a new private Space, viewable only by the Confluence User account used by this {@code ConfluenceClient}.
     *
     * @param space
     *            the {@code Space} to create.
     * @return the {@code Space} as a confirmation returned by Confluence REST API.
     */
    @POST("/rest/api/space/_private")
    Space createPrivateSpace(final @Body Space space);

    /**
     * Fetch all content from a confluence space.
     *
     * @param spaceKey
     *            the key that identifies the target Space.
     * @param params
     *            the query parameters.
     * @return a list of all content in the given Space identified by {@code spaceKey}.
     */
    @GET("/rest/api/space/{spaceKey}/content/page")
    ContentResultList getAllSpaceContent(final @Path("spaceKey") String spaceKey, final @QueryMap Map<String, String> params);

    /**
     * Obtain paginated results of root content available from a given space.
     *
     * @param spaceKey
     *            the space key of the space to search.
     * @param contentType
     *            the type of content to return.
     * @return a wrapper model around the {@link ContentResultList} resulting from this call.
     */
    @GET("/rest/api/space/{spaceKey}/content/{type}")
    ContentResultList getRootContentBySpaceKey(final @Path("spaceKey") String spaceKey, final @Path("type") String contentType);

}
