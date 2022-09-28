package net.shyshkin.war.sitecrawler.service;

import net.shyshkin.war.sitecrawler.dto.SearchRequest;
import reactor.core.publisher.Mono;

public interface FetchService {

    Mono<String> fetchSearchPage(String reservistName);

    Mono<String> fetchSearchPage(String reservistName, SearchRequest searchRequest);

    Mono<String> fetchUserPage(String userId);
}
