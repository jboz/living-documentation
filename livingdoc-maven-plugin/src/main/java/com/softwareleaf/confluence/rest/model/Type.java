package com.softwareleaf.confluence.rest.model;

/**
 * Represents the type of a piece of content.
 * <p>
 * Example
 * 
 * <pre>
 * {@literal
 *  {
 *      "id": "2345678",
 *      "type": "page",
 *      "space": {
 *          ...
 *      }
 *      ...
 *  }
 * }
 * </pre>
 *
 * @author Jonathon Hope
 */
public enum Type {
    ATTACHMENT, BLOGPOST, COMMENT, PAGE;

    /**
     * We override this here to
     */
    @Override
    public String toString() {
        return super.toString().toLowerCase();
    }
}
