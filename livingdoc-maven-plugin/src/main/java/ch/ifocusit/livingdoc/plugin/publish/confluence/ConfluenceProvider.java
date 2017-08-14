package ch.ifocusit.livingdoc.plugin.publish.confluence;

import ch.ifocusit.livingdoc.plugin.publish.PublishProvider;
import ch.ifocusit.livingdoc.plugin.publish.model.Page;
import com.softwareleaf.confluence.rest.ConfluenceClient;
import com.softwareleaf.confluence.rest.model.*;
import org.apache.commons.codec.digest.DigestUtils;

public class ConfluenceProvider implements PublishProvider {

    final ConfluenceClient client;

    public ConfluenceProvider(String endpoint, String username, String password) {
        client = ConfluenceClient.builder()
                .baseURL(endpoint).username(username).password(password)
                .build();
    }

    @Override
    public boolean exists(final Page page) {
        ContentResultList contentBySpaceKeyAndTitle = searchContentWithoutBody(page.getSpaceKey(), page.getTitle());
        return contentBySpaceKeyAndTitle.getSize() > 0;
    }

    private ContentResultList searchContentWithoutBody(String spaceKey, String title) {
        return client.getContentBySpaceKeyAndTitle(spaceKey, title);
    }

    @Override
    public void update(final Page page) {
        Content existingPage = getContentByTitle(page.getSpaceKey(), page.getTitle());
        String oldContent = existingPage.getBody().getStorage().getValue();

        String newContentHash = DigestUtils.sha256Hex(page.getContent());
        String oldContentHash = DigestUtils.sha256Hex(oldContent);
        if (!oldContentHash.equals(newContentHash)) {
            Body body = new Body(new Storage(page.getContent(), Storage.Representation.STORAGE.toString()));
            existingPage.setBody(body);
            existingPage.setSpace(new Space(page.getSpaceKey()));
            existingPage.getVersion().setNumber(existingPage.getVersion().getNumber() + 1);
            client.putContent(existingPage);

            // TODO deleteConfluenceAttachmentsNotPresentUnderPage(contentId, page.getAttachments());
            // TODO addAttachments(contentId, page.getContentFilePath(), page.getAttachments());
        } else {
            // TODO log INFO
            System.out.println("Page with title=" + page.getTitle() + " did not change.");
        }
    }

    public Content getContentByTitle(String spaceKey, String title) {
        ContentResultList pageByTitle = searchContentWithoutBody(spaceKey, title);
        if (pageByTitle.getSize() > 0) {
            Content[] contents = pageByTitle.getContents();
            return client.getContentById(contents[0].getId());
        }
        throw new IllegalStateException("Could not find find page with title=" + title + " in space=" + spaceKey);
    }

    @Override
    public void insert(final Page page) {
        // configure page
        Content newContent = new Content();
        newContent.setType(Type.PAGE);
        newContent.setSpace(new Space(page.getSpaceKey()));
        newContent.setTitle(page.getTitle());
        newContent.setBody(new Body(new Storage(page.getContent(), Storage.Representation.STORAGE.toString())));

        Parent parent = new Parent(page.getParentId(), Type.PAGE.toString());
        newContent.setAncestors(new Parent[]{parent});

        // post page to confluence.
        client.postContent(newContent);
    }
}
