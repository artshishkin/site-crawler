package net.shyshkin.war.vkstreamingapi.api;

import com.vk.api.sdk.streaming.objects.StreamingRule;
import com.vk.api.sdk.streaming.objects.responses.StreamingResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.shyshkin.war.vkstreamingapi.model.VkStreamingRule;
import net.shyshkin.war.vkstreamingapi.service.RulesService;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("rules")
public class RulesController {

    private final RulesService rulesService;

    @GetMapping(produces = {MediaType.APPLICATION_JSON_VALUE, MediaType.TEXT_EVENT_STREAM_VALUE})
    public Flux<StreamingRule> getRules() {
        log.debug("Getting all the rules");
        return rulesService.getRules();
    }

    @PostMapping
    public Mono<StreamingResponse> addRule(@RequestBody VkStreamingRule rule) {
        log.debug("Adding new rule: {}", rule);
        return rulesService.addRule(rule);
    }

    @DeleteMapping("{tag}")
    public Mono<StreamingResponse> deleteRule(@PathVariable String tag) {
        log.debug("Deleting rule with tag: {}", tag);
        return rulesService.deleteRule(tag);
    }

}
