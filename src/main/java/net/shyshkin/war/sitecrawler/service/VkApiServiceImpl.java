package net.shyshkin.war.sitecrawler.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.war.sitecrawler.config.VkApiConfigData;
import net.shyshkin.war.sitecrawler.dto.SearchRequest;
import net.shyshkin.war.sitecrawler.dto.VkSearchUserResponse;
import net.shyshkin.war.sitecrawler.dto.VkUser;
import net.shyshkin.war.sitecrawler.dto.VkUserResponse;
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
        return searchUsers(searchRequest,String.class)
                .doOnNext(jsonResponse -> log.debug("Search User Response: {}", jsonResponse));
    }

    private <T> Mono<T> getUser(Long userId, Class<T> T) {
        return vkApiClient.get()
                .uri(builder -> builder
                        .path(configData.getUserEndpoint())
                        .queryParam("user_id", userId)
                        .build())
                .exchangeToMono(response -> {
                    log.debug("Status code: {}", response.statusCode());
                    log.debug("Headers: {}", response.headers().asHttpHeaders());
                    return response.bodyToMono(T);
                });
    }

    private <T> Mono<T> searchUsers(SearchRequest searchRequest, Class<T> T) {
        return vkApiClient.get()
                .uri(builder -> builder
                        .path(configData.getSearchEndpoint())
                        .queryParam("q", searchRequest.getName())
                        .queryParam("city", 1)
                        .queryParam("country", 1)
                        .queryParam("birth_day", searchRequest.getBday())
                        .queryParam("birth_month", searchRequest.getBmonth())
                        .queryParam("birth_year", searchRequest.getByear())
                        .build())
                .exchangeToMono(response -> {
                    log.debug("Status code: {}", response.statusCode());
                    log.debug("Headers: {}", response.headers().asHttpHeaders());
                    return response.bodyToMono(T);
                });
    }

}
