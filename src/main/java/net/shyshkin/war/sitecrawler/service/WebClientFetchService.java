package net.shyshkin.war.sitecrawler.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.war.sitecrawler.config.FetchConfigData;
import net.shyshkin.war.sitecrawler.dto.SearchRequest;
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
    private final FetchConfigData fetchConfigData;

    @Override
    public Mono<String> fetchSearchPage(String reservistName) {
        String encode = UriUtils.encode(reservistName, StandardCharsets.UTF_8);
        String uriString = fetchConfigData.getBaseUrl() +
                fetchConfigData.getSearchPattern().replace("{reservist_name}", encode);
        URI uri = URI.create(uriString);
        log.debug("Fetching {}", uri);
        return webClient
                .get().uri(uri)
                .exchangeToMono(clientResponse -> {
//                    log.debug("Status code: {}", clientResponse.statusCode());
//                    log.debug("Headers as HttpHeaders: {}", clientResponse.headers().asHttpHeaders());
                    return clientResponse.bodyToMono(String.class);
                })
                .doOnNext(body -> log.debug("Response body: {}", body))
                .log();
    }

    @Override
    public Mono<String> fetchSearchPage(SearchRequest searchRequest) {
        log.debug("Searching for {} with {}", searchRequest.getName(), searchRequest);
        return webClient
                .get().uri(uriBuilder -> uriBuilder
                        .path(fetchConfigData.getSearchPattern())
                        .queryParam("c[q]", searchRequest.getName())
                        .queryParam("c[bday]", searchRequest.getBday())
                        .queryParam("c[bmonth]", searchRequest.getBmonth())
                        .queryParam("c[byear]", searchRequest.getByear())
                        .queryParam("c[city]", 1)
                        .queryParam("c[country]", 1)
                        .queryParam("c[per_page]", 40)
                        .queryParam("c[name]", 1)
                        .queryParam("c[section]", "people")
                        .build()
                )
                .exchangeToMono(clientResponse -> {
                    log.debug("Status code: {}", clientResponse.statusCode());
                    log.debug("Headers as HttpHeaders: {}", clientResponse.headers().asHttpHeaders());
                    return clientResponse.bodyToMono(String.class);
                })
                .doOnNext(body -> log.debug("Response body: {}", body))
                .log();
    }

    @Override
    public Mono<String> fetchUserPage(Long userId) {
        log.debug("Getting user page {}", userId);
        return webClient
                .get().uri(fetchConfigData.getUserPattern(), "id" + userId)
                .exchangeToMono(clientResponse -> {
                    log.debug("Status code: {}", clientResponse.statusCode());
                    log.debug("Headers as HttpHeaders: {}", clientResponse.headers().asHttpHeaders());
                    return clientResponse.bodyToMono(String.class);
                })
                .doOnNext(body -> log.debug("Response body: {}", body))
                .log();
    }
}
