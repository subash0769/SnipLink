package com.urlshortener.config;

import com.urlshortener.dto.CreateUrlRequest;
import com.urlshortener.repository.ShortUrlRepository;
import com.urlshortener.service.UrlService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataInitializer {

    @Bean
    CommandLineRunner initData(ShortUrlRepository repository, UrlService urlService) {
        return args -> {
            if (repository.count() == 0) {
                urlService.createShortUrl(new CreateUrlRequest(
                        "https://spring.io/projects/spring-boot",
                        "spring",
                        "Spring Boot Official Documentation"
                ));
                urlService.createShortUrl(new CreateUrlRequest(
                        "https://vuejs.org/guide/introduction.html",
                        "vuejs",
                        "Vue 3 Getting Started Guide"
                ));
                urlService.createShortUrl(new CreateUrlRequest(
                        "https://hub.docker.com",
                        "docker",
                        "Docker Hub Container Registry"
                ));
                System.out.println("[DataInitializer] Sample short URLs seeded: /spring, /vuejs, /docker");
            }
        };
    }
}
