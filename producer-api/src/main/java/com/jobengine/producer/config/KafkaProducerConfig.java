package com.jobengine.producer.config;

import com.jobengine.common.dto.JobEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaProducerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Bean
    public ProducerFactory<String, JobEvent> producerFactory() {
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        // acks=all: wait for all replicas to confirm — guarantees no message loss
        config.put(ProducerConfig.ACKS_CONFIG, "all");
        config.put(ProducerConfig.RETRIES_CONFIG, 3);
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean
    public KafkaTemplate<String, JobEvent> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    // Creates the topic on startup if it does not already exist
    @Bean
    public NewTopic jobEventsTopic() {
        return TopicBuilder.name("job-events")
                .partitions(3)  // allows up to 3 workers to consume in parallel
                .replicas(1)    // single broker in local dev — increase for production
                .build();
    }

    @Bean
    public NewTopic jobEventsDltTopic() {
        return TopicBuilder.name("job-events.DLT")
                .partitions(3)
                .replicas(1)
                .build();
    }
}
