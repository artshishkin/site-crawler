package net.shyshkin.war.sitecrawler.api;

import net.shyshkin.war.sitecrawler.dto.SearchRequest;
import net.shyshkin.war.sitecrawler.dto.VkCity;
import net.shyshkin.war.sitecrawler.dto.VkUser;
import net.shyshkin.war.sitecrawler.service.FetchService;
import net.shyshkin.war.sitecrawler.service.VkApiService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;

@WebFluxTest(controllers = ProxyController.class)
class ProxyControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @MockBean
    FetchService fetchService;

    @MockBean
    VkApiService vkApiService;

    @Test
    void search_shouldCallFetchService() {

        //given
        given(fetchService.fetchSearchPage(anyString()))
                .willReturn(Mono.just("Request was called simple"));

        //when
        webTestClient
                .get().uri("/search?name=ART")
                .accept(MediaType.TEXT_HTML)
                .exchange()

                //then
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo("Request was called simple");

        then(fetchService).should().fetchSearchPage(eq("ART"));
    }

    @Test
    void searchWithBirthdayParameters_shouldCallFetchServiceWithBirthdayParameters() {

        //given
        given(fetchService.fetchSearchPage(any(SearchRequest.class)))
                .willReturn(Mono.just("Request was called"));
        SearchRequest expectedSearchRequest = SearchRequest.builder()
                .name("ART")
                .bday(7)
                .bmonth(2)
                .byear(1983)
                .build();

        //when
        webTestClient
                .get().uri("/search?name=ART&bday=7&bmonth=2&byear=1983")
                .accept(MediaType.TEXT_HTML)
                .exchange()

                //then
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo("Request was called");

        then(fetchService).should().fetchSearchPage(eq(expectedSearchRequest));
    }

    @Test
    void getUser_withAcceptTxtHtml_shouldCallFetchService() {

        //given
        given(fetchService.fetchUserPage(anyString()))
                .willReturn(Mono.just("Text HTML"));

        //when
        webTestClient
                .get().uri("/users/12345678")
                .accept(MediaType.TEXT_HTML)
                .exchange()

                //then
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo("Text HTML");

        then(fetchService).should().fetchUserPage(eq("12345678"));
    }

    @Test
    void getUser_withAcceptJson_shouldCallVkApiService() {

        //given
        VkUser expectedUser = new VkUser();
        expectedUser.setId(12345678L);
        expectedUser.setFirstName("Art");
        expectedUser.setLastName("Shyshkin");
        given(vkApiService.getUser(anyLong()))
                .willReturn(Mono.just(expectedUser));

        //when
        webTestClient
                .get().uri("/users/12345678")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()

                //then
                .expectStatus().isOk()
                .expectBody(VkUser.class)
                .isEqualTo(expectedUser);

        then(vkApiService).should().getUser(eq(12345678L));
    }

    @Test
    void getUser_withAcceptJson_andParameterDebug_shouldCallVkApiService_fullJson() {

        //given
        String expectedJson = "{\"content\": \"full JSON\"}";
        given(vkApiService.getUserJson(anyLong()))
                .willReturn(Mono.just(expectedJson));

        //when
        webTestClient
                .get().uri("/users/12345678?debug")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()

                //then
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo(expectedJson);

        then(vkApiService).should().getUserJson(eq(12345678L));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_EVENT_STREAM_VALUE
    })
    void searchUser_withAcceptJsonOrEvent_shouldCallVkApiService(String mediaTypeString) {

        //given
        MediaType mediaType = MediaType.valueOf(mediaTypeString);
        VkUser mockUser = new VkUser();
        mockUser.setId(12345678L);
        mockUser.setFirstName("Art");
        mockUser.setLastName("Shyshkin");
        SearchRequest expectedRequest = SearchRequest.builder()
                .name("Art")
                .bday(7)
                .bmonth(2)
                .byear(1983)
                .build();
        given(vkApiService.searchUsers(any(SearchRequest.class)))
                .willReturn(Flux.just(mockUser));

        //when
        Flux<VkUser> userFlux = webTestClient
                .get().uri("/search?name=Art&bday=7&bmonth=2&byear=1983")
                .accept(mediaType)
                .exchange()

                //then
                .expectStatus().isOk()
                .returnResult(VkUser.class)
                .getResponseBody();

        StepVerifier.create(userFlux)
                .expectNext(mockUser)
                .verifyComplete();

        then(vkApiService).should().searchUsers(eq(expectedRequest));
    }

    @Test
    void searchUser_withAcceptJson_andParameterDebug_shouldCallVkApiService_fullJson() {

        //given
        SearchRequest expectedRequest = SearchRequest.builder()
                .name("Art")
                .bday(7)
                .bmonth(2)
                .byear(1983)
                .build();
        String mockResponse = "{\"response\": \"mock-response\"}";
        given(vkApiService.searchUsersJson(any(SearchRequest.class)))
                .willReturn(Mono.just(mockResponse));

        //when
        webTestClient
                .get().uri("/search?name=Art&bday=7&bmonth=2&byear=1983&debug")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()

                //then
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo(mockResponse);

        then(vkApiService).should().searchUsersJson(eq(expectedRequest));
    }

    @ParameterizedTest
    @ValueSource(strings = {
            MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_EVENT_STREAM_VALUE
    })
    void getCities_withAcceptJsonOrEvent_shouldCallVkApiService(String mediaTypeString) {

        //given
        MediaType mediaType = MediaType.valueOf(mediaTypeString);
        var mockCity = new VkCity();
        mockCity.setId(12345678);
        mockCity.setTitle("Moscow");
        mockCity.setRegion("Mordor");
        mockCity.setArea("BullShit");
        given(vkApiService.getCities())
                .willReturn(Flux.just(mockCity));

        //when
        Flux<VkCity> cityFlux = webTestClient
                .get().uri("/cities")
                .accept(mediaType)
                .exchange()

                //then
                .expectStatus().isOk()
                .returnResult(VkCity.class)
                .getResponseBody();

        StepVerifier.create(cityFlux)
                .expectNext(mockCity)
                .verifyComplete();

        then(vkApiService).should().getCities();
    }

    @Test
    void getCities_withAcceptJsonOrEvent_shouldCallVkApiService_fullJson() {

        //given
        String mockResponse = "{\"resp\":\"some resp\"}";
        given(vkApiService.getCitiesJson())
                .willReturn(Mono.just(mockResponse));

        //when
        webTestClient
                .get().uri("/cities?debug")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()

                //then
                .expectStatus().isOk()
                .expectBody(String.class)
                .isEqualTo(mockResponse);

        then(vkApiService).should().getCitiesJson();
    }

}