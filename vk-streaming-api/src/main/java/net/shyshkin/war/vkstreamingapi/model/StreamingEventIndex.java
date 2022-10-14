package net.shyshkin.war.vkstreamingapi.model;

import com.vk.api.sdk.streaming.objects.StreamingEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(indexName = "streaming-event")
public class StreamingEventIndex {

    @Id
    private String id;
    private StreamingEvent event;

}
