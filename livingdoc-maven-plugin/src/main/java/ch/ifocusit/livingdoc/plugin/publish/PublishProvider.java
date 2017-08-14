package ch.ifocusit.livingdoc.plugin.publish;

import ch.ifocusit.livingdoc.plugin.publish.model.Page;

public interface PublishProvider {

    public boolean exists(Page page);

    public void update(Page page);

    public void insert(Page page);
}
