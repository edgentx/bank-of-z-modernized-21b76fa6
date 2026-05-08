package com.example.ports;

/**
 * Port interface for the external Ticketing System (e.g., GitHub Issues).
 * Used to decouple the domain logic from the specific API client.
 */
public interface TicketingSystem {
    /**
     * Creates a new issue/ticket in the external system.
     *
     * @param title The title of the issue
     * @param description The description/body of the issue
     * @return The URL of the created issue, or null if creation failed.
     */
    String createIssue(String title, String description);
}
