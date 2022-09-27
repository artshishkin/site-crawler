package net.shyshkin.war.sitecrawler.api;

import lombok.RequiredArgsConstructor;
import net.shyshkin.war.sitecrawler.service.FetchService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
public class ProxyController {

    private final FetchService fetchService;

    @GetMapping
    public Mono<String> getContent(@RequestParam("name") String reservistName) {
        return fetchService.fetchPage(reservistName);
    }

}
