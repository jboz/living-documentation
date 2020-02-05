package ch.ifocusit.livingdoc.example.cargo.model;

import ch.ifocusit.livingdoc.annotations.UbiquitousLanguage;
import ch.ifocusit.livingdoc.example.location.model.Location;
import ch.ifocusit.livingdoc.example.shared.ValueObject;
import ch.ifocusit.livingdoc.example.voyage.model.Voyage;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.util.Date;

/**
 * An itinerary consists of one or more legs.
 */
@UbiquitousLanguage
public class Leg implements ValueObject<Leg> {

    private Voyage voyage;
    private Location loadLocation;
    private Location unloadLocation;
    private Date loadTime;
    private Date unloadTime;
    // Auto-generated surrogate key
    private Long id;

    public Leg(Voyage voyage, Location loadLocation, Location unloadLocation, Date loadTime, Date unloadTime) {
        Validate.noNullElements(new Object[]{voyage, loadLocation, unloadLocation, loadTime, unloadTime});

        this.voyage = voyage;
        this.loadLocation = loadLocation;
        this.unloadLocation = unloadLocation;
        this.loadTime = loadTime;
        this.unloadTime = unloadTime;
    }

    Leg() {
        // Needed by Hibernate
    }

    public Voyage voyage() {
        return voyage;
    }

    public Location loadLocation() {
        return loadLocation;
    }

    public Location unloadLocation() {
        return unloadLocation;
    }

    public Date loadTime() {
        return new Date(loadTime.getTime());
    }

    public Date unloadTime() {
        return new Date(unloadTime.getTime());
    }

    @Override
    public boolean sameValueAs(final Leg other) {
        return other != null && new EqualsBuilder().
                append(this.voyage, other.voyage).
                append(this.loadLocation, other.loadLocation).
                append(this.unloadLocation, other.unloadLocation).
                append(this.loadTime, other.loadTime).
                append(this.unloadTime, other.unloadTime).
                isEquals();
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Leg leg = (Leg) o;

        return sameValueAs(leg);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().
                append(voyage).
                append(loadLocation).
                append(unloadLocation).
                append(loadTime).
                append(unloadTime).
                toHashCode();
    }

}
