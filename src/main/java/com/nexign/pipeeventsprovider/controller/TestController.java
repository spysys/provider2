package com.nexign.pipeeventsprovider.controller;

import com.nexign.pipeeventsprovider.service.ImageToKafkaPipelineService;
import com.nexign.pipeeventsprovider.service.Worker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RequestMapping("/test")
@RestController
public class TestController {

    private final ImageToKafkaPipelineService imageToKafkaPipelineService;
    private final Worker worker;

    @Autowired
    public TestController(ImageToKafkaPipelineService imageToKafkaPipelineService, Worker worker) {
        this.imageToKafkaPipelineService = imageToKafkaPipelineService;
        this.worker = worker;
    }

//    @GetMapping("/test")
//    public void testPipeline() throws IOException {
//        imageToKafkaPipelineService.executePipeline();
//    }

    @PostMapping("/push")
    public void testPipeline(@RequestBody String base64image) throws IOException {
        imageToKafkaPipelineService.sendMessage(base64image);
    }

    @PostMapping("/run")
    public void start() throws IOException {
        worker.run();
    }
}
