package net.shyshkin.war.vkstreamingapi;

import com.vk.api.sdk.streaming.clients.actors.StreamingActor;
import net.shyshkin.war.vkstreamingapi.service.StreamingService;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
        "VK_APP_ID=123",
        "VK_APP_ACCESS_TOKEN=super_secret_access_token_taken_from_vk_console_when_registering_new_application"
})
class VkStreamingApiApplicationTest {

    @MockBean
    StreamingActor streamingActor;

    @MockBean
    StreamingService streamingService;

    @Test
    void contextLoads() {
    }

}