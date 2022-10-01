package net.shyshkin.war.sitecrawler.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.war.sitecrawler.config.VkApiConfigData;
import net.shyshkin.war.sitecrawler.dto.*;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class VkApiServiceImpl implements VkApiService {

    private final WebClient vkApiClient;
    private final VkApiConfigData configData;

    @Override
    public Mono<VkUser> getUser(Long userId) {
        return getUser(userId, VkUserResponse.class)
                .flatMapIterable(VkUserResponse::getResponse)
                .next()
                .doOnNext(vkUser -> log.debug("User: {}", vkUser));
    }

    @Override
    public Mono<String> getUserJson(Long userId) {
        return getUser(userId, String.class)
                .doOnNext(jsonResponse -> log.debug("User Response: {}", jsonResponse));
    }

    @Override
    public Flux<VkUser> searchUsers(SearchRequest searchRequest) {
        return searchUsers(searchRequest, VkSearchUserResponse.class)
                .map(VkSearchUserResponse::getResponse)
                .flatMapIterable(VkSearchUserResponse.SearchUserResponse::getItems)
                .doOnNext(jsonResponse -> log.debug("Search User Response: {}", jsonResponse));
    }

    @Override
    public Mono<String> searchUsersJson(SearchRequest searchRequest) {
        return searchUsers(searchRequest, String.class)
                .doOnNext(jsonResponse -> log.debug("Search User Response: {}", jsonResponse));
    }

    @Override
    public Flux<VkCity> getCities(Pageable pageable) {
        log.debug("Getting cities...");
        return getCities(pageable, VkCitiesResponse.class)
                .map(VkCitiesResponse::getResponse)
                .flatMapIterable(VkCitiesResponse.CitiesResponse::getItems);
    }

    @Override
    public Mono<String> getCitiesJson(Pageable pageable) {
        log.debug("Getting cities full JSON...");
        return getCities(pageable, String.class);
    }

    @Override
    public Mono<Integer> getCitiesCount() {
        log.debug("Getting cities count...");
        return getCities(Pageable.ofSize(1), VkCitiesResponse.class)
                .map(VkCitiesResponse::getResponse)
                .map(VkCitiesResponse.CitiesResponse::getCount);
    }

    private <T> Mono<T> getUser(Long userId, Class<T> T) {
        return vkApiClient.get()
                .uri(builder -> builder
                        .path(configData.getUserEndpoint())
                        .queryParam("user_id", userId)
                        .queryParam("fields", configData.getFields())
                        .build())
                .exchangeToMono(response -> {
                    log.debug("Status code: {}", response.statusCode());
                    log.debug("Headers: {}", response.headers().asHttpHeaders());
                    return response.bodyToMono(T);
                });
    }

    private <T> Mono<T> searchUsers(SearchRequest searchRequest, Class<T> T) {
        log.debug("Searching for: {}", searchRequest);
        return vkApiClient.get()
                .uri(builder -> builder
                        .path(configData.getSearchEndpoint())
                        .queryParam("q", searchRequest.getName())
                        .queryParam("city", 1)
                        .queryParam("country", 1)
                        .queryParam("birth_day", searchRequest.getBday())
                        .queryParam("birth_month", searchRequest.getBmonth())
                        .queryParam("birth_year", searchRequest.getByear())
                        .queryParam("fields", configData.getFields())
                        .build())
                .exchangeToMono(response -> {
                    log.debug("Status code: {}", response.statusCode());
                    log.debug("Headers: {}", response.headers().asHttpHeaders());
                    return response.bodyToMono(T);
                });
    }

    private <T> Mono<T> getCities(Pageable pageable, Class<T> T) {
        if (pageable.getPageSize() <= 0 || pageable.getPageSize() > 1000)
            throw new IllegalArgumentException("Page size must be positive and less then 1000");
        return vkApiClient.get()
                .uri(builder -> builder
                        .path(configData.getCitiesEndpoint())
                        .queryParam("country_id", 1)
                        .queryParam("need_all", 1)
                        .queryParam("count", pageable.getPageSize())
                        .queryParam("offset", pageable.getOffset())
                        .build())
                .exchangeToMono(response -> {
                    log.debug("Status code: {}", response.statusCode());
                    log.debug("Headers: {}", response.headers().asHttpHeaders());
                    return response.bodyToMono(T);
                });
    }

}
