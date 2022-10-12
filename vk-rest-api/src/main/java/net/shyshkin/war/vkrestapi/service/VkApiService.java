package net.shyshkin.war.vkrestapi.service;

import com.vk.api.sdk.objects.users.UserFull;
import net.shyshkin.war.vkrestapi.dto.SearchRequest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public interface VkApiService {

    Mono<UserFull> getUser(Integer userId);

    Flux<UserFull> getUsers(List<Integer> userIds);

    Flux<UserFull> searchUsers(SearchRequest searchRequest);

}
