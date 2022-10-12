package net.shyshkin.war.vkrestapi.api;

import com.vk.api.sdk.objects.users.UserFull;
import lombok.RequiredArgsConstructor;
import net.shyshkin.war.vkrestapi.dto.SearchRequest;
import net.shyshkin.war.vkrestapi.service.VkApiService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class ProxyController {

    private final VkApiService vkApiService;

    @GetMapping(value = "/users/{id}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<UserFull> getUser(@PathVariable("id") Integer userId) {
        return vkApiService.getUser(userId);
    }

    @GetMapping(
            value = "/users",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_EVENT_STREAM_VALUE}
    )
    public Flux<UserFull> getUsers(@RequestParam("ids") String userIds) {
        List<Integer> ids = Arrays.stream(userIds.split(","))
                .map(Integer::parseInt)
                .collect(Collectors.toList());
        return vkApiService.getUsers(ids);
    }

    @GetMapping(
            value = "/users",
            produces = {MediaType.APPLICATION_JSON_VALUE},
            params = {"debug"}
    )
    public Mono<String> getUsersJson(@RequestParam("ids") String userIds) {
        List<Integer> ids = Arrays.stream(userIds.split(","))
                .map(Integer::parseInt)
                .collect(Collectors.toList());
        return vkApiService.getUsersJson(ids);
    }

    @GetMapping(
            value = "/search",
            produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_EVENT_STREAM_VALUE}
    )
    public Flux<UserFull> searchUser(SearchRequest searchRequest) {
        return vkApiService.searchUsers(searchRequest);
    }

}
