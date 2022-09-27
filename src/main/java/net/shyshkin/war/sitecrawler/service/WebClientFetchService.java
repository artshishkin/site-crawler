package net.shyshkin.war.sitecrawler.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.util.UriUtils;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.nio.charset.StandardCharsets;

@Slf4j
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
        log.debug("Fetching {}", uri);
        return webClient
                .get().uri(uri)
                .exchangeToMono(clientResponse -> {
                    log.debug("Status code: {}", clientResponse.statusCode());
                    log.debug("Headers as HttpHeaders: {}", clientResponse.headers().asHttpHeaders());
                    return clientResponse.bodyToMono(String.class);
                })
                .doOnNext(body -> log.debug("Response body: {}", body))
                .log();
    }
}
