package net.shyshkin.war.sitecrawler.dto;

import lombok.Data;

import java.util.Set;

@Data
public class VkUserResponse {

    private Set<VkUser> response;

}
