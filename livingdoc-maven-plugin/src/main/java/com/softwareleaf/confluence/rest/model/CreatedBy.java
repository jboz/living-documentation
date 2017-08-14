/**
 * Copyright(C) 2015 <a href="mailto:Jonathon.a.hope@gmail.com" >Jonathon Hope</a>
 */

package com.softwareleaf.confluence.rest.model;

/**
 * Represents the Author details for a piece of {@code Content}.
 *
 * @author Jonathon Hope
 */
public class CreatedBy {

    private String username;
    private String displayName;
    private String type;

    public CreatedBy() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
