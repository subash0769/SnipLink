package com.urlshortener.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateUrlRequest {

    @NotBlank(message = "Original URL is required")
    private String originalUrl;

    @Size(min = 3, max = 30, message = "Custom code must be between 3 and 30 characters")
    private String customCode;

    @Size(max = 255, message = "Title cannot exceed 255 characters")
    private String title;

    public CreateUrlRequest() {}

    public CreateUrlRequest(String originalUrl, String customCode, String title) {
        this.originalUrl = originalUrl;
        this.customCode = customCode;
        this.title = title;
    }

    public String getOriginalUrl() { return originalUrl; }
    public void setOriginalUrl(String originalUrl) { this.originalUrl = originalUrl; }

    public String getCustomCode() { return customCode; }
    public void setCustomCode(String customCode) { this.customCode = customCode; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
}
