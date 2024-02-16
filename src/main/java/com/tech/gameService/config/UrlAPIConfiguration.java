package com.tech.gameService.config;


import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

@Configuration
@PropertySource("url-configuration.properties")
@Getter
public class UrlAPIConfiguration {

    @Value("${api.rating.url}")
    private String urlApiRating;
}
