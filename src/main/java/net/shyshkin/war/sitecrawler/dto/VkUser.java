package net.shyshkin.war.sitecrawler.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class VkUser {

    private Long id;
    private String firstName;
    private String lastName;
    @JsonProperty("bdate")
    private String birthDate;
    private VkCity city;
    private VkCountry country;
    private VkLastSeen lastSeen;

}
