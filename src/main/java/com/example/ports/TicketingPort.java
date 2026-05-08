package com.example.ports;

/**
 * Port interface for interacting with the Ticketing system (e.g. GitHub Issues).
 */
public interface TicketingPort {
    /**
     * Creates a ticket in the external system.
     * @param title The title of the defect/ticket.
     * @param description The body content.
     * @return The URL of the created ticket.
     */
    String createTicket(String title, String description);
}