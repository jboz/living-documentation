package ch.ifocusit.livingdoc.example.cargo.model;

import ch.ifocusit.livingdoc.example.location.model.Location;
import ch.ifocusit.livingdoc.example.shared.ValueObject;
import ch.ifocusit.livingdoc.example.sharedhandling.model.HandlingEventType;
import ch.ifocusit.livingdoc.example.voyage.model.Voyage;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

/**
 * A handling activity represents how and where a cargo can be handled,
 * and can be used to express predictions about what is expected to
 * happen to a cargo in the future.
 */
public class HandlingActivity implements ValueObject<HandlingActivity> {

    // TODO make HandlingActivity a part of HandlingEvent too? There is some overlap.

    private HandlingEventType type;
    private Location location;
    private Voyage voyage;

    public HandlingActivity(final HandlingEventType type, final Location location) {
        Validate.notNull(type, "Handling event type is required");
        Validate.notNull(location, "Location is required");

        this.type = type;
        this.location = location;
    }

    public HandlingActivity(final HandlingEventType type, final Location location, final Voyage voyage) {
        Validate.notNull(type, "Handling event type is required");
        Validate.notNull(location, "Location is required");
        Validate.notNull(location, "Voyage is required");

        this.type = type;
        this.location = location;
        this.voyage = voyage;
    }

    HandlingActivity() {
        // Needed by Hibernate
    }

    public HandlingEventType type() {
        return type;
    }

    public Location location() {
        return location;
    }

    public Voyage voyage() {
        return voyage;
    }

    @Override
    public boolean sameValueAs(final HandlingActivity other) {
        return other != null && new EqualsBuilder().
                append(this.type, other.type).
                append(this.location, other.location).
                append(this.voyage, other.voyage).
                isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().
                append(this.type).
                append(this.location).
                append(this.voyage).
                toHashCode();
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj == this) return true;
        if (obj == null) return false;
        if (obj.getClass() != this.getClass()) return false;

        HandlingActivity other = (HandlingActivity) obj;

        return sameValueAs(other);
    }

}
