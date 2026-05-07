package com.example.adapters;

import com.example.domain.shared.Command;
import com.example.domain.vforce.ports.VForce360Port;

/**
 * Adapter for VForce360 integration.
 * Currently acts as a stub/simulator for the defect reporting system.
 * In a production environment, this would make an HTTP call to the external API.
 */
public class VForce360Adapter implements VForce360Port {

    @Override
    public String reportDefect(Command cmd) {
        // Simulate the external call returning a GitHub Issue URL
        // This fixed URL allows us to verify the Slack integration logic
        // without actually creating a GitHub issue.
        // S-FB-1: Validation of GitHub URL in Slack body.
        return "https://github.com/bank-of-z/issues/454";
    }
}
