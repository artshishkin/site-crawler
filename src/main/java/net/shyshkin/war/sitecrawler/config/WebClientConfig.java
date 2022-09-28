package net.shyshkin.war.sitecrawler.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class WebClientConfig {

    @Bean
    WebClient webClient(FetchConfigData fetchConfigData) {
        return WebClient.builder()
                .baseUrl(fetchConfigData.getBaseUrl())
                .defaultHeader(HttpHeaders.ACCEPT_LANGUAGE,"ru-RU")
                .build();
    }

}
