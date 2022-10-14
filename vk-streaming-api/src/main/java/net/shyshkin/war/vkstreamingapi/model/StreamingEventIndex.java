package net.shyshkin.war.vkstreamingapi.model;

import com.vk.api.sdk.streaming.objects.StreamingEvent;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(indexName = "streaming-event")
public class StreamingEventIndex {

    @Id
    private String id;
    @Field(type = FieldType.Date, format = {}, pattern = "uuuu-MM-dd'T'HH:mm:ssZZ")
    private ZonedDateTime createdAt;
    private StreamingEvent event;

}
