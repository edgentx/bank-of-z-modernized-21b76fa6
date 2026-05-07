package com.vforce360.mar.adapters;

import com.vforce360.mar.services.MarService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/mar")
public class MarController {

    private final MarService marService;

    public MarController(MarService marService) {
        this.marService = marService;
    }

    @GetMapping(value = "/{id}/review", produces = MediaType.TEXT_HTML_VALUE)
    public ResponseEntity<String> getMarReview(@PathVariable UUID id) {
        // Delegate to service to get the rendered HTML
        String htmlContent = marService.getMarReviewHtml(id);
        return ResponseEntity.ok(htmlContent);
    }
}