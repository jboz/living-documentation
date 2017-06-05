package ch.ifocusit.livingdoc.example.voyage.model;

import ch.ifocusit.livingdoc.annotations.Glossary;
import ch.ifocusit.livingdoc.example.shared.ValueObject;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.util.Collections;
import java.util.List;

/**
 * A voyage schedule.
 */
@Glossary
public class Schedule implements ValueObject<Schedule> {

    public static final Schedule EMPTY = new Schedule();
    private List<CarrierMovement> carrierMovements = Collections.EMPTY_LIST;

    Schedule(final List<CarrierMovement> carrierMovements) {
        Validate.notNull(carrierMovements);
        Validate.noNullElements(carrierMovements);
        Validate.notEmpty(carrierMovements);

        this.carrierMovements = carrierMovements;
    }

    Schedule() {
        // Needed by Hibernate
    }

    /**
     * @return Carrier movements.
     */
    public List<CarrierMovement> carrierMovements() {
        return Collections.unmodifiableList(carrierMovements);
    }

    @Override
    public boolean sameValueAs(final Schedule other) {
        return other != null && this.carrierMovements.equals(other.carrierMovements);
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final Schedule that = (Schedule) o;

        return sameValueAs(that);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(this.carrierMovements).toHashCode();
    }

}
