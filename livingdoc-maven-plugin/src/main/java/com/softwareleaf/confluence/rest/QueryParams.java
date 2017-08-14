package com.softwareleaf.confluence.rest;

/**
 * An easy way to access common query params.
 *
 * @author Jonathon Hope
 */
public interface QueryParams {

    /**
     * These are the acceptable expandable queries.
     * <p>
     * The format for these is:
     * 
     * <pre>
     * {@literal
     *      REQUEST_URL ?expand.{expandable}
     * }
     * </pre>
     * 
     * Default value: history,space,version.
     */
    enum Expandables {

        HISTORY, BODY, CONTAINER, ANCESTORS, CHILDREN, DESCENDANTS, SPACE, VERSION, METADATA;

        @Override
        public String toString() {
            return name().toLowerCase();
        }

    }

    /**
     * Enumerated list of statuses the content to be found is in. Defaults to {@code CURRENT} if not specified. If set to
     * {@code ANY}, content in {@code CURRENT} and {@code TRASHED} status will be fetched.
     */
    enum Status {
        ALL, CURRENT, TRASHED;

        @Override
        public String toString() {
            return name().toLowerCase();
        }
    }

    /**
     * This will expand the body.storage object of a request for particular content.
     * <p>
     * For example:
     * 
     * <pre>
     * {@literal
     *     GET /rest/api/content/757575775?expand=body.storage
     * }
     * </pre>
     * <p>
     * would expand the actual content stored in the page or blog post with the {@code id=757575775}.
     */
    String EXPAND_BODY_STORAGE = "?expand=body.storage,version";

}
