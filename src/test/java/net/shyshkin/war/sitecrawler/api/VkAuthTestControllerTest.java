package net.shyshkin.war.sitecrawler.api;

import net.shyshkin.war.sitecrawler.dto.ClientCredentials;
import net.shyshkin.war.sitecrawler.dto.VkAccessToken;
import net.shyshkin.war.sitecrawler.service.VkAuthTestService;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@WebFluxTest(VkAuthTestController.class)
class VkAuthTestControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @MockBean
    VkAuthTestService vkAuthTestService;

    @Test
    void requestingWithCorrectClientCredentials_shouldCallVkAuthTestService() {

        //given
        VkAccessToken expectedToken = new VkAccessToken();
        expectedToken.setAccessToken("Some token");
        given(vkAuthTestService.getAccessToken(any(ClientCredentials.class))).willReturn(Mono.just(expectedToken));

        //when
        webTestClient.get().uri("/vk/auth/token?client_id=123&client_secret=12345678")
                .exchange()

                //then
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.access_token")
                .isEqualTo("Some token");
        then(vkAuthTestService).should().getAccessToken(ArgumentMatchers.eq(ClientCredentials.builder()
                .clientId("123")
                .clientSecret("12345678")
                .build()));
    }
}