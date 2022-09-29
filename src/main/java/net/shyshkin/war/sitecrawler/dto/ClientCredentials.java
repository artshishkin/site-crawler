package net.shyshkin.war.sitecrawler.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClientCredentials {

    private String clientId;
    private String clientSecret;

    public void setClient_id(String clientId) {
        this.clientId = clientId;
    }

    public void setClient_secret(String clientSecret) {
        this.clientSecret = clientSecret;
    }
}
