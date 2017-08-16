package ch.ifocusit.livingdoc.example.cargo.model;

import ch.ifocusit.livingdoc.annotations.UbiquitousLanguage;
import ch.ifocusit.livingdoc.example.shared.ValueObject;

/**
 * Routing status.
 */
@UbiquitousLanguage
public enum RoutingStatus implements ValueObject<RoutingStatus> {

    @UbiquitousLanguage
    NOT_ROUTED,
    @UbiquitousLanguage
    ROUTED,
    @UbiquitousLanguage
    MISROUTED;

    @Override
    public boolean sameValueAs(final RoutingStatus other) {
        return this.equals(other);
    }

}
