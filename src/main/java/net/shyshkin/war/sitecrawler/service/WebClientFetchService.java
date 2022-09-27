package net.shyshkin.war.sitecrawler.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriUtils;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
public class WebClientFetchService implements FetchService {

    private final WebClient webClient;

    @Value("${app.site.url-pattern}")
    String webSiteUrlPattern;

    @Override
    public Mono<String> fetchPage(String reservistName) {
        String encode = UriUtils.encode(reservistName, StandardCharsets.UTF_8);
        URI uri = URI.create(webSiteUrlPattern.replace("{reservist_name}", encode));
        return webClient
                .get().uri(uri)
                .retrieve()
                .bodyToMono(String.class);
    }
}
