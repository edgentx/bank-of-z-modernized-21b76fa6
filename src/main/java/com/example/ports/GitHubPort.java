package com.example.ports;

import com.example.domain.validation.model.ValidationAggregate;

public interface GitHubPort {
    String createIssue(ValidationAggregate aggregate);
}
