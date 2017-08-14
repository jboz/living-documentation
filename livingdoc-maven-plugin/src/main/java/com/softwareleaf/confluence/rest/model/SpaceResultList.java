package com.softwareleaf.confluence.rest.model;

import com.google.gson.annotations.SerializedName;

/**
 * Represents a result set of confluence spaces.
 *
 * @author Jonathon Hope
 * @see <a href="https://docs.atlassian.com/atlassian-confluence/REST/latest/#d3e841">Space REST API</a>
 */
public class SpaceResultList {
    @SerializedName("results")
    private Space[] spaces;

    /**
     * Constructor.
     *
     * @param spaces
     *            the array of {@code Space} instances.
     */
    public SpaceResultList(Space[] spaces) {
        this.spaces = spaces;
    }

    // getter and setter

    public Space[] getSpaces() {
        return spaces;
    }

    public void setSpaces(Space[] spaces) {
        this.spaces = spaces;
    }
}
