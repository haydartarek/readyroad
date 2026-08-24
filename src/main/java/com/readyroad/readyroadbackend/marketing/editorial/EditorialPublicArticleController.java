package com.readyroad.readyroadbackend.marketing.editorial;

import java.time.Duration;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/articles")
@RequiredArgsConstructor
public class EditorialPublicArticleController {

    private static final CacheControl PUBLIC_CACHE = CacheControl.maxAge(Duration.ofMinutes(15)).cachePublic();

    private final EditorialPublicArticleService service;

    @GetMapping
    public ResponseEntity<List<EditorialPublicArticleDtos.Summary>> summaries(
            @RequestParam(defaultValue = "EN") String language) {
        try {
            return ResponseEntity.ok()
                    .cacheControl(PUBLIC_CACHE)
                    .body(service.summaries(language));
        } catch (IllegalArgumentException error) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, error.getMessage());
        }
    }

    @GetMapping("/related")
    public ResponseEntity<List<EditorialPublicArticleDtos.Summary>> related(
            @RequestParam(defaultValue = "EN") String language,
            @RequestParam String targetPath,
            @RequestParam(defaultValue = "3") int limit) {
        try {
            return ResponseEntity.ok()
                    .cacheControl(PUBLIC_CACHE)
                    .body(service.related(language, targetPath, limit));
        } catch (IllegalArgumentException error) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, error.getMessage());
        }
    }

    @GetMapping("/{slug}")
    public ResponseEntity<EditorialPublicArticleDtos.Article> article(
            @PathVariable String slug,
            @RequestParam(defaultValue = "EN") String language) {
        try {
            return service.article(language, slug)
                    .map(article -> ResponseEntity.ok()
                            .cacheControl(PUBLIC_CACHE)
                            .body(article))
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        } catch (IllegalArgumentException error) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, error.getMessage());
        }
    }
}
