package com.example.ports;

/**
 * Port interface for VForce360 external system interaction.
 * Originally looked for VForce360Aggregate, adjusting to generic DTO/Object pattern.
 */
public interface VForce360Repository {
    void saveData(Object data);
}
