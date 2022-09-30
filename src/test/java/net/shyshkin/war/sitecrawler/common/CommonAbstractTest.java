package net.shyshkin.war.sitecrawler.common;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.testcontainers.containers.DockerComposeContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.nio.file.Paths;
import java.util.Arrays;
import java.util.stream.Collectors;

@Slf4j
@Testcontainers
@SpringBootTest
@ActiveProfiles("mock")
public abstract class CommonAbstractTest {

    private static final String[] COMPOSE_FILES_PATHS = {
            "docker-compose/mockserver/common.yml",
            "docker-compose/mockserver/mock-server.yml"
    };

    private static final String ENV_FILE_PATH = ".env";

    static protected DockerComposeContainer<?> composeContainer = new DockerComposeContainer<>(
            Arrays.stream(COMPOSE_FILES_PATHS)
                    .map(path -> Paths.get(path).toAbsolutePath().normalize().toFile())
                    .collect(Collectors.toList())
    )
            .withOptions("--env-file " + ENV_FILE_PATH)
            .withBuild(true)
            .withEnv("MOCKSERVER_HOST_PORT", "0")
            .withExposedService("mockserver", 1080, Wait.forHttp("/health"));

    static {
        composeContainer.start();
    }

}