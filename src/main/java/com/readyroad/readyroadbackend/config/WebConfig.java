package com.readyroad.readyroadbackend.config;

import java.nio.file.Path;
import java.time.Duration;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.CacheControl;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Web MVC configuration.
 *
 * Spring Boot already uses UTF-8 for request/response handling, so this class
 * only keeps the explicit static resource mapping we still need.
 */
@Configuration
public class WebConfig implements WebMvcConfigurer {

    private final String editorialImageDirectory;

    public WebConfig(
            @Value("${rijvia.editorial.images.directory:data/editorial-images}")
            String editorialImageDirectory) {
        this.editorialImageDirectory = editorialImageDirectory;
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/signs/**")
                .addResourceLocations("file:public/images/signs/")
                .setCachePeriod(3600);

        registry.addResourceHandler("/images/quiz/**")
                .addResourceLocations("file:public/images/quiz/")
                .setCachePeriod(3600);

        String optimizedImages = Path.of(editorialImageDirectory)
                .toAbsolutePath()
                .normalize()
                .resolve("optimized")
                .toUri()
                .toString();
        registry.addResourceHandler("/images/articles/**")
                .addResourceLocations(optimizedImages)
                .setCacheControl(CacheControl.maxAge(Duration.ofDays(365)).cachePublic().immutable());
    }
}
