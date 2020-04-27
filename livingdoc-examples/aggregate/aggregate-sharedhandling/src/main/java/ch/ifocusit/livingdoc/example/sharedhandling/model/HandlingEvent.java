package ch.ifocusit.livingdoc.example.sharedhandling.model;

public interface HandlingEvent<V, L, C> {
    public HandlingEventType type();

    public V voyage();

    public L location();

    public C cargo();
}
