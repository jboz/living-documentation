package ch.ifocusit.telecom.domain;

/**
 * Bill payment state values.
 */
public enum PaymentState {
    /**
     * Wainting payment by the client.
     */
    WAITING,
    /**
     * Client has payed.
     */
    DONE;
}