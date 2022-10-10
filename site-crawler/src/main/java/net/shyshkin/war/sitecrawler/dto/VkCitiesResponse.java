package net.shyshkin.war.sitecrawler.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
public class VkCitiesResponse extends CommonResponse{

    private CitiesResponse response;

    @Data
    public static class CitiesResponse {

        private Integer count;
        private Set<VkCity> items;

    }

}
