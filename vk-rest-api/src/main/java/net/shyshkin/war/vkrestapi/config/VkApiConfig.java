package net.shyshkin.war.vkrestapi.config;

import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.UserActor;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import net.shyshkin.war.vkrestapi.config.data.VkApiConfigData;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class VkApiConfig {

    @Bean
    VkApiClient vk() {
        TransportClient transportClient = new HttpTransportClient();
        return new VkApiClient(transportClient);
    }

    @Bean
    UserActor actor(VkApiConfigData configData) {
        return new UserActor(configData.getUserId(), configData.getAccessToken());
    }

}
