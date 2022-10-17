package net.shyshkin.war.vkstreamingapi.repository;

import net.shyshkin.war.vkstreamingapi.model.StreamingEventIndex;
import org.springframework.data.elasticsearch.annotations.Query;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;
import reactor.core.publisher.Flux;

public interface StreamingEventRepository extends ReactiveElasticsearchRepository<StreamingEventIndex, String> {

    @Query("{\"bool\":{\"should\":[{\"match\":{\"event.text\":\"?0\"}},{\"match\":{\"content.items.copyHistory.text\":\"?0\"}},{\"match\":{\"content.items.text\":\"?0\"}}]}}")
    Flux<StreamingEventIndex> findEventsWithText(String searchText);

}
