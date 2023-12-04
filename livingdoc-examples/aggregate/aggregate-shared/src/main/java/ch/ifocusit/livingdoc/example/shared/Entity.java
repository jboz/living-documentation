package ch.ifocusit.livingdoc.example.shared;

/**
 * An entity, as explained in the DDD book.
 */
public interface Entity<T> {

    /**
     * Entities compare by identity, not by attributes.
     *
     * @param other The other entity.
     * @return true if the identities are the same, regardles of other attributes.
     */
    boolean sameIdentityAs(T other);

}
