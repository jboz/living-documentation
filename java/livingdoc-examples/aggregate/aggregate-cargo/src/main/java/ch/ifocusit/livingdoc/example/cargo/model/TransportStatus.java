package ch.ifocusit.livingdoc.example.cargo.model;

import ch.ifocusit.livingdoc.annotations.UbiquitousLanguage;
import ch.ifocusit.livingdoc.example.shared.ValueObject;

/**
 * Represents the different transport statuses for a cargo.
 */
@UbiquitousLanguage
public enum TransportStatus implements ValueObject<TransportStatus> {

    @UbiquitousLanguage
    NOT_RECEIVED,
    @UbiquitousLanguage
    IN_PORT,
    @UbiquitousLanguage
    ONBOARD_CARRIER,
    @UbiquitousLanguage
    CLAIMED,
    @UbiquitousLanguage
    UNKNOWN;

    @Override
    public boolean sameValueAs(final TransportStatus other) {
        return this.equals(other);
    }
}
