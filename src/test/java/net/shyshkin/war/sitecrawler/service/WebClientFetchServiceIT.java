package net.shyshkin.war.sitecrawler.service;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.war.sitecrawler.dto.SearchRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
@Testcontainers
@SpringBootTest
@ActiveProfiles("mock")
class WebClientFetchServiceIT {

    private static final String[] COMPOSE_FILES_PATHS = {
            "docker-compose/mockserver/common.yml",
            "docker-compose/mockserver/mock-server.yml"
    };

    @Autowired
    FetchService fetchService;

    private static final String ENV_FILE_PATH = ".env";

    static DockerComposeContainer<?> composeContainer = new DockerComposeContainer<>(
            Arrays.stream(COMPOSE_FILES_PATHS)
                    .map(path -> Paths.get(path).toAbsolutePath().normalize().toFile())
                    .collect(Collectors.toList())
    )
            .withOptions("--env-file " + ENV_FILE_PATH);

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
                .bday(29)
                .bmonth(3)
                .byear(1992)
                .build();

        //when
        Mono<String> fetchSearchPage = fetchService.fetchSearchPage(searchName, request);

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
                .bday(29)
                .bmonth(3)
                .byear(1992)
                .build();

        //when
        Mono<String> fetchSearchPage = fetchService.fetchSearchPage(searchName, request);

        //then
        StepVerifier.create(fetchSearchPage)
                .expectNext("Correct search for СКАЧКОВ СЕРГЕЙ was made")
                .verifyComplete();
    }

    @DynamicPropertySource
    static void mockserverProperties(DynamicPropertyRegistry registry) {
        composeContainer
                .withEnv("MOCKSERVER_HOST_PORT", "0")
//                .withEnv("COMPOSE_PROFILES", "mockserver")
                .withExposedService("mockserver", 1080, Wait.forListeningPort())
                .start();
        registry.add("app.fetch.base-url",
                () -> String.format(
                        "http://%s:%s",
                        composeContainer.getServiceHost("mockserver", 1080),
                        composeContainer.getServicePort("mockserver", 1080)
                )
        );
    }

}