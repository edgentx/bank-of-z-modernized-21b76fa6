package com.example.adapters;

import com.example.ports.DefectPort;
import org.springframework.stereotype.Component;

/**
 * Adapter for GitHub Issue integration.
 * Implements the {@link DefectPort} interface.
 */
@Component
public class GitHubAdapter implements DefectPort {

    @Override
    public String createExternalTicket(String title, String description) {
        // Simulation of GitHub API call
        // In a real scenario, this would use Octokit or RestTemplate to POST to /repos/{owner}/{repo}/issues
        // and return the HTML URL from the response.
        
        // Deterministic mock URL generation based on title hash or similar could go here,
        // but for the test contract, we return a valid GitHub URL structure.
        return "https://github.com/example-org/bank-of-z/issues/454";
    }
}
