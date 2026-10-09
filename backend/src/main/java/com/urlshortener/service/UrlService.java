package com.urlshortener.service;

import com.urlshortener.dto.AnalyticsResponse;
import com.urlshortener.dto.CreateUrlRequest;
import com.urlshortener.dto.ShortUrlResponse;
import com.urlshortener.model.ClickEvent;
import com.urlshortener.model.ShortUrl;
import com.urlshortener.repository.ClickEventRepository;
import com.urlshortener.repository.ShortUrlRepository;
import com.urlshortener.util.Base62;
import com.urlshortener.util.UserAgentParser;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.net.URI;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class UrlService {

    private final ShortUrlRepository shortUrlRepository;
    private final ClickEventRepository clickEventRepository;

    @Value("${app.base-url:http://localhost:8080}")
    private String baseUrl;

    public UrlService(ShortUrlRepository shortUrlRepository, ClickEventRepository clickEventRepository) {
        this.shortUrlRepository = shortUrlRepository;
        this.clickEventRepository = clickEventRepository;
    }

    @Transactional
    public ShortUrlResponse createShortUrl(CreateUrlRequest req) {
        String originalUrl = req.getOriginalUrl().trim();
        if (!originalUrl.startsWith("http://") && !originalUrl.startsWith("https://")) {
            originalUrl = "https://" + originalUrl;
        }

        String code = req.getCustomCode() != null && !req.getCustomCode().isBlank()
                ? req.getCustomCode().trim()
                : null;

        if (code != null) {
            if (shortUrlRepository.existsByShortCode(code)) {
                throw new IllegalArgumentException("Short code '" + code + "' is already in use.");
            }
        } else {
            // Generate unique Base62 code
            do {
                code = Base62.generateRandomCode(6);
            } while (shortUrlRepository.existsByShortCode(code));
        }

        String title = req.getTitle() != null && !req.getTitle().isBlank()
                ? req.getTitle().trim()
                : extractDomain(originalUrl);

        ShortUrl shortUrl = new ShortUrl(code, originalUrl, title);
        shortUrl = shortUrlRepository.save(shortUrl);

        return mapToResponse(shortUrl);
    }

    @Transactional
    public String resolveAndTrack(String shortCode, HttpServletRequest request) {
        ShortUrl shortUrl = shortUrlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new NoSuchElementException("Short URL not found for code: " + shortCode));

        // Increment click count
        shortUrl.setClickCount(shortUrl.getClickCount() + 1);
        shortUrlRepository.save(shortUrl);

        // Record click event
        String userAgent = request.getHeader("User-Agent");
        String ip = extractClientIp(request);
        String referrer = request.getHeader("Referer");
        if (referrer != null && referrer.length() > 1024) {
            referrer = referrer.substring(0, 1024);
        }

        String browser = UserAgentParser.parseBrowser(userAgent);
        String os = UserAgentParser.parseOs(userAgent);
        String device = UserAgentParser.parseDeviceType(userAgent);

        ClickEvent clickEvent = new ClickEvent(
                shortUrl, ip, userAgent, browser, os, device,
                (referrer != null && !referrer.isBlank()) ? referrer : "Direct"
        );
        clickEventRepository.save(clickEvent);

        return shortUrl.getOriginalUrl();
    }

    @Transactional(readOnly = true)
    public List<ShortUrlResponse> getAllUrls() {
        return shortUrlRepository.findAll().stream()
                .sorted(Comparator.comparing(ShortUrl::getCreatedAt).reversed())
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public AnalyticsResponse getAnalytics(String shortCode) {
        ShortUrl shortUrl = shortUrlRepository.findByShortCode(shortCode)
                .orElseThrow(() -> new NoSuchElementException("Short URL not found for code: " + shortCode));

        List<ClickEvent> clicks = clickEventRepository.findByShortUrlOrderByClickedAtDesc(shortUrl);

        AnalyticsResponse response = new AnalyticsResponse();
        response.setShortCode(shortUrl.getShortCode());
        response.setOriginalUrl(shortUrl.getOriginalUrl());
        response.setTitle(shortUrl.getTitle());
        response.setTotalClicks(shortUrl.getClickCount());

        Map<String, Long> browsers = clicks.stream()
                .collect(Collectors.groupingBy(ClickEvent::getBrowser, Collectors.counting()));
        response.setBrowsers(browsers);
        response.setTopBrowser(getTopEntry(browsers));

        Map<String, Long> os = clicks.stream()
                .collect(Collectors.groupingBy(ClickEvent::getOs, Collectors.counting()));
        response.setOperatingSystems(os);
        response.setTopOs(getTopEntry(os));

        Map<String, Long> referrers = clicks.stream()
                .map(ClickEvent::getReferrer)
                .filter(Objects::nonNull)
                .collect(Collectors.groupingBy(r -> r, Collectors.counting()));
        response.setReferrers(referrers);
        response.setUniqueReferrers(referrers.size());

        List<AnalyticsResponse.ClickDetail> recent = clicks.stream()
                .limit(25)
                .map(c -> new AnalyticsResponse.ClickDetail(
                        c.getClickedAt(),
                        c.getIpAddress(),
                        c.getBrowser(),
                        c.getOs(),
                        c.getDeviceType(),
                        c.getReferrer()
                ))
                .collect(Collectors.toList());
        response.setRecentClicks(recent);

        return response;
    }

    @Transactional
    public void deleteUrl(Long id) {
        ShortUrl shortUrl = shortUrlRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("URL with ID " + id + " not found."));
        clickEventRepository.deleteByShortUrl(shortUrl);
        shortUrlRepository.delete(shortUrl);
    }

    private ShortUrlResponse mapToResponse(ShortUrl entity) {
        String cleanBase = baseUrl.endsWith("/") ? baseUrl.substring(0, baseUrl.length() - 1) : baseUrl;
        String fullShortUrl = cleanBase + "/" + entity.getShortCode();
        return new ShortUrlResponse(
                entity.getId(),
                entity.getShortCode(),
                fullShortUrl,
                entity.getOriginalUrl(),
                entity.getTitle(),
                entity.getClickCount(),
                entity.getCreatedAt()
        );
    }

    private String getTopEntry(Map<String, Long> map) {
        return map.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse("None");
    }

    private String extractDomain(String url) {
        try {
            URI uri = new URI(url);
            String domain = uri.getHost();
            return (domain != null && !domain.isBlank()) ? domain : url;
        } catch (Exception e) {
            return url;
        }
    }

    private String extractClientIp(HttpServletRequest request) {
        String header = request.getHeader("X-Forwarded-For");
        if (header != null && !header.isBlank()) {
            return header.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }
}
