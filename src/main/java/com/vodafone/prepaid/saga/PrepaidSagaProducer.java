package com.vodafone.prepaid.saga;

import com.vodafone.prepaid.config.KafkaConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class PrepaidSagaProducer {

    private static final Logger log = LoggerFactory.getLogger(PrepaidSagaProducer.class);

    private final KafkaTemplate<String, SagaEvent> kafkaTemplate;

    public PrepaidSagaProducer(KafkaTemplate<String, SagaEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendMigrationEvent(SagaEvent event) {
        log.info("SAGA [PrepaidProducer] -> Publishing event: type={}, txId={}, msisdn={}",
                event.getEventType(), event.getTransactionId(), event.getMsisdn());
        kafkaTemplate.send(KafkaConfig.PREPAID_EVENTS_TOPIC, event.getMsisdn(), event);
    }
}
