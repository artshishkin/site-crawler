package net.shyshkin.war.vkrestapi.api;

import net.shyshkin.war.vkrestapi.dto.SearchRequest;
import net.shyshkin.war.vkrestapi.service.VkApiService;
import org.assertj.core.data.Index;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@WebFluxTest(ProxyController.class)
class ProxyControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @MockBean
    VkApiService vkApiService;

    @Captor
    ArgumentCaptor<List<SearchRequest>> searchRequestsCaptor;

    @Nested
    class BatchSearchTests {

        @Test
        void batchSearchUser_withAcceptJson_andParameterDebug_shouldCallVkApiService_fullJson() {

            //given
            String requestBodyJson = "[\n" +
                    "  {\n" +
                    "    \"name\": \"СКАЧКОВ СЕРГЕЙ\",\n" +
                    "    \"bday\": 29,\n" +
                    "    \"bmonth\": 3,\n" +
                    "    \"byear\": 1992,\n" +
                    "    \"city\": 1\n" +
                    "  },\n" +
                    "  {\n" +
                    "    \"name\": \"МАТЕВОСЯН\",\n" +
                    "    \"bday\": 1,\n" +
                    "    \"bmonth\": 2,\n" +
                    "    \"byear\": 1996,\n" +
                    "    \"city\": 1121988\n" +
                    "  }\n" +
                    "]";
            String mockResponse = "{\"response\": \"mock-response\"}";
            given(vkApiService.searchUsersBatchJson(any()))
                    .willReturn(Mono.just(mockResponse));

            //when
            webTestClient
                    .post().uri("/search?debug")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBodyJson)
                    .exchange()

                    //then
                    .expectStatus().isOk()
                    .expectBody(String.class)
                    .isEqualTo(mockResponse);

            then(vkApiService).should().searchUsersBatchJson(searchRequestsCaptor.capture());
            assertThat(searchRequestsCaptor.getValue())
                    .hasSize(2)
                    .satisfies(searchRequest ->
                            assertAll(
                                    () -> assertThat(searchRequest.getName()).isEqualTo("СКАЧКОВ СЕРГЕЙ"),
                                    () -> assertThat(searchRequest.getBday()).isEqualTo(29),
                                    () -> assertThat(searchRequest.getBmonth()).isEqualTo(3),
                                    () -> assertThat(searchRequest.getByear()).isEqualTo(1992),
                                    () -> assertThat(searchRequest.getCity()).isEqualTo(1)
                            ), Index.atIndex(0)
                    )
                    .satisfies(searchRequest ->
                            assertAll(
                                    () -> assertThat(searchRequest.getName()).isEqualTo("МАТЕВОСЯН"),
                                    () -> assertThat(searchRequest.getBday()).isEqualTo(1),
                                    () -> assertThat(searchRequest.getBmonth()).isEqualTo(2),
                                    () -> assertThat(searchRequest.getByear()).isEqualTo(1996),
                                    () -> assertThat(searchRequest.getCity()).isEqualTo(1121988)
                            ), Index.atIndex(1)
                    );
        }

        @Test
        void batchSearchUser_shouldCallVkApiService_fullJson() {

            //given
            String requestBodyJson = "[\n" +
                    "  {\n" +
                    "    \"name\": \"СКАЧКОВ СЕРГЕЙ\",\n" +
                    "    \"bday\": 29,\n" +
                    "    \"bmonth\": 3,\n" +
                    "    \"byear\": 1992,\n" +
                    "    \"city\": 1\n" +
                    "  },\n" +
                    "  {\n" +
                    "    \"name\": \"МАТЕВОСЯН\",\n" +
                    "    \"bday\": 1,\n" +
                    "    \"bmonth\": 2,\n" +
                    "    \"byear\": 1996,\n" +
                    "    \"city\": 1121988\n" +
                    "  },\n" +
                    "  {\n" +
                    "    \"name\": \"ОТСУТСТВУЮЩИЙ\",\n" +
                    "    \"bday\": 1,\n" +
                    "    \"bmonth\": 1,\n" +
                    "    \"byear\": 1911,\n" +
                    "    \"city\": 1121988\n" +
                    "  }\n" +
                    "]";
            String mockResponse = "{\"response\": \"mock-response\"}";
            given(vkApiService.searchUsersBatchJson(any()))
                    .willReturn(Mono.just(realResponse()));

            //when
            webTestClient
                    .post().uri("/search?debug")
                    .accept(MediaType.APPLICATION_JSON)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBodyJson)
                    .exchange()

                    //then
                    .expectStatus().isOk()
                    .expectBody(String.class)
                    .isEqualTo(realResponse());

            then(vkApiService).should().searchUsersBatchJson(searchRequestsCaptor.capture());
            assertThat(searchRequestsCaptor.getValue())
                    .hasSize(3)
                    .satisfies(searchRequest ->
                            assertAll(
                                    () -> assertThat(searchRequest.getName()).isEqualTo("СКАЧКОВ СЕРГЕЙ"),
                                    () -> assertThat(searchRequest.getBday()).isEqualTo(29),
                                    () -> assertThat(searchRequest.getBmonth()).isEqualTo(3),
                                    () -> assertThat(searchRequest.getByear()).isEqualTo(1992),
                                    () -> assertThat(searchRequest.getCity()).isEqualTo(1)
                            ), Index.atIndex(0)
                    )
                    .satisfies(searchRequest ->
                            assertAll(
                                    () -> assertThat(searchRequest.getName()).isEqualTo("МАТЕВОСЯН"),
                                    () -> assertThat(searchRequest.getBday()).isEqualTo(1),
                                    () -> assertThat(searchRequest.getBmonth()).isEqualTo(2),
                                    () -> assertThat(searchRequest.getByear()).isEqualTo(1996),
                                    () -> assertThat(searchRequest.getCity()).isEqualTo(1121988)
                            ), Index.atIndex(1)
                    );
        }

    }

    private String realResponse(){
        return "{\n" +
                "  \"response\": [\n" +
                "    {\n" +
                "      \"count\": 1,\n" +
                "      \"items\": [\n" +
                "        {\n" +
                "          \"id\": 207667411,\n" +
                "          \"nickname\": \"\",\n" +
                "          \"domain\": \"id207667411\",\n" +
                "          \"bdate\": \"29.3.1992\",\n" +
                "          \"city\": {\n" +
                "            \"id\": 1,\n" +
                "            \"title\": \"Москва\"\n" +
                "          },\n" +
                "          \"country\": {\n" +
                "            \"id\": 1,\n" +
                "            \"title\": \"Россия\"\n" +
                "          },\n" +
                "          \"has_photo\": 1,\n" +
                "          \"has_mobile\": 0,\n" +
                "          \"books\": \"\",\n" +
                "          \"quotes\": \"\",\n" +
                "          \"about\": \"\",\n" +
                "          \"movies\": \"\",\n" +
                "          \"activities\": \"\",\n" +
                "          \"music\": \"\",\n" +
                "          \"mobile_phone\": \"\",\n" +
                "          \"home_phone\": \"\",\n" +
                "          \"site\": \"\",\n" +
                "          \"status\": \"как же тоскливо без тебя\",\n" +
                "          \"last_seen\": {\n" +
                "            \"platform\": 7,\n" +
                "            \"time\": 1370691677\n" +
                "          },\n" +
                "          \"followers_count\": 1,\n" +
                "          \"career\": [],\n" +
                "          \"military\": [],\n" +
                "          \"university\": 0,\n" +
                "          \"university_name\": \"\",\n" +
                "          \"faculty\": 0,\n" +
                "          \"faculty_name\": \"\",\n" +
                "          \"graduation\": 0,\n" +
                "          \"home_town\": \"донской\",\n" +
                "          \"relation\": 4,\n" +
                "          \"universities\": [],\n" +
                "          \"schools\": [],\n" +
                "          \"relatives\": [],\n" +
                "          \"track_code\": \"0b64056a1gdDQXOsuEcD6MXmxZhW2ioZ8ghO0vuOKq5ZsTMpLnaxblATL5a8RAfn8kgOGN3fQhf0C07S9ehYxw\",\n" +
                "          \"sex\": 2,\n" +
                "          \"first_name\": \"Сергей\",\n" +
                "          \"last_name\": \"Скачков\",\n" +
                "          \"can_access_closed\": true,\n" +
                "          \"is_closed\": false\n" +
                "        }\n" +
                "      ]\n" +
                "    },\n" +
                "    {\n" +
                "      \"count\": 1,\n" +
                "      \"items\": [\n" +
                "        {\n" +
                "          \"id\": 180581556,\n" +
                "          \"nickname\": \"\",\n" +
                "          \"domain\": \"hachik96\",\n" +
                "          \"bdate\": \"1.2.1996\",\n" +
                "          \"city\": {\n" +
                "            \"id\": 99,\n" +
                "            \"title\": \"Новосибирск\"\n" +
                "          },\n" +
                "          \"country\": {\n" +
                "            \"id\": 1,\n" +
                "            \"title\": \"Россия\"\n" +
                "          },\n" +
                "          \"has_photo\": 1,\n" +
                "          \"has_mobile\": 1,\n" +
                "          \"home_phone\": \"\",\n" +
                "          \"site\": \"\",\n" +
                "          \"status\": \"Instagram... matevosyan_54\",\n" +
                "          \"last_seen\": {\n" +
                "            \"platform\": 4,\n" +
                "            \"time\": 1663495833\n" +
                "          },\n" +
                "          \"followers_count\": 313,\n" +
                "          \"occupation\": {\n" +
                "            \"id\": 3924,\n" +
                "            \"name\": \"НВВКУ (ВИ МО РФ) (бывш. НВОКУ, НВВПОУ)\",\n" +
                "            \"type\": \"university\",\n" +
                "            \"graduate_year\": 2018,\n" +
                "            \"country_id\": 1,\n" +
                "            \"city_id\": 99\n" +
                "          },\n" +
                "          \"track_code\": \"e72de842ZRSZhVdaTbEBi2Nkvr0OhSHOBbpZFyioZRT5HYXLBDUCfdrTCmdNsVLcWsx0OYKAScADuVkXJs4XfQ\",\n" +
                "          \"sex\": 2,\n" +
                "          \"first_name\": \"Хачатур\",\n" +
                "          \"last_name\": \"Матевосян\",\n" +
                "          \"can_access_closed\": true,\n" +
                "          \"is_closed\": false\n" +
                "        }\n" +
                "      ]\n" +
                "    },\n" +
                "    {\n" +
                "      \"count\": 0,\n" +
                "      \"items\": []\n" +
                "    }\n" +
                "  ]\n" +
                "}";
    }

}