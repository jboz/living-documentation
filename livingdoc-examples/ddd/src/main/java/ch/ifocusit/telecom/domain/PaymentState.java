package ch.ifocusit.telecom.domain.model;

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