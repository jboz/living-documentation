package com.softwareleaf.confluence.rest.model;

import com.google.gson.GsonBuilder;

/**
 * Represents the type of a piece of content.
 * <p>
 * Example
 * 
 * <pre>
 * {@literal
 *  {
 *      ...
 *      "space": {
 *          "key": "KEY"
 *      }
 *      ...
 *  }
 * }
 * </pre>
 *
 * @author Jonathon Hope
 */
public class Space {
    /**
     * The space key. For example: {@literal "DEV"}. Space keys may only consist of ASCII letters or numbers
     * {@literal (A-Z, a-z, 0-9)} and no empty spaces, special characters or underscore are allowed in the key
     */
    private String key;
    /**
     * The space name. For example: {@literal "Development"}.
     */
    private String name;

    /**
     * Default constructor.
     */
    public Space() {
    }

    /**
     * Constructor.
     *
     * @param key
     *            the {@code key} of the space. Space keys may only consist of ASCII letters or numbers {@literal (A-Z, a-z, 0-9)}
     *            and no empty spaces, special characters or underscore are allowed in the key
     */
    public Space(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return new GsonBuilder().disableHtmlEscaping().create().toJson(this);
    }

    // equals and hashcode

    @Override
    public int hashCode() {
        int result = 17;
        result = 31 * result + key.hashCode();
        result = 31 * result + name.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof Space))
            return false;
        Space that = (Space) obj;
        return ((name == null) ? that.name == null : that.name.equals(this.name))
                && ((key == null) ? that.key == null : that.key.equals(this.key));
    }

}
