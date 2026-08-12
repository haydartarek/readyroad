package com.readyroad.readyroadbackend.marketing.analytics;

import java.net.http.HttpClient;
import java.time.Duration;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class GoogleReadOnlyHttpClient {

    private final RestClient client;

    public GoogleReadOnlyHttpClient() {
        HttpClient httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(10))
                .followRedirects(HttpClient.Redirect.NORMAL)
                .build();
        JdkClientHttpRequestFactory requestFactory = new JdkClientHttpRequestFactory(httpClient);
        requestFactory.setReadTimeout(Duration.ofSeconds(45));
        this.client = RestClient.builder().requestFactory(requestFactory).build();
    }

    public RestClient client() {
        return client;
    }
}
