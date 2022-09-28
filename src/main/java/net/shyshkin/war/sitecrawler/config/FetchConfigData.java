package net.shyshkin.war.sitecrawler.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties("app.fetch")
public class FetchConfigData {
    private String baseUrl;
    private String searchPattern;
    private String searchPatternAge;
    private String userPattern;
}
