package net.shyshkin.war.vkstreamingapi.service;

import com.vk.api.sdk.streaming.clients.StreamingEventHandler;
import com.vk.api.sdk.streaming.clients.VkStreamingApiClient;
import com.vk.api.sdk.streaming.clients.actors.StreamingActor;
import com.vk.api.sdk.streaming.objects.StreamingCallbackMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

@Slf4j
@Service
@RequiredArgsConstructor
public class StreamingService {

    private final VkStreamingApiClient streamingClient;
    private final StreamingActor streamingActor;

    @EventListener
    public void startStreaming(ApplicationReadyEvent event) throws ExecutionException, InterruptedException {
        log.debug("Started Streaming service");
        streamingClient.stream().get(streamingActor, new StreamingEventHandler() {
            @Override
            public void handle(StreamingCallbackMessage message) {
                log.debug("Message received: {}", message);
            }
        }).execute();
    }

}
