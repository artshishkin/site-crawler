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
import com.vk.api.sdk.objects.users.responses.SearchResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.war.vkrestapi.config.data.VkApiConfigData;
import net.shyshkin.war.vkrestapi.dto.SearchRequest;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class VkApiServiceImpl implements VkApiService {

    private final VkApiClient vk;
    private final UserActor actor;
    private final VkApiConfigData vkApiConfigData;
    private final Gson gson;

    @Override
    public Mono<UserFull> getUser(Integer userId) {
        return Mono.fromSupplier(() -> getUsersInternal(String.valueOf(userId)))
                .flatMapIterable(list -> list)
                .next()
                .subscribeOn(Schedulers.boundedElastic())
                .cast(UserFull.class);
    }

    @Override
    public Flux<UserFull> getUsers(List<Integer> userIds) {
        return Flux.fromIterable(userIds)
                .map(String::valueOf)
                .collectList()
                .map(this::getUsersInternal)
                .flatMapIterable(Function.identity())
                .subscribeOn(Schedulers.boundedElastic())
                .cast(UserFull.class);
    }

    @Override
    public Flux<UserFull> searchUsers(SearchRequest searchRequest) {
        return Mono.fromSupplier(() -> searchUsersInternal(searchRequest))
                .flatMapIterable(Function.identity())
                .subscribeOn(Schedulers.boundedElastic());
    }

    private List<GetResponse> getUsersInternal(List<String> ids) {
        try {
            List<Fields> fields = readFields();

            return vk.users().get(actor)
                    .userIds(ids)
                    .fields(fields)
                    .lang(Lang.RU)
                    .execute();
        } catch (ClientException | ApiException e) {
            throw new RuntimeException(e);
        }
    }

    private List<Fields> readFields() {
        return vkApiConfigData.getFields()
                .stream()
                .map(str -> gson.fromJson(str, Fields.class))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private List<GetResponse> getUsersInternal(String... ids) {
        return getUsersInternal(List.of(ids));
    }

    private List<UserFull> searchUsersInternal(SearchRequest searchRequest) {

        List<Fields> fields = readFields();

        try {
            SearchResponse searchResponse = vk.users().search(actor)
                    .q(searchRequest.getName())
                    .birthDay(searchRequest.getBday())
                    .birthMonth(searchRequest.getBmonth())
                    .birthYear(searchRequest.getByear())
                    .city(searchRequest.getCity())
                    .fields(fields)
                    .execute();
            return searchResponse.getItems();
        } catch (ApiException | ClientException e) {
            throw new RuntimeException(e);
        }
    }

}
