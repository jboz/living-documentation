package ch.ifocusit.livingdoc.example.sharedhandling.model;

import ch.ifocusit.livingdoc.annotations.UbiquitousLanguage;
import ch.ifocusit.livingdoc.example.shared.ValueObject;

/**
 * Handling event type. Either requires or prohibits a carrier movement
 * association, it's never optional.
 */
@UbiquitousLanguage
public enum HandlingEventType implements ValueObject<HandlingEventType> {
    LOAD(true),
    UNLOAD(true),
    RECEIVE(false),
    CLAIM(false),
    CUSTOMS(false);

    private final boolean voyageRequired;

    /**
     * Private enum constructor.
     *
     * @param voyageRequired whether or not a voyage is associated with this event type
     */
    HandlingEventType(final boolean voyageRequired) {
        this.voyageRequired = voyageRequired;
    }

    /**
     * @return True if a voyage association is required for this event type.
     */
    public boolean requiresVoyage() {
        return voyageRequired;
    }

    /**
     * @return True if a voyage association is prohibited for this event type.
     */
    public boolean prohibitsVoyage() {
        return !requiresVoyage();
    }

    @Override
    public boolean sameValueAs(HandlingEventType other) {
        return other != null && this.equals(other);
    }

}