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
    }

}