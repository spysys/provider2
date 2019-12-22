package com.nexign.pipeeventsprovider.service;

import java.io.IOException;

public interface ImageToKafkaPipelineService {
    void executePipeline() throws IOException;

    void sendMessage(String base64image);
}
