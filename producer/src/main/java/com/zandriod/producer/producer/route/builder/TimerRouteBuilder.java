package com.zandriod.producer.producer.route.builder;

import com.zandriod.producer.producer.processor.ProtobufProcessor;
import lombok.RequiredArgsConstructor;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class TimerRouteBuilder extends RouteBuilder {

    private final ProtobufProcessor protobufProcessor;

    @Override
    public void configure() {

        from("timer://foo?fixedRate=true&period=1000")
                .routeId(TimerRouteBuilder.class.getName() + " Timer")
                .removeHeaders("*")
                .process(protobufProcessor)
                .toD("kafka:MESSAGING-TIMER-EXAMPLE");
    }
}
