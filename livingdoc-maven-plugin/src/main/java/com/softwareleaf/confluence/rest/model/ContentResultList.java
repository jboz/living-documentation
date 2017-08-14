package com.softwareleaf.confluence.rest.model;

import com.google.gson.annotations.SerializedName;

/**
 * A representation of the results object returned by the GET {@literal /rest/api/content/} API call.
 *
 * @author Jonathon Hope
 */
public class ContentResultList {

    /**
     * Array holds results.
     */
    @SerializedName("results")
    private Content[] contents;
    /**
     * The start index.
     */
    private int start;
    /**
     * The limit provided.
     */
    private int limit;
    /**
     * The size of the collection returned.
     */
    private int size;

    public ContentResultList() {
    }

    public ContentResultList(Content[] contents) {
        this.contents = contents;
    }

    // getter and setter

    public Content[] getContents() {
        return contents;
    }

    public void setContents(Content[] contents) {
        this.contents = contents;
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }
}
