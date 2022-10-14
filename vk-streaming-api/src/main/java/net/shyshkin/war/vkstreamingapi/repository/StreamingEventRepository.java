package net.shyshkin.war.vkstreamingapi.repository;

import net.shyshkin.war.vkstreamingapi.model.StreamingEventIndex;
import org.springframework.data.elasticsearch.repository.ReactiveElasticsearchRepository;

public interface StreamingEventRepository extends ReactiveElasticsearchRepository<StreamingEventIndex,String> {
}
