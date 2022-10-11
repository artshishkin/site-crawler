package net.shyshkin.war.vkrestapi.config.data;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties("app.vk-api")
public class VkApiConfigData {
    private Integer userId;
    private String accessToken;
    private List<String> fields;
}
