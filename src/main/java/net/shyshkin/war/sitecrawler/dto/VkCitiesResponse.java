package net.shyshkin.war.sitecrawler.dto;

import lombok.Data;

import java.util.Set;

@Data
public class VkCitiesResponse {

    private CitiesResponse response;

    @Data
    public static class CitiesResponse {

        private Integer count;
        private Set<VkCity> items;

    }

}
