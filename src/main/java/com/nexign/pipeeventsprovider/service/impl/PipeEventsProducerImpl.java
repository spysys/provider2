package com.nexign.pipeeventsprovider.service.impl;

import com.nexign.pipeeventsprovider.model.PipeEvent;
import com.nexign.pipeeventsprovider.service.PipeEventsProducer;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Slf4j
@Service
public class PipeEventsProducerImpl implements PipeEventsProducer {

    private static final Logger LOG = LoggerFactory.getLogger(PipeEventsProducerImpl.class);

    private final KafkaTemplate<String, PipeEvent> kafkaTemplate;

    @Value("${kafka.pipe-detector-topic}")
    private String pipeDetectorTopicName = "com.nexign.events.PipeEvent";

    @Value("${kafka.pipe-detector-topic-prefix}")
    private String pipeEventTopicPrefix;

    private String fullPipeEventsTopicName;

    @PostConstruct
    void postConstruct() {
        this.fullPipeEventsTopicName = pipeEventTopicPrefix.concat(pipeDetectorTopicName);
    }

    @Autowired
    public PipeEventsProducerImpl(KafkaTemplate<String, PipeEvent> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    @Override
    public void produce(PipeEvent pipeEvent) {
        LOG.debug("send message to topic {}", pipeEvent);
        kafkaTemplate.send(fullPipeEventsTopicName, "asdfasf", pipeEvent);
    }

}
