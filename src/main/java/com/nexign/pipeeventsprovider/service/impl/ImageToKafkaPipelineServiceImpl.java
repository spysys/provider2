package com.nexign.pipeeventsprovider.service.impl;

import com.nexign.pipeeventsprovider.model.PipeEvent;
import com.nexign.pipeeventsprovider.service.ImageToKafkaPipelineService;
import com.nexign.pipeeventsprovider.service.PipeEventsProducer;
import com.nexign.pipeeventsprovider.service.Sender;
import com.nexign.pipeeventsprovider.service.SourceProvider;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service
public class ImageToKafkaPipelineServiceImpl implements ImageToKafkaPipelineService {

    private static final Logger LOG = LoggerFactory.getLogger(SourceProviderImpl.class);

    private final SourceProvider sourceProvider;
    private final PipeEventsProducer pipeEventsProducer;
    private final Sender sender;


    @Autowired
    public ImageToKafkaPipelineServiceImpl(SourceProvider sourceProvider, PipeEventsProducer pipeEventsProducer, Sender sender) {
        this.sourceProvider = sourceProvider;
        this.pipeEventsProducer = pipeEventsProducer;
        this.sender = sender;
    }

    @Override
    @Scheduled(initialDelayString = "${scheduled.initial-delay}", fixedDelayString = "${scheduled.task-period}")
    public void executePipeline() {
        byte[] image = sourceProvider.getImage();
        if (image != null) {
            LOG.info("Getting image from SFTP proxy server, size: {}", image.length);
            try {
                sender.sendMessage(buildPipeEvent(image));
            } catch (IOException e) {
                LOG.error("ERROR: message: {}, cause: {} ", e.getMessage(), e.getCause());
            }
        }
    }

    @Override
    public void sendMessage(String base64image) {
        sender.sendMessage(buildPipeEvent(base64image));
    }

    private PipeEvent buildPipeEvent(@NonNull byte[] imageSource) throws IOException {
        Map<String, String> customParams = new HashMap<>();
        customParams.put("distance_to_object", String.valueOf(new Random().nextInt(10)));
        customParams.put("group_number", "0");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE MMM dd kk:mm:ss zzz yyyy", Locale.ENGLISH);
        String dateTime = simpleDateFormat.format(new Date());
        return PipeEvent.builder()
                .id(UUID.randomUUID())
                .photoByteArray(org.springframework.util.Base64Utils.encodeToString(imageSource))
                .timestamp(dateTime)
                .taskId(UUID.randomUUID())
                .customParameters(customParams)
                .build();
    }

    private PipeEvent buildPipeEvent(@NonNull String base64image) {
        Map<String, String> customParams = new HashMap<>();
        customParams.put("distance_to_object", String.valueOf(new Random().nextInt(10)));
        customParams.put("group_number", "0");
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEE MMM dd kk:mm:ss zzz yyyy", Locale.ENGLISH);
        String dateTime = simpleDateFormat.format(new Date());
        return PipeEvent.builder()
                .id(UUID.randomUUID())
                .photoByteArray(base64image)
                .timestamp(dateTime)
                .taskId(UUID.randomUUID())
                .customParameters(customParams)
                .build();
    }
}
