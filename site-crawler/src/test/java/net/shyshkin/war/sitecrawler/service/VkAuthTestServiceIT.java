package net.shyshkin.war.sitecrawler.service;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.war.sitecrawler.common.CommonAbstractTest;
import net.shyshkin.war.sitecrawler.dto.ClientCredentials;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import reactor.test.StepVerifier;

@Slf4j
@TestPropertySource(properties = {
        "app.vk-api.auth-url=${MOCKSERVER_URL}/access_token"
})
class VkAuthTestServiceIT extends CommonAbstractTest {

    @Autowired
    VkAuthTestService vkAuthTestService;

    @Test
    @DisplayName("Searching for user only by name should return `Was called` from mock-server")
    void gettingAccessToken_shouldReturnAccessToken() {
        //given
        ClientCredentials clientCredentials = ClientCredentials.builder()
                .clientId("123")
                .clientSecret("12345678")
                .build();

        //when
        var accessTokenMono = vkAuthTestService.getAccessToken(clientCredentials);

        //then
        StepVerifier.create(accessTokenMono)
                .expectNextMatches(token -> "some_long_token" .equals(token.getAccessToken()))
                .verifyComplete();
    }

}