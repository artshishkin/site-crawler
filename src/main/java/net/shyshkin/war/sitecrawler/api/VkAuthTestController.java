package net.shyshkin.war.sitecrawler.api;

import lombok.RequiredArgsConstructor;
import net.shyshkin.war.sitecrawler.dto.ClientCredentials;
import net.shyshkin.war.sitecrawler.dto.VkAccessToken;
import net.shyshkin.war.sitecrawler.service.VkAuthTestService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/vk/auth")
@RequiredArgsConstructor
public class VkAuthTestController {

    private final VkAuthTestService vkAuthTestService;

    @GetMapping("/token")
    public Mono<VkAccessToken> getAccessToken(ClientCredentials clientCredentials) {
        return vkAuthTestService.getAccessToken(clientCredentials);
    }

}
