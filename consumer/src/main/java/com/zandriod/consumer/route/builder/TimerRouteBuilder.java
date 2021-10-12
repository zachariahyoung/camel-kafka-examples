package com.zandriod.consumer.route.builder;

import com.zandriod.consumer.processor.HealthCheckProcessor;
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
    private final HealthCheckProcessor healthCheckProcessor;

    @Override
    public void configure() throws Exception {

        from("kafka:MESSAGING-TIMER-EXAMPLE")
                .routeId(TimerRouteBuilder.class.getName() + " Timer")
                .process(healthCheckProcessor)
                .process(kafkaOffsetManagerProcessor);
    }

}
