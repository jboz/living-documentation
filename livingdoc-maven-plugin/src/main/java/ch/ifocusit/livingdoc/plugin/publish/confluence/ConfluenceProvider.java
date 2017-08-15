package ch.ifocusit.livingdoc.plugin.publish.confluence;

import ch.ifocusit.livingdoc.plugin.publish.PublishProvider;
import ch.ifocusit.livingdoc.plugin.publish.confluence.client.ConfluencePage;
import ch.ifocusit.livingdoc.plugin.publish.confluence.client.ConfluenceRestClient;
import ch.ifocusit.livingdoc.plugin.publish.confluence.client.NotFoundException;
import ch.ifocusit.livingdoc.plugin.publish.model.Page;
import org.apache.commons.codec.digest.DigestUtils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.nio.file.Path;

public class ConfluenceProvider implements PublishProvider {

    final ConfluenceRestClient client;

    public ConfluenceProvider(String endpoint, String username, String password) {
        client = new ConfluenceRestClient(endpoint, username, password);
    }

    @Override
    public boolean exists(final Page page) {
        try {
            client.getPageByTitle(page.getSpaceKey(), page.getTitle());
            return true;
        } catch (NotFoundException e) {
            return false;
        }
    }

    @Override
    public void update(final Page page) {
        String contentId = client.getPageByTitle(page.getSpaceKey(), page.getTitle());
        ConfluencePage existingPage = client.getPageWithContentAndVersionById(contentId);
        String oldContent = existingPage.getContent();

        String newContentHash = DigestUtils.sha256Hex(page.getContent());
        String oldContentHash = DigestUtils.sha256Hex(oldContent);
        if (!oldContentHash.equals(newContentHash)) {
            client.updatePage(contentId, page.getParentId(), page.getTitle(), page.getContent(),
                    (int) (existingPage.getVersion() + 1));

            // remove all attachement
            client.getAttachments(contentId).forEach(attachment -> client.deleteAttachment(attachment.getId()));
            // add attachements
            page.getAttachements().forEach(attachement ->
                    client.addAttachment(contentId, attachement.getName(), fileInputStream(attachement.getFile()))
            );
        } else {
            // TODO log INFO
            System.out.println("Page with title=" + page.getTitle() + " did not change.");
        }
    }

    @Override
    public void insert(final Page page) {
        // post page to confluence.
        String contentId = client.addPageUnderAncestor(page.getSpaceKey(), page.getParentId(), page.getTitle(), page.getContent());

        // add attachements
        page.getAttachements().forEach(attachement ->
                client.addAttachment(contentId, attachement.getName(), fileInputStream(attachement.getFile()))
        );
    }

    private static FileInputStream fileInputStream(Path filePath) {
        try {
            return new FileInputStream(filePath.toFile());
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Could not find attachment ", e);
        }
    }

}
