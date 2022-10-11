package net.shyshkin.war.vkrestapi.api;

import com.vk.api.sdk.objects.users.UserFull;
import lombok.RequiredArgsConstructor;
import net.shyshkin.war.vkrestapi.service.VkApiService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class ProxyController {

    private final VkApiService vkApiService;

    @GetMapping(value = "/users/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<UserFull> getUser(@PathVariable("id") Integer userId) {
        return vkApiService.getUser(userId);
    }

}
