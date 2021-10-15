package com.zandriod.consumer.processor;

import com.zandriod.consumer.client.HealthCheckClient;
import com.zandriod.consumer.dto.ResilienceDto;
import com.zandriod.consumer.proto.Timer;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.apache.camel.component.kafka.KafkaConstants;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class HealthCheckProcessor implements Processor {


    public final HealthCheckClient healthCheckClient;

    public HealthCheckProcessor(HealthCheckClient healthCheckClient) {
        this.healthCheckClient = healthCheckClient;
    }

    @Override
    public void process(Exchange exchange) throws Exception {

        log.info(this.dumpKafkaDetails(exchange));

        String eventType = exchange.getIn().getHeader("ce_type", String.class);

        log.info("Event Type " + eventType);

        ResilienceDto health = switch (eventType){
            case "com.zandriod.messaging.exception" -> healthCheckClient.fetchResilience().block();
            case "com.zandriod.messaging.gateway" -> healthCheckClient.fetchGateway().block();
            case "com.zandriod.messaging.delay" -> healthCheckClient.fetchDelay().block();
            default -> healthCheckClient.fetchStatus().block();
        };

        log.info("Status {} Event {} ", health.status(),eventType);

    }

    private String dumpKafkaDetails(Exchange exchange) {

        Timer body = exchange.getIn().getBody(Timer.class);

        StringBuilder sb = new StringBuilder();
        sb.append("\r\n");
        sb.append("Message Received from topic:").append(exchange.getIn().getHeader(KafkaConstants.TOPIC));
        sb.append("\r\n");
        sb.append("Message Received from partition:").append(exchange.getIn().getHeader(KafkaConstants.PARTITION));
        sb.append(" with partition key:").append(exchange.getIn().getHeader(KafkaConstants.PARTITION_KEY));
        sb.append("\r\n");
        sb.append("Message offset:").append(exchange.getIn().getHeader(KafkaConstants.OFFSET));
        sb.append("\r\n");
        sb.append("Message last record:").append(exchange.getIn().getHeader(KafkaConstants.LAST_RECORD_BEFORE_COMMIT));
        sb.append("\r\n");
        sb.append("Message Received:");
        sb.append("\r\n").append(body.toString());

        return sb.toString();
    }

}
