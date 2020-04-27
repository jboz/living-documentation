package ch.ifocusit.livingdoc.example.sharedhandling.model;

public class EmptyHandlingHistory implements HandlingHistory {
    @Override
    public HandlingEvent mostRecentlyCompletedEvent() {
        return null;
    }
}
