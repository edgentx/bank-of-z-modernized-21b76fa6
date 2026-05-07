package com.vforce360;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/projects")
public class MarController {

    private final MarService marService;

    public MarController(MarService marService) {
        this.marService = marService;
    }

    @GetMapping("/{projectId}/mar")
    public String viewMar(@PathVariable String projectId) {
        return marService.getFormattedReport(projectId);
    }
}
