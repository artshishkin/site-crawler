package net.shyshkin.war.sitecrawler.api;

import lombok.RequiredArgsConstructor;
import net.shyshkin.war.sitecrawler.dto.SearchRequest;
import net.shyshkin.war.sitecrawler.service.FetchService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class ProxyController {

    private final FetchService fetchService;

    @GetMapping(value = "/search", produces = MediaType.TEXT_HTML_VALUE)
    public Mono<String> getContent(@RequestParam("name") String reservistName) {
        return fetchService.fetchSearchPage(reservistName);
    }

    @GetMapping(value = "/search", produces = MediaType.TEXT_HTML_VALUE, params = "bday")
    public Mono<String> getContent(SearchRequest searchRequest) {
        return fetchService.fetchSearchPage(searchRequest);
    }

    @GetMapping(value = "/users/{id}", produces = MediaType.TEXT_HTML_VALUE)
    public Mono<String> getUser(@PathVariable("id") String userId) {
        return fetchService.fetchUserPage(userId);
    }

}
