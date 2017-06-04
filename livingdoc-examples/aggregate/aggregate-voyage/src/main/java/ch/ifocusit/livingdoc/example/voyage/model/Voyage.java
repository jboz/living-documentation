package ch.ifocusit.livingdoc.example.voyage.model;

import ch.ifocusit.livingdoc.annotations.RootAggregate;
import ch.ifocusit.livingdoc.example.location.model.Location;
import ch.ifocusit.livingdoc.example.shared.Entity;
import org.apache.commons.lang.Validate;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * A Voyage.
 */
@RootAggregate
public class Voyage implements Entity<Voyage> {

    // Null object pattern
    public static final Voyage NONE = new Voyage(
            new VoyageNumber(""), Schedule.EMPTY
    );
    private VoyageNumber voyageNumber;
    private Schedule schedule;
    // Needed by Hibernate
    private Long id;

    public Voyage(final VoyageNumber voyageNumber, final Schedule schedule) {
        Validate.notNull(voyageNumber, "Voyage number is required");
        Validate.notNull(schedule, "Schedule is required");

        this.voyageNumber = voyageNumber;
        this.schedule = schedule;
    }

    Voyage() {
        // Needed by Hibernate
    }

    /**
     * @return Voyage number.
     */
    public VoyageNumber voyageNumber() {
        return voyageNumber;
    }

    /**
     * @return Schedule.
     */
    public Schedule schedule() {
        return schedule;
    }

    @Override
    public int hashCode() {
        return voyageNumber.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (!(o instanceof Voyage)) return false;

        final Voyage that = (Voyage) o;

        return sameIdentityAs(that);
    }

    @Override
    public boolean sameIdentityAs(Voyage other) {
        return other != null && this.voyageNumber().sameValueAs(other.voyageNumber());
    }

    @Override
    public String toString() {
        return "Voyage " + voyageNumber;
    }

    /**
     * Builder pattern is used for incremental construction
     * of a Voyage aggregate. This serves as an aggregate factory.
     */
    public static final class Builder {

        private final List<CarrierMovement> carrierMovements = new ArrayList<CarrierMovement>();
        private final VoyageNumber voyageNumber;
        private Location departureLocation;

        public Builder(final VoyageNumber voyageNumber, final Location departureLocation) {
            Validate.notNull(voyageNumber, "Voyage number is required");
            Validate.notNull(departureLocation, "Departure location is required");

            this.voyageNumber = voyageNumber;
            this.departureLocation = departureLocation;
        }

        public Builder addMovement(Location arrivalLocation, Date departureTime, Date arrivalTime) {
            carrierMovements.add(new CarrierMovement(departureLocation, arrivalLocation, departureTime, arrivalTime));
            // Next departure location is the same as this arrival location
            this.departureLocation = arrivalLocation;
            return this;
        }

        public Voyage build() {
            return new Voyage(voyageNumber, new Schedule(carrierMovements));
        }

    }

}
