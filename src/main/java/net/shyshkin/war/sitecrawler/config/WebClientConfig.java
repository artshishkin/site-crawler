package net.shyshkin.war.sitecrawler.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.web.reactive.function.client.ClientRequest;
import org.springframework.web.reactive.function.client.ExchangeFilterFunction;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class WebClientConfig {

    private final VkApiConfigData vkApiConfigData;

    @Bean
    WebClient webClient(FetchConfigData fetchConfigData) {
        return WebClient.builder()
                .baseUrl(fetchConfigData.getBaseUrl())
                .defaultHeader(HttpHeaders.ACCEPT_LANGUAGE, "ru-RU")
                .build();
    }

    @Bean
    WebClient vkAuthClient(VkApiConfigData vkApiConfigData) {
        return WebClient.builder()
                .baseUrl(vkApiConfigData.getAuthUrl())
                .build();
    }

    @Bean
    WebClient vkApiClient() {
        return WebClient.builder()
                .baseUrl(vkApiConfigData.getBaseUrl())
                .filter(defaultQueryParametersFilterFunction())
                .build();
    }

    private ExchangeFilterFunction defaultQueryParametersFilterFunction() {
        return (request, next) -> {
            URI uri = UriComponentsBuilder.fromUri(request.url())
                    .queryParam("v", vkApiConfigData.getApiVersion())
                    .queryParam("access_token", vkApiConfigData.getAccessToken())
                    .queryParam("lang", "ru")
                    .build(true)
                    .toUri();
            ClientRequest clientRequest = ClientRequest.from(request)
                    .url(uri)
                    .build();
            return next.exchange(clientRequest);
        };
    }

}
