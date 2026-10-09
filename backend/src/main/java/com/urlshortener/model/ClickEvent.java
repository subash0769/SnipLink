package com.urlshortener.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "click_events", indexes = {
    @Index(name = "idx_click_short_url", columnList = "short_url_id")
})
public class ClickEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "short_url_id", nullable = false)
    private ShortUrl shortUrl;

    @Column(nullable = false)
    private LocalDateTime clickedAt;

    private String ipAddress;

    @Column(length = 512)
    private String userAgent;

    private String browser;

    private String os;

    private String deviceType;

    @Column(length = 1024)
    private String referrer;

    public ClickEvent() {}

    public ClickEvent(ShortUrl shortUrl, String ipAddress, String userAgent,
                      String browser, String os, String deviceType, String referrer) {
        this.shortUrl = shortUrl;
        this.clickedAt = LocalDateTime.now();
        this.ipAddress = ipAddress;
        this.userAgent = userAgent;
        this.browser = browser;
        this.os = os;
        this.deviceType = deviceType;
        this.referrer = referrer;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public ShortUrl getShortUrl() { return shortUrl; }
    public void setShortUrl(ShortUrl shortUrl) { this.shortUrl = shortUrl; }

    public LocalDateTime getClickedAt() { return clickedAt; }
    public void setClickedAt(LocalDateTime clickedAt) { this.clickedAt = clickedAt; }

    public String getIpAddress() { return ipAddress; }
    public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }

    public String getUserAgent() { return userAgent; }
    public void setUserAgent(String userAgent) { this.userAgent = userAgent; }

    public String getBrowser() { return browser; }
    public void setBrowser(String browser) { this.browser = browser; }

    public String getOs() { return os; }
    public void setOs(String os) { this.os = os; }

    public String getDeviceType() { return deviceType; }
    public void setDeviceType(String deviceType) { this.deviceType = deviceType; }

    public String getReferrer() { return referrer; }
    public void setReferrer(String referrer) { this.referrer = referrer; }
}
