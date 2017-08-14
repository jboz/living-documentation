package com.softwareleaf.confluence.rest.model;

import com.google.gson.GsonBuilder;

import java.util.Objects;

/**
 * Represents a piece of content.
 * <p>
 * <p>
 * Example
 * 
 * <pre>
 * {@literal
 *      {
 *          "id": "22217244",
 *          "type": "page",
 *          "space": {
 *              "key": "TST"
 *          },
 *          "title": "A title",
 *          "body": {
 *              "storage": {
 *                  "value": "<p>This is a new page</p>",
 *                  "representation": "storage"
 *              }
 *          }
 *      }
 * }
 * </pre>
 *
 * @author Jonathon Hope
 */
public class Content {
    /**
     * The id of the page or blog post.
     */
    private String id;
    /**
     * The type of content: blog post or page.
     */
    private String type;
    /**
     * The ancestors of this blog post or page.
     */
    private Parent[] ancestors;
    /**
     * The space object holds the space key, that is used to identify the location of the piece of content.
     */
    private Space space;
    /**
     * The title of the page or blog post.
     */
    private String title;
    /**
     * The body object holds the stored data of the page or blog post.
     */
    private Body body;
    /**
     * Versioning information.
     */
    private Version version;
    /**
     * History information.
     */
    private History history;

    /**
     * Default Constructor.
     */
    public Content() {
    }

    /**
     * Constructor.
     *
     * @param id
     *            the id of the piece of content.
     * @param type
     *            the type of piece of content.
     * @param space
     *            the space object for the piece of content.
     * @param title
     *            the title of the piece of content.
     * @param body
     *            the body of the piece of content.
     */
    public Content(String id, String type, Space space, String title, Body body) {
        this.id = id;
        this.type = type;
        this.title = title;
        this.space = space;
        this.body = body;
    }

    // getters and setters

    public String getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type.toString();
    }

    public Parent[] getAncestors() {
        return ancestors;
    }

    public void setAncestors(Parent[] ancestors) {
        this.ancestors = ancestors;
    }

    public Space getSpace() {
        return space;
    }

    public void setSpace(Space space) {
        this.space = space;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setType(String type) {
        this.type = type;
    }

    public Version getVersion() {
        return version;
    }

    public void setVersion(Version version) {
        this.version = version;
    }

    public History getHistory() {
        return history;
    }

    public void setHistory(History history) {
        this.history = history;
    }

    @Override
    public String toString() {
        return new GsonBuilder().disableHtmlEscaping().create().toJson(this);
    }

    // equals and hashcode

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !(o instanceof Content)) {
            return false;
        }
        Content content = (Content) o;
        return Objects.equals(id, content.id) && Objects.equals(type, content.type) && Objects.equals(ancestors, content.ancestors)
                && Objects.equals(space, content.space) && Objects.equals(title, content.title)
                && Objects.equals(body, content.body) && Objects.equals(version, content.version)
                && Objects.equals(history, content.history);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, type, ancestors, space, title, body, version, history);
    }
}
