package ch.ifocusit.livingdoc.example.cargo.model;

import ch.ifocusit.livingdoc.annotations.Glossary;
import ch.ifocusit.livingdoc.example.shared.ValueObject;

/**
 * Routing status.
 */
@Glossary
public enum RoutingStatus implements ValueObject<RoutingStatus> {
    NOT_ROUTED, ROUTED, MISROUTED;

    @Override
    public boolean sameValueAs(final RoutingStatus other) {
        return this.equals(other);
    }

}
