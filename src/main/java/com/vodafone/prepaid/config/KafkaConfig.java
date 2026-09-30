package com.vodafone.prepaid.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;

@Configuration
public class KafkaConfig {

    public static final String PREPAID_EVENTS_TOPIC = "prepaid-events";
    public static final String POSTPAID_EVENTS_TOPIC = "postpaid-events";

    @Bean
    public NewTopic prepaidEventsTopic() {
        return TopicBuilder.name(PREPAID_EVENTS_TOPIC)
                .partitions(1)
                .replicas(1)
                .build();
    }
}
