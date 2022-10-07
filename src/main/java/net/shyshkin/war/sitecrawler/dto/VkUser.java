package net.shyshkin.war.sitecrawler.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.JsonNode;
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

    private String deactivated;
    @JsonProperty("is_closed")
    private Boolean isClosed;
    private String about;
    private String activities;
    private String books;
    private JsonNode career;
    private JsonNode connections;
    private JsonNode contacts;
    private JsonNode counters; //Поле возвращается только в методе users.get при запросе информации об одном пользователе, с передачей пользовательского access_token.
    private String domain;
    private JsonNode education;
    private JsonNode exports;
    private Integer followers_count;
    private Integer has_mobile;
    private Integer has_photo;
    private String home_town;
    private JsonNode military;
    private String movies;
    private String music;
    private String nickname;
    private JsonNode occupation;
    private JsonNode personal;
    private String quotes;
    private JsonNode relatives;
    private Integer relation;
    private JsonNode schools;
    private Integer sex;
    private String site;
    private String status;
    private JsonNode universities;

}
