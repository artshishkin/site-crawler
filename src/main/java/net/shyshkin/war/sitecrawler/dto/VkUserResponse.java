package net.shyshkin.war.sitecrawler.dto;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Data
public class VkUserResponse extends CommonResponse {

    private Set<VkUser> response;

}
