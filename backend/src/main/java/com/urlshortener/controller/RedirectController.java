package com.urlshortener.controller;

import com.urlshortener.service.UrlService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.NoSuchElementException;

@RestController
public class RedirectController {

    private final UrlService urlService;

    public RedirectController(UrlService urlService) {
        this.urlService = urlService;
    }

    @GetMapping("/{shortCode:[a-zA-Z0-9_-]+}")
    public ResponseEntity<Void> redirect(@PathVariable String shortCode, HttpServletRequest request) {
        try {
            String destinationUrl = urlService.resolveAndTrack(shortCode, request);
            HttpHeaders headers = new HttpHeaders();
            headers.setLocation(URI.create(destinationUrl));
            return new ResponseEntity<>(headers, HttpStatus.FOUND);
        } catch (NoSuchElementException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}
