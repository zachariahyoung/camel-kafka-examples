package com.zandriod.producer.producer.processor;

import com.google.protobuf.Timestamp;
import com.zandriod.producer.producer.proto.Timer;
import lombok.extern.slf4j.Slf4j;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
public class ProtobufProcessor implements Processor {

    @Value("${type:resilience}")
    private String type;

    @Override
    public void process(Exchange exchange) throws Exception {
        Instant now = Instant.now();
        Timestamp timestamp = Timestamp.newBuilder().setSeconds(now.getEpochSecond()).setNanos(now.getNano()).build();

        Timer timerMessage = Timer.newBuilder()
                .setName("Timer message")
                .setValue(1)
                .setStatusType(Timer.StatusType.Active)
                .setTimerDatetime(timestamp)
                .build();
        exchange.getIn().setBody(timerMessage);
        log.info("Timer Message: " + timerMessage.toString());

        exchange.getIn().setHeaders(SetHeaders());
    }

    private Map<String, Object> SetHeaders(){

        log.info("type "+ type);

        UUID uuid = UUID.randomUUID();
        Map<String, String> headers = new HashMap<>();
        headers.put("ce_specversion","1.0");
        headers.put("ce_type","com.zandriod.messaging."+ type);
        headers.put("ce_id",uuid.toString());
        headers.put("content-type","application/proto");
        Map readOnlyMap = Collections.unmodifiableMap(headers);
        return readOnlyMap;
    }
}
