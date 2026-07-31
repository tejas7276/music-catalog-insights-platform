// backend/src/main/java/com/musiccatalog/config/WebClientConfig.java
package com.musiccatalog.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.http.codec.json.Jackson2JsonDecoder;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    public WebClient itunesWebClient(
            @Value("${itunes.api.base-url}") String baseUrl,
            ObjectMapper objectMapper
    ) {
        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer.defaultCodecs().jackson2JsonDecoder(
                        new Jackson2JsonDecoder(
                                objectMapper,
                                MediaType.APPLICATION_JSON,
                                MediaType.valueOf("text/javascript"),
                                MediaType.valueOf("application/javascript"),
                                MediaType.valueOf("application/x-javascript")
                        )
                ))
                .build();

        return WebClient.builder()
                .baseUrl(baseUrl)
                .exchangeStrategies(strategies)
                .build();
    }

    @Bean
    public WebClient geminiWebClient(@Value("${ai.gemini.base-url}") String baseUrl) {
        return WebClient.builder()
                .baseUrl(baseUrl)
                .build();
    }
}