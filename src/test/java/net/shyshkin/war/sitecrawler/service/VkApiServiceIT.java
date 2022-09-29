package net.shyshkin.war.sitecrawler.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.war.sitecrawler.dto.VkUser;
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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;

@Slf4j
@Testcontainers
@SpringBootTest
@ActiveProfiles("mock")
class VkApiServiceIT {

    private static final String[] COMPOSE_FILES_PATHS = {
            "docker-compose/mockserver/common.yml",
            "docker-compose/mockserver/mock-server.yml"
    };

    @Autowired
    VkApiService vkApiService;

    ObjectMapper objectMapper = new ObjectMapper();

    private static final String ENV_FILE_PATH = ".env";

    static DockerComposeContainer<?> composeContainer = new DockerComposeContainer<>(
            Arrays.stream(COMPOSE_FILES_PATHS)
                    .map(path -> Paths.get(path).toAbsolutePath().normalize().toFile())
                    .collect(Collectors.toList())
    )
            .withOptions("--env-file " + ENV_FILE_PATH);


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

    @DynamicPropertySource
    static void mockserverProperties(DynamicPropertyRegistry registry) {
        composeContainer
                .withEnv("MOCKSERVER_HOST_PORT", "0")
                .withExposedService("mockserver", 1080, Wait.forHttp("/health"))
                .start();
        registry.add("app.vk-api.base-url",
                () -> String.format(
                        "http://%s:%s/method",
                        composeContainer.getServiceHost("mockserver", 1080),
                        composeContainer.getServicePort("mockserver", 1080)
                )
        );
    }

}