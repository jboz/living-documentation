package ch.ifocusit.livingdoc.plugin.publish.model;

public class Page {

    String spaceKey;
    String parentId;
    String content;
    String title;

    public String getSpaceKey() {
        return spaceKey;
    }

    public void setSpaceKey(final String spaceKey) {
        this.spaceKey = spaceKey;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(final String parentId) {
        this.parentId = parentId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(final String content) {
        this.content = content;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(final String title) {
        this.title = title;
    }
}
