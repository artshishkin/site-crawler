package net.shyshkin.war.vkstreamingapi.api;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.war.vkstreamingapi.model.StreamingEventIndex;
import net.shyshkin.war.vkstreamingapi.service.EventsService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequestMapping("events")
@RequiredArgsConstructor
public class EventsController {

    private final EventsService eventsService;

    @PostMapping(produces = {MediaType.TEXT_EVENT_STREAM_VALUE, MediaType.APPLICATION_JSON_VALUE})
    public Flux<StreamingEventIndex> updateAllEvents() {
        return eventsService.updateAllEvents();
    }

    @PutMapping("{id}")
    public Mono<StreamingEventIndex> updateEvent(@PathVariable String id) {
        return eventsService.updateEvent(id);
    }

    @DeleteMapping("{id}")
    public Mono<Void> deleteEvent(@PathVariable String id) {
        return eventsService.deleteEvent(id);
    }

    @GetMapping("search")
    public Flux<String> searchEvents(@RequestParam("text") String searchText) {
        return eventsService.getEventIdsWithText(searchText);
    }

}
