package net.shyshkin.war.vkstreamingapi.service;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.ServiceActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.objects.wall.responses.GetByIdExtendedResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.war.vkstreamingapi.exception.ApiWallException;
import org.springframework.stereotype.Service;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Slf4j
@Service
@RequiredArgsConstructor
public class ContentService {

    public static final String CONTENT_SEQUENCE = "/wall";
    private final VkApiClient vkClient;
    private final ServiceActor serviceActor;

    public Mono<GetByIdExtendedResponse> readContent(String contentUrl) {
        return Mono.just(contentUrl)
                .filter(url -> url.contains(CONTENT_SEQUENCE))
                .map(url -> UriComponentsBuilder.fromUriString(url).build())
                .mapNotNull(UriComponents::getPath)
                .map(path -> path.replace(CONTENT_SEQUENCE, ""))
                .map(this::fetchContent)
                .subscribeOn(Schedulers.boundedElastic());
    }

    private GetByIdExtendedResponse fetchContent(String postId) {
        try {
            log.debug("Getting content of post with id: {}", postId);
            return vkClient.wall().getByIdExtended(serviceActor, postId).execute();
        } catch (ApiException | ClientException e) {
            throw new ApiWallException(e);
        }
    }

}
