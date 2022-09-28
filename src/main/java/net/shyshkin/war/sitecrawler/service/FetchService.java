package net.shyshkin.war.sitecrawler.service;

import reactor.core.publisher.Mono;

public interface FetchService {

    Mono<String> fetchSearchPage(String reservistName);

    Mono<String> fetchUserPage(String userId);
}
