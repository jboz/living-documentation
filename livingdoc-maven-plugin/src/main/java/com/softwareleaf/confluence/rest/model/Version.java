/**
 * Copyright(C) 2015 <a href="mailto:Jonathon.a.hope@gmail.com" >Jonathon Hope</a>
 */

package com.softwareleaf.confluence.rest.model;

/**
 * Represents Version information about a piece of {@code Content}.
 *
 * @author Jonathon Hope
 * @author Julien Boz
 */
public class Version {

    /**
     * @see CreatedBy
     */
    private CreatedBy by;
    /**
     * A {@code Date} in format {@literal "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"}.
     */
    private String when;
    /**
     * An optional message.
     */
    private String message;

    private Long number;

    public Version() {
    }

    public CreatedBy getBy() {
        return by;
    }

    public void setBy(CreatedBy by) {
        this.by = by;
    }

    public String getWhen() {
        return when;
    }

    public void setWhen(String when) {
        this.when = when;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getNumber() {
        return number;
    }

    public void setNumber(Long number) {
        this.number = number;
    }

    // equals and hashcode

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        Version version = (Version) o;

        if (by != null ? !by.equals(version.by) : version.by != null)
            return false;
        if (!when.equals(version.when))
            return false;
        return !(message != null ? !message.equals(version.message) : version.message != null);

    }

    @Override
    public int hashCode() {
        int result = by != null ? by.hashCode() : 0;
        result = 31 * result + when.hashCode();
        result = 31 * result + (message != null ? message.hashCode() : 0);
        return result;
    }

}
