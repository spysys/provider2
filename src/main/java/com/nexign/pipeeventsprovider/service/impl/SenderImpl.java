package com.nexign.pipeeventsprovider.service.impl;

import com.nexign.pipeeventsprovider.model.PipeEvent;
import com.nexign.pipeeventsprovider.service.Sender;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
public class SenderImpl implements Sender {

    private static final Logger LOG = LoggerFactory.getLogger(SenderImpl.class);

    @Autowired
    private KafkaTemplate<String, PipeEvent> kafkaTemplate;

    @Value("${kafka.pipe-detector-topic}")
    private String targetTopic;

    public void sendMessage(PipeEvent data){

        Message<PipeEvent> message = MessageBuilder
                .withPayload(data)
                .setHeader(KafkaHeaders.TOPIC, targetTopic)
                .setHeader(KafkaHeaders.PARTITION_ID, 0)
                .build();

        LOG.info("sending message='{}' to topic='{}'", data, targetTopic);
        kafkaTemplate.send(message);
    }

    @Override
    public void sendBar(String data) {

    }
}