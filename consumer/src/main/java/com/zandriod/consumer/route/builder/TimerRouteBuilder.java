package com.zandriod.consumer.route.builder;

import com.zandriod.consumer.exception.NonRecoverableException;
import com.zandriod.consumer.processor.HealthCheckProcessor;
import com.zandriod.consumer.processor.KafkaOffsetManagerProcessor;
import com.zandriod.consumer.proto.Timer;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.core.registry.RegistryEventConsumer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.builder.RouteBuilder;
import org.apache.camel.component.kafka.KafkaConstants;
import org.apache.camel.component.kafka.consumer.errorhandler.KafkaConsumerListener;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientRequestException;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.LongAdder;

@Component
@Slf4j
@RequiredArgsConstructor
public class TimerRouteBuilder extends RouteBuilder {

    private final KafkaOffsetManagerProcessor kafkaOffsetManagerProcessor;
    private final HealthCheckProcessor healthCheckProcessor;
    private final RegistryEventConsumer<CircuitBreaker> circuitBreakerRegistryEventConsumer;
    private ScheduledExecutorService executorService;
    private LongAdder count = new LongAdder();
    private static final int SIMULATED_FAILURES = 5;

    @Override
    public void configure() throws Exception {

        CircuitBreakerConfig config = CircuitBreakerConfig
                .custom()
                .slidingWindowType(CircuitBreakerConfig.SlidingWindowType.COUNT_BASED)
                .slidingWindowSize(10)
                .failureRateThreshold(70.0f)
                .build();

        CircuitBreakerRegistry registry = CircuitBreakerRegistry.of(config);

        CircuitBreaker circuitBreaker = registry.circuitBreaker("flightSearchService");

//        CircuitBreaker.

        /*
         * Set a watcher for the circuit breaker events. This watcher simulates a check for a downstream
         * system availability. It watches for error events and, when they happen, it triggers a scheduled
         * check (in this case, that simply increments a value). On success, it shuts down the scheduled check
         */
        circuitBreaker.getEventPublisher()
                .onSuccess(event -> {
                    log.info("Downstream call succeeded");
                    if (executorService != null) {
                        executorService.shutdownNow();
                        executorService = null;
                    }
                })
                .onError(event -> {
                    log.info(
                            "Downstream call error. Starting a thread to simulate checking for the downstream availability");

                    if (executorService == null) {
                        executorService = Executors.newSingleThreadScheduledExecutor();
                        // In a real world scenario, instead of incrementing, it could be pinging a remote system or
                        // running a similar check to determine whether it's available. That
                        executorService.scheduleAtFixedRate(() -> increment(), 1, 1, TimeUnit.SECONDS);
                    }
                });

        // Binds the configuration to the registry
        getCamelContext().getRegistry().bind("pausableCircuit", circuitBreakerRegistryEventConsumer);
//
//        onException(NonRecoverableException.class)
//                .handled(true)
//                .process(kafkaOffsetManagerProcessor);


//        from("kafka:MESSAGING-TIMER-EXAMPLE")
//                .routeId(TimerRouteBuilder.class.getName() + " Timer")
//                .pausable(new KafkaConsumerListener(), o -> canContinue())
//                .circuitBreaker()
//                    .resilience4jConfiguration().circuitBreaker("pausableCircuit").end()
//                .process(healthCheckProcessor)
//                .end()
//                .process(exchange -> {
//                    log.info(this.dumpKafkaDetails(exchange));
//                })
//                .process(kafkaOffsetManagerProcessor);

        from("kafka:MESSAGING-TIMER-EXAMPLE")
                .routeId(TimerRouteBuilder.class.getName() + " Timer")
//                .pausable(new KafkaConsumerListener(), o -> canContinue())
                .process(healthCheckProcessor)
                .process(exchange -> {
                    log.info(this.dumpKafkaDetails(exchange));
                })
                .process(kafkaOffsetManagerProcessor);
    }



    private boolean canContinue() {
        // First one should go through ...
        if (count.intValue() <= 1) {
            log.info("Count is 1, allowing processing to proceed");


            return true;
        }

        if (count.intValue() >= SIMULATED_FAILURES) {
            log.info("Count is {}, allowing processing to proceed because it's greater than retry count {}",
                    count.intValue(), SIMULATED_FAILURES);
            return true;
        }

        log.info("Cannot proceed at the moment ... count is {}", count.intValue());
        return false;
    }

    public void increment() {
        count.increment();
    }

    public int getCount() {
        return count.intValue();
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
