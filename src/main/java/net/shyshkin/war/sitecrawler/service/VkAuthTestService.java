package net.shyshkin.war.sitecrawler.service;

import net.shyshkin.war.sitecrawler.dto.ClientCredentials;
import net.shyshkin.war.sitecrawler.dto.VkAccessToken;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class VkAuthTestService {

    public Mono<VkAccessToken> getAccessToken(ClientCredentials clientCredentials) {
        throw new RuntimeException("Not implemented yet");
    }

}
