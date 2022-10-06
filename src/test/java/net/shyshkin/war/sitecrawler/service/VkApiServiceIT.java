package net.shyshkin.war.sitecrawler.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.war.sitecrawler.common.CommonAbstractTest;
import net.shyshkin.war.sitecrawler.dto.SearchRequest;
import net.shyshkin.war.sitecrawler.dto.VkUser;
import net.shyshkin.war.sitecrawler.exception.VkApiException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.TestPropertySource;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.time.Duration;
import java.util.concurrent.atomic.AtomicInteger;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Slf4j
@TestPropertySource(properties = {
        "app.vk-api.base-url=${MOCKSERVER_URL}/method",
        "logging.level.org.springframework.web.reactive.function.client=debug"
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
    void getUser_exception() {

        //given
        Long userId = 987654321L;

        //when
        Mono<VkUser> userMono = vkApiService.getUser(userId);

        //then
        StepVerifier.create(userMono)
                .verifyErrorSatisfies(throwable -> assertThat(throwable)
                        .isInstanceOf(VkApiException.class)
                        .hasMessageContaining("\"error_code\":5")
                        .hasMessageContaining("\"error_msg\":\"User authorization failed: access_token has expired.\"")
                );
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
                .city(1L)
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
    void searchUsers_exception() {

        //given
        SearchRequest searchRequest = SearchRequest.builder()
                .name("AUTH FAILED")
                .bday(29)
                .bmonth(3)
                .byear(1992)
                .city(1L)
                .build();

        //when
        Flux<VkUser> userFlux = vkApiService.searchUsers(searchRequest);

        //then
        StepVerifier.create(userFlux)
                .verifyErrorSatisfies(throwable -> assertThat(throwable)
                        .isInstanceOf(VkApiException.class)
                        .hasMessageContaining("\"error_code\":5")
                        .hasMessageContaining("\"error_msg\":\"User authorization failed: access_token has expired.\"")
                );
    }

    @Test
    void searchUsersJson() {

        //given
        SearchRequest searchRequest = SearchRequest.builder()
                .name("СКАЧКОВ СЕРГЕЙ")
                .bday(29)
                .bmonth(3)
                .byear(1992)
                .city(1L)
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

    @Test
    void getCities() {

        //given
        int expectedSize = 100;
        AtomicInteger counter = new AtomicInteger();

        //when
        var cityFlux = vkApiService.getCities(Pageable.ofSize(1000));

        //then
        StepVerifier.create(cityFlux)
                .thenConsumeWhile(
                        vkCity -> true,
                        vkCity -> assertAll(
                                () -> assertThat(vkCity)
                                        .isNotNull()
//                                        .hasNoNullFieldsOrProperties()
                                ,
                                counter::incrementAndGet
                        )
                )
                .verifyComplete();
        assertThat(counter.get()).isEqualTo(expectedSize);
    }

    @Test
    void getCities_exception_illegalArgument() {

        //when
        var cityFlux = vkApiService.getCities(Pageable.ofSize(10000));

        //then
        StepVerifier.create(cityFlux)
                .verifyErrorSatisfies(throwable -> assertThat(throwable)
                        .isInstanceOf(IllegalArgumentException.class)
                        .hasMessageContaining("Page size must be positive and less then 1000")
                );
    }

    @Test
    void getCities_exception_vkApi() {

        //given
        int fakePageSizeForMockException = 321;

        //when
        var cityFlux = vkApiService.getCities(Pageable.ofSize(fakePageSizeForMockException));

        //then
        StepVerifier.create(cityFlux)
                .verifyErrorSatisfies(throwable -> assertThat(throwable)
                        .isInstanceOf(VkApiException.class)
                        .hasMessageContaining("\"error_code\":5")
                        .hasMessageContaining("\"error_msg\":\"User authorization failed: access_token has expired.\"")
                );
    }

    @Test
    @DisplayName("Requesting all the cities should call VK API multiple time")
    void getAllCities() {

        //given
        int expectedRequestNumber = 157601 / 1000 + 1;
        int expectedSize = 100 * expectedRequestNumber;

        //when
        StepVerifier
                .withVirtualTime(() -> vkApiService.getCities())

                //then
                .expectSubscription()
                .thenAwait(Duration.ofMillis(350))
                .expectNextCount(100)
                .thenAwait(Duration.ofMillis(350))
                .expectNextCount(100)
                .thenAwait(Duration.ofSeconds(60))
                .expectNextCount(expectedSize - 200)
                .expectComplete()
                .verify(Duration.ofSeconds(10));
    }

    @Test
    void getCitiesJson() {

        //when
        var jsonMono = vkApiService.getCitiesJson(Pageable.ofSize(1000));

        //then
        StepVerifier.create(jsonMono)
                .consumeNextWith(citiesResp -> assertThat(citiesResp)
                                .isNotNull()
                                .satisfies(jsonString -> {
//                            log.debug("Response from server:\n{}", jsonString);
                                    JsonNode jsonNode = objectMapper.readValue(jsonString, JsonNode.class);
                                    assertAll(
                                            () -> assertThat(jsonNode.at("/response/count").asInt()).isEqualTo(157601),
                                            () -> assertThat(jsonNode.at("/response/items/0/id").asLong()).isEqualTo(5487461L),
                                            () -> assertThat(jsonNode.at("/response/items/0/title").asText()).isEqualTo("0 км"),
                                            () -> assertThat(jsonNode.at("/response/items/0/area").asText()).isEqualTo("Надымский район район"),
                                            () -> assertThat(jsonNode.at("/response/items/0/region").asText()).isEqualTo("Ямало-Ненецкий автономный округ АО"),
                                            () -> assertThat(jsonNode.at("/response/items/99/id").asLong()).isEqualTo(1081023L),
                                            () -> assertThat(jsonNode.at("/response/items/99/title").asText()).isEqualTo("10-й год Октября"),
                                            () -> assertThat(jsonNode.at("/response/items/99/area").asText()).isEqualTo("Михайловский район район"),
                                            () -> assertThat(jsonNode.at("/response/items/99/region").asText()).isEqualTo("Рязанская область область")
                                    );
                                })
                )
                .verifyComplete();
    }

    @Test
    void getCitiesCount() {

        //when
        var countMono = vkApiService.getCitiesCount();

        //then
        StepVerifier.create(countMono)
                .expectNext(157601)
                .verifyComplete();
    }


}