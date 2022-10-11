package net.shyshkin.war.vkrestapi.service;

import com.vk.api.sdk.objects.users.UserFull;
import reactor.core.publisher.Mono;

public interface VkApiService {

    Mono<UserFull> getUser(Integer userId);

}
