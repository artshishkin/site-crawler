package net.shyshkin.war.sitecrawler.common;

import lombok.extern.slf4j.Slf4j;
import net.shyshkin.war.sitecrawler.util.VersionUtil;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;
import org.testcontainers.utility.MountableFile;

@Slf4j
@Testcontainers
@SpringBootTest
@ActiveProfiles("mock")
public abstract class CommonAbstractTest {

    private static final String MOCKSERVER_CONFIG_DIR_PATH = "../docker-compose/mockserver/data-mockserver/mock-init.json";

    static protected GenericContainer<?> mockserver = new GenericContainer<>(DockerImageName.parse("mockserver/mockserver")
            .withTag(VersionUtil.getVersion("MOCKSERVER_VERSION")))
            .withCopyFileToContainer(MountableFile.forHostPath(MOCKSERVER_CONFIG_DIR_PATH), "/config/mock-init.json")
            .withEnv("MOCKSERVER_INITIALIZATION_JSON_PATH", "/config/mock-init.json")
            .withExposedPorts(1080)
            .waitingFor(Wait.forHttp("/health"));

    static {
        mockserver.start();
    }

    @DynamicPropertySource
    static void mockserverProperties(DynamicPropertyRegistry registry) {
        registry.add("MOCKSERVER_URL",
                () -> String.format(
                        "http://%s:%s",
                        mockserver.getHost(),
                        mockserver.getMappedPort(1080)
                )
        );
    }

}