package net.shyshkin.war.sitecrawler.service;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.war.sitecrawler.common.CommonAbstractTest;
import net.shyshkin.war.sitecrawler.dto.SearchRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestPropertySource;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@Slf4j
@TestPropertySource(properties = {
        "app.fetch.base-url=${MOCKSERVER_URL}"
})
class WebClientFetchServiceIT extends CommonAbstractTest {

    @Autowired
    FetchService fetchService;

    @Test
    @DisplayName("Searching for user only by name should return `Was called` from mock-server")
    void fetchSearchPage_nameOnly() {
        //given
        String searchName = "Art";

        //when
        Mono<String> fetchSearchPage = fetchService.fetchSearchPage(searchName);

        //then
        StepVerifier.create(fetchSearchPage)
                .expectNext("Was called")
                .verifyComplete();
    }

    @Test
    @DisplayName("Searching for user by name and birth date, when user is absent should return `Correct search for ANY was made` from mock-server")
    void fetchSearchPage_withBirthdate_butAbsentName() {
        //given
        String searchName = "Art";
        SearchRequest request = SearchRequest.builder()
                .name(searchName)
                .bday(29)
                .bmonth(3)
                .byear(1992)
                .city(1L)
                .build();

        //when
        Mono<String> fetchSearchPage = fetchService.fetchSearchPage(request);

        //then
        StepVerifier.create(fetchSearchPage)
                .expectNext("Correct search for ANY was made")
                .verifyComplete();
    }

    @Test
    @DisplayName("Searching for user by name and birth date, when user is present should return `Correct search for СКАЧКОВ СЕРГЕЙ was made` from mock-server")
    void fetchSearchPage_withBirthdate_presentName() {
        //given
        String searchName = "СКАЧКОВ СЕРГЕЙ";
        SearchRequest request = SearchRequest.builder()
                .name(searchName)
                .bday(29)
                .bmonth(3)
                .byear(1992)
                .city(1L)
                .build();

        //when
        Mono<String> fetchSearchPage = fetchService.fetchSearchPage(request);

        //then
        StepVerifier.create(fetchSearchPage)
                .expectNext("Correct search for СКАЧКОВ СЕРГЕЙ was made")
                .verifyComplete();
    }

}