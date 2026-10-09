package com.urlshortener.dto;

import java.time.LocalDateTime;

public class ShortUrlResponse {
    private Long id;
    private String shortCode;
    private String shortUrl;
    private String originalUrl;
    private String title;
    private long clickCount;
    private LocalDateTime createdAt;

    public ShortUrlResponse() {}

    public ShortUrlResponse(Long id, String shortCode, String shortUrl, String originalUrl, String title, long clickCount, LocalDateTime createdAt) {
        this.id = id;
        this.shortCode = shortCode;
        this.shortUrl = shortUrl;
        this.originalUrl = originalUrl;
        this.title = title;
        this.clickCount = clickCount;
        this.createdAt = createdAt;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getShortCode() { return shortCode; }
    public void setShortCode(String shortCode) { this.shortCode = shortCode; }

    public String getShortUrl() { return shortUrl; }
    public void setShortUrl(String shortUrl) { this.shortUrl = shortUrl; }

    public String getOriginalUrl() { return originalUrl; }
    public void setOriginalUrl(String originalUrl) { this.originalUrl = originalUrl; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public long getClickCount() { return clickCount; }
    public void setClickCount(long clickCount) { this.clickCount = clickCount; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
