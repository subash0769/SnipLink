package com.urlshortener.dto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class AnalyticsResponse {
    private String shortCode;
    private String originalUrl;
    private String title;
    private long totalClicks;
    private int uniqueReferrers;
    private String topBrowser;
    private String topOs;
    private Map<String, Long> browsers;
    private Map<String, Long> operatingSystems;
    private Map<String, Long> referrers;
    private List<ClickDetail> recentClicks;

    public static class ClickDetail {
        private LocalDateTime clickedAt;
        private String ipAddress;
        private String browser;
        private String os;
        private String deviceType;
        private String referrer;

        public ClickDetail() {}

        public ClickDetail(LocalDateTime clickedAt, String ipAddress, String browser, String os, String deviceType, String referrer) {
            this.clickedAt = clickedAt;
            this.ipAddress = ipAddress;
            this.browser = browser;
            this.os = os;
            this.deviceType = deviceType;
            this.referrer = referrer;
        }

        public LocalDateTime getClickedAt() { return clickedAt; }
        public void setClickedAt(LocalDateTime clickedAt) { this.clickedAt = clickedAt; }
        public String getIpAddress() { return ipAddress; }
        public void setIpAddress(String ipAddress) { this.ipAddress = ipAddress; }
        public String getBrowser() { return browser; }
        public void setBrowser(String browser) { this.browser = browser; }
        public String getOs() { return os; }
        public void setOs(String os) { this.os = os; }
        public String getDeviceType() { return deviceType; }
        public void setDeviceType(String deviceType) { this.deviceType = deviceType; }
        public String getReferrer() { return referrer; }
        public void setReferrer(String referrer) { this.referrer = referrer; }
    }

    public AnalyticsResponse() {}

    // Getters and Setters
    public String getShortCode() { return shortCode; }
    public void setShortCode(String shortCode) { this.shortCode = shortCode; }

    public String getOriginalUrl() { return originalUrl; }
    public void setOriginalUrl(String originalUrl) { this.originalUrl = originalUrl; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public long getTotalClicks() { return totalClicks; }
    public void setTotalClicks(long totalClicks) { this.totalClicks = totalClicks; }

    public int getUniqueReferrers() { return uniqueReferrers; }
    public void setUniqueReferrers(int uniqueReferrers) { this.uniqueReferrers = uniqueReferrers; }

    public String getTopBrowser() { return topBrowser; }
    public void setTopBrowser(String topBrowser) { this.topBrowser = topBrowser; }

    public String getTopOs() { return topOs; }
    public void setTopOs(String topOs) { this.topOs = topOs; }

    public Map<String, Long> getBrowsers() { return browsers; }
    public void setBrowsers(Map<String, Long> browsers) { this.browsers = browsers; }

    public Map<String, Long> getOperatingSystems() { return operatingSystems; }
    public void setOperatingSystems(Map<String, Long> operatingSystems) { this.operatingSystems = operatingSystems; }

    public Map<String, Long> getReferrers() { return referrers; }
    public void setReferrers(Map<String, Long> referrers) { this.referrers = referrers; }

    public List<ClickDetail> getRecentClicks() { return recentClicks; }
    public void setRecentClicks(List<ClickDetail> recentClicks) { this.recentClicks = recentClicks; }
}
