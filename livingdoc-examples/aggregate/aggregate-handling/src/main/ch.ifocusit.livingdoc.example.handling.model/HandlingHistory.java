import ch.ifocusit.livingdoc.annotations.UbiquitousLanguage;
import ch.ifocusit.livingdoc.example.shared.ValueObject;
import org.apache.commons.lang.Validate;

import java.util.*;

import static java.util.Collections.sort;

/**
 * The handling history of a cargo.
 */
@UbiquitousLanguage
public class HandlingHistory implements ch.ifocusit.livingdoc.example.sharedhandling.model.HandlingHistory, ValueObject<HandlingHistory> {

    private static final Comparator<HandlingEvent> BY_COMPLETION_TIME_COMPARATOR =
            (he1, he2) -> he1.completionTime().compareTo(he2.completionTime());
    private final List<HandlingEvent> handlingEvents;


    public HandlingHistory(Collection<HandlingEvent> handlingEvents) {
        Validate.notNull(handlingEvents, "Handling events are required");

        this.handlingEvents = new ArrayList<>(handlingEvents);
    }

    /**
     * @return A distinct list (no duplicate registrations) of handling events, ordered by completion time.
     */
    public List<HandlingEvent> distinctEventsByCompletionTime() {
        final List<HandlingEvent> ordered = new ArrayList<>(
                new HashSet<>(handlingEvents)
        );
        sort(ordered, BY_COMPLETION_TIME_COMPARATOR);
        return Collections.unmodifiableList(ordered);
    }

    /**
     * @return Most recently completed event, or null if the delivery history is empty.
     */
    public HandlingEvent mostRecentlyCompletedEvent() {
        final List<HandlingEvent> distinctEvents = distinctEventsByCompletionTime();
        if (distinctEvents.isEmpty()) {
            return null;
        } else {
            return distinctEvents.get(distinctEvents.size() - 1);
        }
    }

    @Override
    public boolean sameValueAs(HandlingHistory other) {
        return other != null && this.handlingEvents.equals(other.handlingEvents);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        final HandlingHistory other = (HandlingHistory) o;
        return sameValueAs(other);
    }

    @Override
    public int hashCode() {
        return handlingEvents.hashCode();
    }

}
