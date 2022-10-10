package net.shyshkin.war.sitecrawler.dto;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.Data;

@Data
public abstract class CommonResponse {

    private JsonNode error;

}
