package net.shyshkin.war.sitecrawler.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.war.sitecrawler.common.CommonAbstractTest;
import net.shyshkin.war.sitecrawler.dto.SearchRequest;
import net.shyshkin.war.sitecrawler.dto.VkUser;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import reactor.core.publisher.Flux;
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

    @Test
    void searchUsers() {

        //given
        SearchRequest searchRequest = SearchRequest.builder()
                .name("СКАЧКОВ СЕРГЕЙ")
                .bday(29)
                .bmonth(3)
                .byear(1992)
                .build();

        //when
        Flux<VkUser> userFlux = vkApiService.searchUsers(searchRequest);

        //then
        StepVerifier.create(userFlux)
                .thenConsumeWhile(
                        vkUser -> true,
                        vkUser -> assertAll(
                                () -> assertThat(vkUser)
                                        .isNotNull()
                                        .hasNoNullFieldsOrProperties()
                                        .hasFieldOrPropertyWithValue("firstName", "Сергей")
                                        .hasFieldOrPropertyWithValue("lastName", "Скачков"),
                                () -> assertThat(vkUser.getId()).isIn(207667411L, 565294160L)
                        )
                )
                .verifyComplete();
    }

    @Test
    void searchUsersJson() {

        //given
        SearchRequest searchRequest = SearchRequest.builder()
                .name("СКАЧКОВ СЕРГЕЙ")
                .bday(29)
                .bmonth(3)
                .byear(1992)
                .build();

        //when
        var searchResponseJsonMono = vkApiService.searchUsersJson(searchRequest);

        //then
        StepVerifier.create(searchResponseJsonMono)
                .consumeNextWith(vkUser -> assertThat(vkUser)
                        .isNotNull()
                        .satisfies(jsonString -> {
                            log.debug("Response from server:\n{}", jsonString);
                            JsonNode jsonNode = objectMapper.readValue(jsonString, JsonNode.class);
                            assertAll(
                                    () -> assertThat(jsonNode.at("/response/count").asInt()).isEqualTo(2),
                                    () -> assertThat(jsonNode.at("/response/items/0/id").asLong()).isEqualTo(565294160L),
                                    () -> assertThat(jsonNode.at("/response/items/0/first_name").asText()).isEqualTo("Сергей"),
                                    () -> assertThat(jsonNode.at("/response/items/0/last_name").asText()).isEqualTo("Скачков"),
                                    () -> assertThat(jsonNode.at("/response/items/1/id").asLong()).isEqualTo(207667411L),
                                    () -> assertThat(jsonNode.at("/response/items/1/first_name").asText()).isEqualTo("Сергей"),
                                    () -> assertThat(jsonNode.at("/response/items/1/last_name").asText()).isEqualTo("Скачков")
                            );
                        })
                )
                .verifyComplete();
    }

}