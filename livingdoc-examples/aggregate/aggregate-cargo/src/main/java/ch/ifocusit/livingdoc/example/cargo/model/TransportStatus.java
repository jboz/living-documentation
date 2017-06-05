package ch.ifocusit.livingdoc.example.cargo.model;

import ch.ifocusit.livingdoc.annotations.Glossary;
import ch.ifocusit.livingdoc.example.shared.ValueObject;

/**
 * Represents the different transport statuses for a cargo.
 */
@Glossary
public enum TransportStatus implements ValueObject<TransportStatus> {
    NOT_RECEIVED, IN_PORT, ONBOARD_CARRIER, CLAIMED, UNKNOWN;

    @Override
    public boolean sameValueAs(final TransportStatus other) {
        return this.equals(other);
    }
}
