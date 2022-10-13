package net.shyshkin.war.vkstreamingapi.config;

import com.google.gson.Gson;
import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.ServiceActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.streaming.responses.GetServerUrlResponse;
import com.vk.api.sdk.streaming.clients.VkStreamingApiClient;
import com.vk.api.sdk.streaming.clients.actors.StreamingActor;
import lombok.RequiredArgsConstructor;
import net.shyshkin.war.vkstreamingapi.config.data.VkStreamingApiConfigData;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class VkStreamingApiConfig {

    private final VkStreamingApiConfigData configData;

    @Bean
    TransportClient transportClient() {
        return new HttpTransportClient();
    }

    @Bean
    VkApiClient vkClient() {
        return new VkApiClient(transportClient());
    }

    @Bean
    VkStreamingApiClient streamingClient() {
        return new VkStreamingApiClient(transportClient());
    }

    @Bean
    ServiceActor serviceActor() {
        return new ServiceActor(configData.getAppId(), configData.getClientAccessToken());
    }

    @Bean
    StreamingActor streamingActor() throws ClientException, ApiException {
        GetServerUrlResponse getServerUrlResponse = vkClient().streaming().getServerUrl(serviceActor()).execute();
        return new StreamingActor(getServerUrlResponse.getEndpoint(), getServerUrlResponse.getKey());
    }

    @Bean
    Gson gson() {
        return new Gson();
    }

}
