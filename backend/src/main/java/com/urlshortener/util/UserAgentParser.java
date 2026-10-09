package com.urlshortener.util;

public final class UserAgentParser {

    private UserAgentParser() {}

    public static String parseBrowser(String ua) {
        if (ua == null || ua.isBlank()) return "Unknown";
        String lower = ua.toLowerCase();
        if (lower.contains("edg/")) return "Edge";
        if (lower.contains("opr/") || lower.contains("opera")) return "Opera";
        if (lower.contains("chrome/") && !lower.contains("chromium")) return "Chrome";
        if (lower.contains("safari/") && !lower.contains("chrome")) return "Safari";
        if (lower.contains("firefox/")) return "Firefox";
        if (lower.contains("curl")) return "cURL";
        if (lower.contains("postman")) return "Postman";
        return "Other";
    }

    public static String parseOs(String ua) {
        if (ua == null || ua.isBlank()) return "Unknown";
        String lower = ua.toLowerCase();
        if (lower.contains("windows nt")) return "Windows";
        if (lower.contains("macintosh") || lower.contains("mac os x")) return "macOS";
        if (lower.contains("iphone") || lower.contains("ipad")) return "iOS";
        if (lower.contains("android")) return "Android";
        if (lower.contains("linux")) return "Linux";
        return "Other";
    }

    public static String parseDeviceType(String ua) {
        if (ua == null || ua.isBlank()) return "Unknown";
        String lower = ua.toLowerCase();
        if (lower.contains("ipad") || lower.contains("tablet")) return "Tablet";
        if (lower.contains("mobile") || lower.contains("android") || lower.contains("iphone")) return "Mobile";
        return "Desktop";
    }
}
