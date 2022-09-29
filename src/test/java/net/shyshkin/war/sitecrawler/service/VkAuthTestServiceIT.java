package net.shyshkin.war.sitecrawler.service;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.war.sitecrawler.dto.ClientCredentials;
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
import reactor.test.StepVerifier;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
@Testcontainers
@SpringBootTest
@ActiveProfiles("mock")
class VkAuthTestServiceIT {

    private static final String[] COMPOSE_FILES_PATHS = {
            "docker-compose/mockserver/common.yml",
            "docker-compose/mockserver/mock-server.yml"
    };

    @Autowired
    VkAuthTestService vkAuthTestService;

    private static final String ENV_FILE_PATH = ".env";

    static DockerComposeContainer<?> composeContainer = new DockerComposeContainer<>(
            Arrays.stream(COMPOSE_FILES_PATHS)
                    .map(path -> Paths.get(path).toAbsolutePath().normalize().toFile())
                    .collect(Collectors.toList())
    )
            .withOptions("--env-file " + ENV_FILE_PATH);

    @Test
    @DisplayName("Searching for user only by name should return `Was called` from mock-server")
    void gettingAccessToken_shouldReturnAccessToken() {
        //given
        ClientCredentials clientCredentials = ClientCredentials.builder()
                .clientId("123")
                .clientSecret("12345678")
                .build();

        //when
        var accessTokenMono = vkAuthTestService.getAccessToken(clientCredentials);

        //then
        StepVerifier.create(accessTokenMono)
                .expectNextMatches(token -> "some_long_token" .equals(token.getAccessToken()))
                .verifyComplete();
    }

    @DynamicPropertySource
    static void mockserverProperties(DynamicPropertyRegistry registry) {
        composeContainer
                .withEnv("MOCKSERVER_HOST_PORT", "0")
//                .withEnv("COMPOSE_PROFILES", "mockserver")
                .withExposedService("mockserver", 1080, Wait.forHttp("/health"))
                .start();
        registry.add("app.vk-api.auth-url",
                () -> String.format(
                        "http://%s:%s/access_token",
                        composeContainer.getServiceHost("mockserver", 1080),
                        composeContainer.getServicePort("mockserver", 1080)
                )
        );
    }

}