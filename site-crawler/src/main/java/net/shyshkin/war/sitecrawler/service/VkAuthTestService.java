package net.shyshkin.war.sitecrawler.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.war.sitecrawler.config.VkApiConfigData;
import net.shyshkin.war.sitecrawler.dto.ClientCredentials;
import net.shyshkin.war.sitecrawler.dto.VkAccessToken;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class VkAuthTestService {

    private final WebClient vkAuthClient;
    private final VkApiConfigData configData;

    public Mono<VkAccessToken> getAccessToken(ClientCredentials clientCredentials) {
//        https://oauth.vk.com/access_token?client_id= + CLIENT_ID + &client_secret= + CLIENT_SECRET + &v=5.194&grant_type=client_credentials
        return vkAuthClient.get().uri(builder -> builder
                        .queryParam("client_id", clientCredentials.getClientId())
                        .queryParam("client_secret", clientCredentials.getClientSecret())
                        .queryParam("v", configData.getApiVersion())
                        .queryParam("grant_type", "client_credentials")
                        .build()
                )
                .exchangeToMono(clientResponse -> {
                    log.debug("Status code: {}", clientResponse.statusCode());
                    log.debug("Headers: {}", clientResponse.headers().asHttpHeaders());
                    return clientResponse.bodyToMono(VkAccessToken.class);
                });

    }

}
