package com.vforce360.mar.controller;

import com.vforce360.mar.service.MarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST Controller for MAR review section.
 * Returns HTML views for the report.
 */
@RestController
@RequestMapping("/api/mar")
public class MarController {

    private final MarService service;

    @Autowired
    public MarController(MarService service) {
        this.service = service;
    }

    /**
     * Returns the MAR content as rendered HTML.
     * Regression Target: Ensure this returns text/html, not application/json.
     */
    @GetMapping(value = "/{projectId}", produces = MediaType.TEXT_HTML_VALUE)
    public String viewReport(@PathVariable String projectId) {
        // TODO: Implementation required to return HTML string
        return "<h1>Not Implemented</h1>"; 
    }
}