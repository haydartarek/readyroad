package com.readyroad.readyroadbackend.config;

import org.springframework.context.annotation.Configuration;
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

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/images/**")
                .addResourceLocations("classpath:/public/images/", "file:public/images/")
                .setCachePeriod(3600);
    }
}
