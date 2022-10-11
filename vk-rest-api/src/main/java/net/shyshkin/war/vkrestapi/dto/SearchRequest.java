package net.shyshkin.war.vkrestapi.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchRequest {

    private String name;
    private Integer bday;
    private Integer bmonth;
    private Integer byear;
    private Integer city;

}
