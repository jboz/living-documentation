package ch.ifocusit.livingdoc.example.voyage.model;

import ch.ifocusit.livingdoc.annotations.Glossary;
import ch.ifocusit.livingdoc.example.location.model.Location;
import ch.ifocusit.livingdoc.example.shared.ValueObject;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.util.Date;


/**
 * A carrier movement is a vessel voyage from one location to another.
 */
@Glossary
public final class CarrierMovement implements ValueObject<CarrierMovement> {

    // Null object pattern
    public static final CarrierMovement NONE = new CarrierMovement(
            Location.UNKNOWN, Location.UNKNOWN,
            new Date(0), new Date(0)
    );
    private Location departureLocation;
    private Location arrivalLocation;
    private Date departureTime;
    private Date arrivalTime;
    // Auto-generated surrogate key
    private Long id;

    /**
     * Constructor.
     *
     * @param departureLocation location of departure
     * @param arrivalLocation   location of arrival
     * @param departureTime     time of departure
     * @param arrivalTime       time of arrival
     */
    // TODO make package local
    public CarrierMovement(Location departureLocation,
                           Location arrivalLocation,
                           Date departureTime,
                           Date arrivalTime) {
        Validate.noNullElements(new Object[]{departureLocation, arrivalLocation, departureTime, arrivalTime});
        this.departureTime = departureTime;
        this.arrivalTime = arrivalTime;
        this.departureLocation = departureLocation;
        this.arrivalLocation = arrivalLocation;
    }

    CarrierMovement() {
        // Needed by Hibernate
    }

    /**
     * @return Departure location.
     */
    public Location departureLocation() {
        return departureLocation;
    }

    /**
     * @return Arrival location.
     */
    public Location arrivalLocation() {
        return arrivalLocation;
    }

    /**
     * @return Time of departure.
     */
    public Date departureTime() {
        return new Date(departureTime.getTime());
    }

    /**
     * @return Time of arrival.
     */
    public Date arrivalTime() {
        return new Date(arrivalTime.getTime());
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final CarrierMovement that = (CarrierMovement) o;

        return sameValueAs(that);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().
                append(this.departureLocation).
                append(this.departureTime).
                append(this.arrivalLocation).
                append(this.arrivalTime).
                toHashCode();
    }

    @Override
    public boolean sameValueAs(CarrierMovement other) {
        return other != null && new EqualsBuilder().
                append(this.departureLocation, other.departureLocation).
                append(this.departureTime, other.departureTime).
                append(this.arrivalLocation, other.arrivalLocation).
                append(this.arrivalTime, other.arrivalTime).
                isEquals();
    }

}
