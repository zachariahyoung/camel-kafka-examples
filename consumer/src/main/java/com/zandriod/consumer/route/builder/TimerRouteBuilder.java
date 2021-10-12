package com.zandriod.consumer.route.builder;

import com.zandriod.consumer.processor.KafkaOffsetManagerProcessor;
import com.zandriod.consumer.proto.Timer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.kafka.KafkaConstants;
import org.springframework.stereotype.Component;

@Component
@Slf4j
@RequiredArgsConstructor
public class TimerRouteBuilder extends RouteBuilder {

    private final KafkaOffsetManagerProcessor kafkaOffsetManagerProcessor;

    @Override
    public void configure() throws Exception {

        from("kafka:MESSAGING-TIMER-EXAMPLE")
                .routeId(TimerRouteBuilder.class.getName() + " Timer")
                .process(exchange -> {
                    log.info(this.dumpKafkaDetails(exchange));
                })
                .process(kafkaOffsetManagerProcessor);
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
