package com.example.domain.vforce360.model;

import com.example.domain.shared.Command;

public record UpdateGitHubLinkCmd(String defectId, String url) implements Command {}