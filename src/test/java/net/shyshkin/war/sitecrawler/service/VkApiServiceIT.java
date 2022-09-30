package net.shyshkin.war.sitecrawler.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.war.sitecrawler.common.CommonAbstractTest;
import net.shyshkin.war.sitecrawler.dto.VkUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Slf4j
@TestPropertySource(properties = {
        "app.vk-api.base-url=${MOCKSERVER_URL}/method"
})
class VkApiServiceIT extends CommonAbstractTest {

    @Autowired
    VkApiService vkApiService;

    ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void getUser() {

        //given
        Long userId = 123L;

        //when
        Mono<VkUser> userMono = vkApiService.getUser(userId);

        //then
        StepVerifier.create(userMono)
                .consumeNextWith(vkUser -> assertThat(vkUser)
                        .isNotNull()
                        .hasNoNullFieldsOrProperties()
                        .hasFieldOrPropertyWithValue("id", 123L)
                        .hasFieldOrPropertyWithValue("firstName", "Art")
                        .hasFieldOrPropertyWithValue("lastName", "Shyshkin")
                )
                .verifyComplete();
    }

    @Test
    void getUserJson() {
        //given
        Long userId = 123L;

        //when
        Mono<String> userMono = vkApiService.getUserJson(userId);

        //then
        StepVerifier.create(userMono)
                .consumeNextWith(response -> assertThat(response)
                        .isNotNull()
                        .satisfies(jsonString -> {

                            JsonNode jsonNode = objectMapper.readValue(jsonString, JsonNode.class);
                            assertAll(
                                    () -> assertThat(jsonNode.at("/response/0/first_name").asText()).isEqualTo("Art"),
                                    () -> assertThat(jsonNode.at("/response/0/last_name").asText()).isEqualTo("Shyshkin"),
                                    () -> assertThat(jsonNode.at("/response/0/id").asLong()).isEqualTo(123L)
                            );
                        })
                )
                .verifyComplete();
    }

}