package com.readyroad.readyroadbackend.marketing.youtube;

import com.readyroad.readyroadbackend.marketing.config.MarketingProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.http.CacheControl;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/youtube")
@RequiredArgsConstructor
public class YouTubePublicController {

    private final YouTubeStore store;
    private final MarketingProperties properties;

    @GetMapping("/videos")
    public ResponseEntity<YouTubeDtos.PublicPage> videos(
            @RequestParam(required = false) String pageToken) {
        int offset = parseOffset(pageToken);
        YouTubeModels.VideoPage page = store.page(offset, properties.getYoutube().getPageSize());
        if (page.channel() == null) {
            throw new ResponseStatusException(org.springframework.http.HttpStatus.SERVICE_UNAVAILABLE);
        }
        return ResponseEntity.ok()
                .cacheControl(CacheControl.maxAge(java.time.Duration.ofHours(1)).cachePublic())
                .body(YouTubeDtos.PublicPage.from(page));
    }

    private static int parseOffset(String pageToken) {
        if (pageToken == null || pageToken.isBlank()) return 0;
        try {
            int value = Integer.parseInt(pageToken);
            if (value < 0 || value > 100_000) throw new NumberFormatException();
            return value;
        } catch (NumberFormatException error) {
            throw new ResponseStatusException(org.springframework.http.HttpStatus.BAD_REQUEST, "Invalid page token");
        }
    }
}
