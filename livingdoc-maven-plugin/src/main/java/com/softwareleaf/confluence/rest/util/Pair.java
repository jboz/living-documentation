package com.softwareleaf.confluence.rest.util;

/**
 * An immutable tuple structure, that can hold two different types.
 *
 * @author Jonathon Hope
 * @since 7/07/2015
 */
public class Pair<U, V> {

    public final U p1;
    public final V p2;

    public Pair(U p1, V p2) {
        this.p1 = p1;
        this.p2 = p2;
    }

}
