package net.shyshkin.war.sitecrawler.dto;

import lombok.Data;

import java.util.Set;

@Data
public class VkSearchUserResponse {

    private SearchUserResponse response;

    @Data
    public static class SearchUserResponse {

        private Integer count;
        private Set<VkUser> items;

    }

}
