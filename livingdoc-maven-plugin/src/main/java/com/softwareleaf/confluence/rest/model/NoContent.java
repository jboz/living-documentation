package com.softwareleaf.confluence.rest.model;

import com.google.gson.GsonBuilder;

/**
 * Represents the JSON response when Content is not found.
 *
 * @author Jonathon Hope
 */
public class NoContent {
    private int statusCode;
    private String message;

    public NoContent() {
    }

    public int getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(int statusCode) {
        this.statusCode = statusCode;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return new GsonBuilder().disableHtmlEscaping().create().toJson(this);
    }
}
