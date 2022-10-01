package net.shyshkin.war.sitecrawler.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties("app.vk-api")
public class VkApiConfigData {
    private String authUrl;
    private String apiVersion;
    private String accessToken;
    private String baseUrl;
    private String searchEndpoint;
    private String userEndpoint;
    private String fields;
}
