package net.shyshkin.war.vkstreamingapi.config.data;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties("app.vk-streaming-api")
public class VkStreamingApiConfigData {
    private Integer appId;
    private String clientAccessToken;
}
