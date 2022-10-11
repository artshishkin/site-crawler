package net.shyshkin.war.vkrestapi.service;

import com.google.gson.Gson;
import com.vk.api.sdk.client.Lang;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.users.Fields;
import com.vk.api.sdk.objects.users.UserFull;
import com.vk.api.sdk.objects.users.responses.GetResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.war.vkrestapi.config.data.VkApiConfigData;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class VkApiServiceImpl implements VkApiService {

    private final VkApiClient vk;
    private final UserActor actor;
    private final VkApiConfigData vkApiConfigData;

    @Override
    public Mono<UserFull> getUser(Integer userId) {
        return Mono.fromSupplier(() -> getUsers(String.valueOf(userId)))
                .flatMapIterable(list -> list)
                .next()
                .subscribeOn(Schedulers.boundedElastic())
                .cast(UserFull.class);
    }

    private List<GetResponse> getUsers(List<String> ids) {
        try {
            Gson gson = new Gson();
            List<Fields> fields = vkApiConfigData.getFields()
                    .stream()
                    .map(str -> gson.fromJson(str, Fields.class))
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            return vk.users().get(actor)
                    .userIds(ids)
                    .fields(fields)
                    .lang(Lang.RU)
                    .execute();
        } catch (ClientException | ApiException e) {
            throw new RuntimeException(e);
        }
    }

    private List<GetResponse> getUsers(String... ids) {
        return getUsers(List.of(ids));
    }

}
