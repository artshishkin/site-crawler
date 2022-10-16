package net.shyshkin.war.vkstreamingapi.service;

import org.junit.jupiter.api.Test;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import static org.assertj.core.api.Assertions.assertThat;

class ContentServiceUrlTests {

    @Test
    void gettingWallId_fromSimpleUrl_shouldReturnCorrectId() {
        //given
        String contentUrl = "https://vk.com/wall-154171849_6169";

        //when
        String wallId = getWallId(contentUrl);

        //then
        assertThat(wallId).isEqualTo("-154171849_6169");

    }

    @Test
    void gettingWallId_fromUrlWithParameters_shouldReturnCorrectId() {
        //given
        String contentUrl = "https://vk.com/wall-190658218_39865?reply=60825&thread=59582";

        //when
        String wallId = getWallId(contentUrl);

        //then
        assertThat(wallId).isEqualTo("-190658218_39865");

    }

    private String getWallId(String url) {
        UriComponents uriComponents = UriComponentsBuilder.fromUriString(url).build();
        String path = uriComponents.getPath();
        return path.replace("/wall", "");
    }
}