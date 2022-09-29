package net.shyshkin.war.sitecrawler.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.war.sitecrawler.config.VkApiConfigData;
import net.shyshkin.war.sitecrawler.dto.VkUser;
import net.shyshkin.war.sitecrawler.dto.VkUserResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class VkApiServiceImpl implements VkApiService {

    private final WebClient vkApiClient;
    private final VkApiConfigData configData;

    @Override
    public Mono<VkUser> getUser(Long userId) {
        return vkApiClient.get()
                .uri(builder -> builder
                        .path(configData.getUserEndpoint())
                        .queryParam("user_id", userId)
                        .build())
                .exchangeToMono(response -> {
                    log.debug("Status code: {}", response.statusCode());
                    log.debug("Headers: {}", response.headers().asHttpHeaders());
                    return response.bodyToMono(VkUserResponse.class)
                            .flatMapIterable(VkUserResponse::getResponse)
                            .next();
                })
                .doOnNext(vkUser -> log.debug("User: {}", vkUser));
    }

    @Override
    public Mono<String> getUserJson(Long userId) {
        return vkApiClient.get()
                .uri(builder -> builder
                        .path(configData.getUserEndpoint())
                        .queryParam("user_id", userId)
                        .build())
                .exchangeToMono(response -> {
                    log.debug("Status code: {}", response.statusCode());
                    log.debug("Headers: {}", response.headers().asHttpHeaders());
                    return response.bodyToMono(String.class);
                })
                .doOnNext(vkUser -> log.debug("User Response: {}", vkUser));
    }
}
