package com.softwareleaf.confluence.rest.model;

import java.util.Objects;

/**
 * This object represents the Body of some {@code Content}.
 *
 * @author Jonathon Hope
 */
public class Body {
    private Storage storage;

    /**
     * Constructor.
     *
     * @param storage
     *            the instance of {@code Storage} to wrap.
     */
    public Body(final Storage storage) {
        this.storage = storage;
    }

    /**
     * Getter for this Body.storage.
     *
     * @return the storage object encapsulated by this Body.
     */
    public Storage getStorage() {
        return storage;
    }

    /**
     * Setter for this Body.storage.
     *
     * @param storage
     *            the instance of storage.
     */
    public void setStorage(Storage storage) {
        this.storage = storage;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Body body = (Body) o;
        return Objects.equals(storage, body.storage);
    }

    @Override
    public int hashCode() {
        return Objects.hash(storage);
    }
}
