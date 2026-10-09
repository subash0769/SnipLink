package com.urlshortener.repository;

import com.urlshortener.model.ClickEvent;
import com.urlshortener.model.ShortUrl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClickEventRepository extends JpaRepository<ClickEvent, Long> {
    List<ClickEvent> findByShortUrlOrderByClickedAtDesc(ShortUrl shortUrl);
    void deleteByShortUrl(ShortUrl shortUrl);
}
