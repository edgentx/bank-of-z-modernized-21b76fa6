package com.example.application;

/**
 * Thrown by application services when a requested aggregate id has no
 * corresponding row in its repository. Maps to HTTP 404 via the global
 * exception handler. Kept in the application layer (not the domain) because
 * "not found" is an interaction concept, not an invariant of any aggregate.
 */
public class AggregateNotFoundException extends RuntimeException {
  public AggregateNotFoundException(String aggregateName, String id) {
    super(aggregateName + " not found: " + id);
  }
}
