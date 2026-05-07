package com.vforce360.controller;

import com.vforce360.service.ReportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Controller for the MAR Review section.
 * Renders the Markdown content as HTML using Thymeleaf.
 */
@Controller
@RequestMapping("/api/projects")
public class ReviewController {

    @Autowired
    private ReportService reportService;

    /**
     * Endpoint to view the MAR review.
     * Returns a Thymeleaf view name, passing the Markdown content as a model attribute.
     */
    @GetMapping(value = "/{projectId}/mar/review", produces = MediaType.TEXT_HTML_VALUE)
    public ModelAndView viewMarReview(@PathVariable String projectId) {
        // 1. Get formatted Markdown content from service
        String markdownContent = reportService.getFormattedReport(projectId);

        // 2. Return View Name and Model
        // The ViewResolver will look for mar_review.html in src/main/resources/templates/
        ModelAndView mav = new ModelAndView("mar_review");
        mav.addObject("reportContent", markdownContent);
        return mav;
    }
}
