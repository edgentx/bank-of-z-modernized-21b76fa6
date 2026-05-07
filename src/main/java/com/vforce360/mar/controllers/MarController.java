package com.vforce360.mar.controllers;

import com.vforce360.mar.service.MarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.UUID;

/**
 * Controller for handling MAR review display requests.
 * Serves HTML content for the frontend integration.
 */
@Controller
public class MarController {

    private final MarService marService;

    @Autowired
    public MarController(MarService marService) {
        this.marService = marService;
    }

    /**
     * Endpoint to retrieve the MAR Review content as rendered HTML.
     * This matches the frontend URL structure defined in tests.
     * 
     * @param projectId The UUID of the brownfield project.
     * @return HTML string response.
     */
    @GetMapping(
        value = "/api/projects/{projectId}/mar/review",
        produces = MediaType.TEXT_HTML_VALUE
    )
    @ResponseBody
    public String getMarReview(@PathVariable UUID projectId) {
        return marService.getMarReviewHtml(projectId);
    }
}