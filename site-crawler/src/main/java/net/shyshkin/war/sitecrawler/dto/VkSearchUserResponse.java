package net.shyshkin.war.sitecrawler.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
public class VkSearchUserResponse extends CommonResponse {

    private SearchUserResponse response;

    @Data
    public static class SearchUserResponse {

        private Integer count;
        private Set<VkUser> items;

    }

}
