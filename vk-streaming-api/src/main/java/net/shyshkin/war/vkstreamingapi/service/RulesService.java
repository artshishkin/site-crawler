package net.shyshkin.war.vkstreamingapi.service;

import com.vk.api.sdk.streaming.clients.VkStreamingApiClient;
import com.vk.api.sdk.streaming.clients.actors.StreamingActor;
import com.vk.api.sdk.streaming.exceptions.StreamingApiException;
import com.vk.api.sdk.streaming.exceptions.StreamingClientException;
import com.vk.api.sdk.streaming.objects.StreamingRule;
import com.vk.api.sdk.streaming.objects.responses.StreamingGetRulesResponse;
import com.vk.api.sdk.streaming.objects.responses.StreamingResponse;
import lombok.RequiredArgsConstructor;
import net.shyshkin.war.vkstreamingapi.exception.ApiRulesException;
import net.shyshkin.war.vkstreamingapi.model.VkStreamingRule;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.function.Consumer;

@Service
@RequiredArgsConstructor
public class RulesService {

    private final VkStreamingApiClient streamingClient;
    private final StreamingActor streamingActor;

    public Flux<StreamingRule> getRules() {
        return Mono.fromSupplier(this::requestRules)
                .doOnNext(checkNoErrors())
                .subscribeOn(Schedulers.boundedElastic())
                .flatMapIterable(StreamingGetRulesResponse::getRules);
    }

    public Mono<StreamingResponse> addRule(VkStreamingRule rule) {
        return Mono.just(rule)
                .map(this::createRule)
                .doOnNext(checkNoErrors())
                .subscribeOn(Schedulers.boundedElastic());
    }

    public Mono<StreamingResponse> deleteRule(String tag) {
        return Mono.just(tag)
                .map(this::deleteRuleInternal)
                .doOnNext(checkNoErrors())
                .subscribeOn(Schedulers.boundedElastic());
    }

    private StreamingGetRulesResponse requestRules() {
        try {
            return streamingClient
                    .rules()
                    .get(streamingActor)
                    .execute();
        } catch (StreamingClientException | StreamingApiException e) {
            throw new ApiRulesException(e);
        }
    }

    private StreamingResponse createRule(VkStreamingRule rule) {
        try {
            return streamingClient.rules().add(streamingActor, rule.getTag(), rule.getValue()).execute();
        } catch (StreamingClientException | StreamingApiException e) {
            throw new ApiRulesException(e);
        }
    }

    private StreamingResponse deleteRuleInternal(String tag) {
        try {
            return streamingClient.rules().delete(streamingActor, tag).execute();
        } catch (StreamingClientException | StreamingApiException e) {
            throw new ApiRulesException(e);
        }
    }

    private Consumer<StreamingResponse> checkNoErrors() {
        return response -> {
            if (response.getError() != null) {
                throw new ApiRulesException(response.getError().getMessage());
            }
        };
    }

}
