package net.shyshkin.war.vkrestapi;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

@SpringBootTest
@TestPropertySource(properties = {
        "VK_USER_ID=123",
        "VK_ACCESS_TOKEN=vk1.a.some_token"
})
class VkRestApiApplicationTests {

    @Test
    void contextLoads() {
    }

}