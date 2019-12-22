package com.nexign.pipeeventsprovider.service.impl;

import com.nexign.pipeeventsprovider.hashAlgorithms.*;
import com.nexign.pipeeventsprovider.matcher.exotic.SingleImageMatcher;
import com.nexign.pipeeventsprovider.model.PipeEvent;
import com.nexign.pipeeventsprovider.service.Sender;
import com.nexign.pipeeventsprovider.service.Worker;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service
public class WorkerImpl implements Worker {

    @Value("${dHashThreshold}")
    private double dHashThreshold;

    @Value("${normalizedPHashThreshold}")
    private double /**/normalizedPHashThreshold;

    @Value("${background_identify_count}")
    private int background_identify_count;

    @Value("${pipe_identify_count}")
    private int pipe_identify_count;

    @Value("${baseBackground}")
    private String baseBackground;

    @Value("${pipesPath}")
    private String pipesPath;
    long count_of_photo = 0;
    int background_count = 0;
    int pipe = 0;
    int id = 0;
    boolean pipe_is_end = true;
    private HashMap<String, BufferedImage> images = new HashMap<>();

    private final Sender sender;

    public WorkerImpl(Sender sender) {
        this.sender = sender;
    }


    @Override
    public void run() {
        loadImages();

//		System.out.println("Example of thresholds for backgrounds: ");
//		chainAlgorithms(images.get("background"), images.get("background1"));

        try {
            Files.list(Paths.get(pipesPath))
                    .sorted()
                    .forEach(this::accept);

        } catch (IOException e) {
            log.error("IOException: {},  " , e.getMessage(), e.getCause());
        }

    }

    public boolean chainAlgorithms(BufferedImage image1, BufferedImage image2) {

        /*
         * Create multiple algorithms we want to test the images against
         */

        HashingAlgorithm dHash = new DifferenceHash(64, DifferenceHash.Precision.Double);
        // When shall an image be classified as a duplicate [0 - keyLenght]
        // DHashes double precision doubles the key length supplied in the constructor

        HashingAlgorithm pHash = new PerceptiveHash(64);
        // When shall an image be counted as a duplicate? [0-1]
        boolean normalized = true;

        // This instance can be reused. No need to recreate it every time you want to
        // match 2 images
        SingleImageMatcher matcher = new SingleImageMatcher();

        // Add algorithm to the matcher

        // First dirty filter
        matcher.addHashingAlgorithm(dHash, dHashThreshold);
        // If successful apply second filer
        matcher.addHashingAlgorithm(pHash, normalizedPHashThreshold, normalized);

        boolean background_image = matcher.checkSimilarity(image1, image2);

        if (background_image) {
            background_count++;
        } else {
            pipe ++;
            if(pipe > pipe_identify_count){
                background_count = 0;
                pipe_is_end = true;
            }
        }

        if (background_count >= background_identify_count && pipe_is_end) {
            background_count = 0;
            id++;
            pipe_is_end = false;
            pipe = 0;
            log.info("Increment id: " + id);
        }

        return !background_image;
//		System.out.println("background_image: " + background_image);
    }

    private void loadImages() {
        // Load images
        try {
            images.put("background", ImageIO.read(new FileInputStream(baseBackground)).getSubimage(560, 300, 490, 380));
        } catch (IOException e) {
            log.error("error opening file: {},  " , e.getMessage(), e.getCause());
        }
    }

    private void accept(Path c) {
        try {
            count_of_photo ++;
            if ((count_of_photo % 100) == 0)
                System.out.println(count_of_photo + ", id: " + id);
//            log.info("id: " + id + ", " + c.toAbsolutePath().toString());
            BufferedImage bufferedImage = ImageIO.read(new FileInputStream(c.toAbsolutePath().toString())).getSubimage(560, 300, 490, 380);
            boolean isPipe = chainAlgorithms(images.get("background"), bufferedImage);

            if (isPipe) {
                log.info("id: " + id + ", " + c.toAbsolutePath().toString());

                ByteArrayOutputStream baos = new ByteArrayOutputStream();

                ImageIO.write(bufferedImage, "jpg", baos);
                baos.flush();

                sender.sendMessage(this.buildPipeEvent(baos.toByteArray(), this.id));

                baos.close();
            }
        } catch (FileNotFoundException e) {
            log.error("FileNotFoundException: {},  " , e.getMessage(), e.getCause());
        } catch (IOException e) {
            log.error("IOException: {},  " , e.getMessage(), e.getCause());
        }
    }

    private PipeEvent buildPipeEvent(@NonNull byte[] imageSource, int pipe_id) throws IOException {
        Map<String, String> customParams = new HashMap<>();
        customParams.put("group_number", String.valueOf(pipe_id));
        customParams.put("distance_to_object", String.valueOf(new Random().nextInt(10)));
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
}
