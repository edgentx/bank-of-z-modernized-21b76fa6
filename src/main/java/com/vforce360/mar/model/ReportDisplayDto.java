package com.vforce360.mar.model;

/**
 * DTO representing the formatted report to be returned to the frontend.
 * This is the object that must be HTML, not raw JSON.
 */
public class ReportDisplayDto {
    private String contentHtml;

    public ReportDisplayDto() {}

    public ReportDisplayDto(String contentHtml) {
        this.contentHtml = contentHtml;
    }

    public String getContentHtml() {
        return contentHtml;
    }

    public void setContentHtml(String contentHtml) {
        this.contentHtml = contentHtml;
    }
}