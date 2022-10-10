package net.shyshkin.war.sitecrawler.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class VkAccessToken {

    @JsonProperty("access_token")
    private String accessToken;

}
