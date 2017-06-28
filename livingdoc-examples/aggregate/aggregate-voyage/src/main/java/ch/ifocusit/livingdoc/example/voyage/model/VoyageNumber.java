package ch.ifocusit.livingdoc.example.voyage.model;

import ch.ifocusit.livingdoc.annotations.UbiquitousLanguage;
import ch.ifocusit.livingdoc.example.shared.ValueObject;
import org.apache.commons.lang.Validate;

/**
 * Identifies a voyage.
 */
@UbiquitousLanguage
public class VoyageNumber implements ValueObject<VoyageNumber> {

    private String number;

    public VoyageNumber(String number) {
        Validate.notNull(number);

        this.number = number;
    }

    VoyageNumber() {
        // Needed by Hibernate
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null) return false;
        if (!(o instanceof VoyageNumber)) return false;

        final VoyageNumber other = (VoyageNumber) o;

        return sameValueAs(other);
    }

    @Override
    public int hashCode() {
        return number.hashCode();
    }

    @Override
    public boolean sameValueAs(VoyageNumber other) {
        return other != null && this.number.equals(other.number);
    }

    @Override
    public String toString() {
        return number;
    }

    public String idString() {
        return number;
    }

}
