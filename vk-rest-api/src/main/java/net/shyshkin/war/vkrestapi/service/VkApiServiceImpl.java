package net.shyshkin.war.vkrestapi.service;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.vk.api.sdk.client.AbstractQueryBuilder;
import com.vk.api.sdk.client.Lang;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.users.Fields;
import com.vk.api.sdk.objects.users.UserFull;
import com.vk.api.sdk.objects.users.responses.GetResponse;
import com.vk.api.sdk.queries.execute.ExecuteBatchQuery;
import com.vk.api.sdk.queries.users.UsersGetQuery;
import com.vk.api.sdk.queries.users.UsersSearchQuery;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
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
    public Mono<String> getUsersJson(List<Integer> userIds) {
        return Flux.fromIterable(userIds)
                .map(String::valueOf)
                .collectList()
                .map(this::getUsersJsonInternal)
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Flux<UserFull> searchUsers(SearchRequest searchRequest) {
        return Mono.fromSupplier(() -> searchUsersInternal(searchRequest))
                .flatMapIterable(Function.identity())
                .subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<String> searchUsersJson(SearchRequest searchRequest) {
        return Mono.fromSupplier(() -> searchUsersJsonInternal(searchRequest));
    }

    @Override
    public Mono<JsonElement> searchUsersBatch(List<SearchRequest> searchRequests) {
        return Mono.just(searchRequests)
                .map(this::searchUsersBatchInternal);
    }

    @Override
    public Mono<String> searchUsersBatchJson(List<SearchRequest> searchRequests) {
        return Mono.just(searchRequests)
                .map(this::searchUsersBatchJsonInternal);
    }

    private List<GetResponse> getUsersInternal(List<String> ids) {
        try {
            return usersGetQuery(ids).execute();
        } catch (ClientException | ApiException e) {
            throw new RuntimeException(e);
        }
    }

    private String getUsersJsonInternal(List<String> ids) {
        try {
            return usersGetQuery(ids).executeAsString();
        } catch (ClientException e) {
            throw new RuntimeException(e);
        }
    }

    private UsersGetQuery usersGetQuery(List<String> ids) {
        return vk.users().get(actor)
                .userIds(ids)
                .fields(readFields())
                .lang(Lang.RU);
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

    @SneakyThrows({ApiException.class, ClientException.class})
    private List<UserFull> searchUsersInternal(SearchRequest searchRequest) {
        return usersSearchQuery(searchRequest)
                .execute()
                .getItems();
    }

    @SneakyThrows(ClientException.class)
    private String searchUsersJsonInternal(SearchRequest searchRequest) {
        return usersSearchQuery(searchRequest)
                .executeAsString();
    }

    private UsersSearchQuery usersSearchQuery(SearchRequest searchRequest) {
        return vk.users().search(actor)
                .q(searchRequest.getName())
                .birthDay(searchRequest.getBday())
                .birthMonth(searchRequest.getBmonth())
                .birthYear(searchRequest.getByear())
                .city(searchRequest.getCity())
                .fields(readFields());
    }

    @SneakyThrows
    private JsonElement searchUsersBatchInternal(List<SearchRequest> searchRequests) {
        return searchUsersBatchQuery(searchRequests)
                .execute();
    }

    @SneakyThrows
    private String searchUsersBatchJsonInternal(List<SearchRequest> searchRequests) {
        return searchUsersBatchQuery(searchRequests)
                .executeAsString();
    }

    private ExecuteBatchQuery searchUsersBatchQuery(List<SearchRequest> searchRequests) {
        var usersSearchQueries = searchRequests.stream()
                .map(this::usersSearchQuery)
                .map(searchQuery -> (AbstractQueryBuilder) searchQuery)
                .collect(Collectors.toList());
        return vk.execute().batch(actor, usersSearchQueries);
    }
}
