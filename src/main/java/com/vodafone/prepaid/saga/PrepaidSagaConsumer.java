package com.vodafone.prepaid.saga;

import com.vodafone.prepaid.config.KafkaConfig;
import com.vodafone.prepaid.entity.PrepaidAccount;
import com.vodafone.prepaid.repository.PrepaidRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Component
public class PrepaidSagaConsumer {

    private static final Logger log = LoggerFactory.getLogger(PrepaidSagaConsumer.class);

    private final PrepaidRepository prepaidRepository;

    public PrepaidSagaConsumer(PrepaidRepository prepaidRepository) {
        this.prepaidRepository = prepaidRepository;
    }

    @KafkaListener(topics = KafkaConfig.POSTPAID_EVENTS_TOPIC, groupId = "prepaid-group")
    @Transactional
    public void consumePostpaidEvent(SagaEvent event) {
        log.info("SAGA [PrepaidConsumer] <- Received event from postpaid-service: type={}, txId={}, status={}",
                event.getEventType(), event.getTransactionId(), event.getStatus());

        Optional<PrepaidAccount> accountOpt = prepaidRepository.findByMsisdn(event.getMsisdn());
        if (!accountOpt.isPresent()) {
            log.error("SAGA [PrepaidConsumer] Account not found for MSISDN: {}", event.getMsisdn());
            return;
        }

        PrepaidAccount account = accountOpt.get();

        if ("POSTPAID_CREATED".equals(event.getEventType()) && "COMPLETED".equals(event.getStatus())) {
            // SAGA Success Step
            account.setStatus("MIGRATED");
            prepaidRepository.save(account);
            log.info("SAGA Transaction COMPLETED successfully for MSISDN: {}. Status set to MIGRATED", event.getMsisdn());
        } else if ("MIGRATION_FAILED".equals(event.getEventType()) || "FAILED".equals(event.getStatus())) {
            // SAGA Compensating Transaction Step
            account.setStatus("ACTIVE");
            prepaidRepository.save(account);
            log.warn("SAGA Compensating Transaction EXECUTED for MSISDN: {}. Rollback performed, Status set back to ACTIVE. Reason: {}",
                    event.getMsisdn(), event.getReason());
        }
    }
}
