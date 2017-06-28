import ch.ifocusit.livingdoc.annotations.UbiquitousLanguage;
import ch.ifocusit.livingdoc.example.cargo.model.Cargo;
import ch.ifocusit.livingdoc.example.location.model.Location;
import ch.ifocusit.livingdoc.example.shared.DomainEvent;
import ch.ifocusit.livingdoc.example.shared.DomainObjectUtils;
import ch.ifocusit.livingdoc.example.sharedhandling.model.HandlingEventType;
import ch.ifocusit.livingdoc.example.voyage.model.Voyage;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.util.Date;

/**
 * A HandlingEvent is used to register the event when, for instance,
 * a cargo is unloaded from a carrier at a some loacation at a given time.
 * <p/>
 * The HandlingEvent's are sent from different Incident Logging Applications
 * some time after the event occured and contain information about the
 * {@link ch.ifocusit.livingdoc.example.cargo.model.TrackingId}, {@link Location}, timestamp of the completion of the event,
 * and possibly, if applicable a {@link Voyage}.
 * <p/>
 * This class is the only member, and consequently the root, of the HandlingEvent aggregate.
 * <p/>
 * HandlingEvent's could contain information about a {@link Voyage} and if so,
 * the event type must be either {@link HandlingEventType#LOAD} or {@link HandlingEventType#UNLOAD}.
 * <p/>
 * All other events must be of {@link HandlingEventType#RECEIVE}, {@link HandlingEventType#CLAIM} or {@link HandlingEventType#CUSTOMS}.
 */
@UbiquitousLanguage
public final class HandlingEvent implements ch.ifocusit.livingdoc.example.sharedhandling.model.HandlingEvent<Voyage, Location, Cargo>, DomainEvent<HandlingEvent> {

    private HandlingEventType type;
    private Voyage voyage;
    private Location location;
    private Date completionTime;
    private Date registrationTime;
    private Cargo cargo;
    // Auto-generated surrogate key
    private Long id;

    /**
     * @param cargo            cargo
     * @param completionTime   completion time, the reported time that the event actually happened (e.g. the receive took place).
     * @param registrationTime registration time, the time the message is received
     * @param type             type of event
     * @param location         where the event took place
     * @param voyage           the voyage
     */
    public HandlingEvent(final Cargo cargo,
                         final Date completionTime,
                         final Date registrationTime,
                         final HandlingEventType type,
                         final Location location,
                         final Voyage voyage) {
        Validate.notNull(cargo, "Cargo is required");
        Validate.notNull(completionTime, "Completion time is required");
        Validate.notNull(registrationTime, "Registration time is required");
        Validate.notNull(type, "Handling event type is required");
        Validate.notNull(location, "Location is required");
        Validate.notNull(voyage, "Voyage is required");

        if (type.prohibitsVoyage()) {
            throw new IllegalArgumentException("Voyage is not allowed with event type " + type);
        }

        this.voyage = voyage;
        this.completionTime = (Date) completionTime.clone();
        this.registrationTime = (Date) registrationTime.clone();
        this.type = type;
        this.location = location;
        this.cargo = cargo;
    }

    /**
     * @param cargo            cargo
     * @param completionTime   completion time, the reported time that the event actually happened (e.g. the receive took place).
     * @param registrationTime registration time, the time the message is received
     * @param type             type of event
     * @param location         where the event took place
     */
    public HandlingEvent(final Cargo cargo,
                         final Date completionTime,
                         final Date registrationTime,
                         final HandlingEventType type,
                         final Location location) {
        Validate.notNull(cargo, "Cargo is required");
        Validate.notNull(completionTime, "Completion time is required");
        Validate.notNull(registrationTime, "Registration time is required");
        Validate.notNull(type, "Handling event type is required");
        Validate.notNull(location, "Location is required");

        if (type.requiresVoyage()) {
            throw new IllegalArgumentException("Voyage is required for event type " + type);
        }

        this.completionTime = (Date) completionTime.clone();
        this.registrationTime = (Date) registrationTime.clone();
        this.type = type;
        this.location = location;
        this.cargo = cargo;
        this.voyage = null;
    }

    HandlingEvent() {
        // Needed by Hibernate
    }

    public HandlingEventType type() {
        return this.type;
    }

    public Voyage voyage() {
        return DomainObjectUtils.nullSafe(this.voyage, Voyage.NONE);
    }

    public Date completionTime() {
        return new Date(this.completionTime.getTime());
    }

    public Date registrationTime() {
        return new Date(this.registrationTime.getTime());
    }

    public Location location() {
        return this.location;
    }

    public Cargo cargo() {
        return this.cargo;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final HandlingEvent event = (HandlingEvent) o;

        return sameEventAs(event);
    }

    @Override
    public boolean sameEventAs(final HandlingEvent other) {
        return other != null && new EqualsBuilder().
                append(this.cargo, other.cargo).
                append(this.voyage, other.voyage).
                append(this.completionTime, other.completionTime).
                append(this.location, other.location).
                append(this.type, other.type).
                isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().
                append(cargo).
                append(voyage).
                append(completionTime).
                append(location).
                append(type).
                toHashCode();
    }

    @Override
    public String toString() {
        final StringBuilder builder = new StringBuilder("\n--- Handling event ---\n").
                append("Cargo: ").append(cargo.trackingId()).append("\n").
                append("HandlingEventType: ").append(type).append("\n").
                append("Location: ").append(location.name()).append("\n").
                append("Completed on: ").append(completionTime).append("\n").
                append("Registered on: ").append(registrationTime).append("\n");

        if (voyage != null) {
            builder.append("Voyage: ").append(voyage.voyageNumber()).append("\n");
        }

        return builder.toString();
    }

}
