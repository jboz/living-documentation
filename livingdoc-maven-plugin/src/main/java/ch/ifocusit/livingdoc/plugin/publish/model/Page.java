package ch.ifocusit.livingdoc.plugin.publish.model;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Page {

    String spaceKey;
    String parentId;
    String content;
    Path file;
    String title;

    List<Attachement> attachements = new ArrayList<>();

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

    public Path getFile() {
        return file;
    }

    public void setFile(final Path file) {
        this.file = file;
    }

    public List<Attachement> getAttachements() {
        return attachements;
    }

    public void addAttachement(String name, String fileName) {
        Attachement attachement = new Attachement();
        attachement.name = name;
        attachement.file = Paths.get(file.getParent().toFile().getAbsolutePath(), fileName);
        attachements.add(attachement);
    }

    public static class Attachement {
        String name;
        Path file;

        public String getName() {
            return name;
        }

        public Path getFile() {
            return file;
        }
    }
}
