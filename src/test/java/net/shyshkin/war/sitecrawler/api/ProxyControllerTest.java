package net.shyshkin.war.sitecrawler.api;

import net.shyshkin.war.sitecrawler.dto.SearchRequest;
import net.shyshkin.war.sitecrawler.dto.VkUser;
import net.shyshkin.war.sitecrawler.service.FetchService;
import net.shyshkin.war.sitecrawler.service.VkApiService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

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

}