package net.shyshkin.war.sitecrawler.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SearchRequest {
    private Integer bday;
    private Integer bmonth;
    private Integer byear;

//    c[bday]=29&c[bmonth]=3&c[city]=1&c[byear]=1992&c[country]=1&c[name]=1&c[per_page]=40&c[q]={reservist_name}&c[section]=people'

}
