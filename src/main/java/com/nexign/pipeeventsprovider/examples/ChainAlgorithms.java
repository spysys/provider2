package com.nexign.pipeeventsprovider.examples;

import java.awt.image.BufferedImage;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;

import javax.imageio.ImageIO;

import com.nexign.pipeeventsprovider.hashAlgorithms.AverageHash;
import com.nexign.pipeeventsprovider.hashAlgorithms.DifferenceHash;
import com.nexign.pipeeventsprovider.hashAlgorithms.DifferenceHash.Precision;
import com.nexign.pipeeventsprovider.hashAlgorithms.HashingAlgorithm;
import com.nexign.pipeeventsprovider.hashAlgorithms.PerceptiveHash;
import com.nexign.pipeeventsprovider.hashAlgorithms.WaveletHash;
import com.nexign.pipeeventsprovider.matcher.exotic.SingleImageMatcher;

/**
 * To increase the quality of the returned results it can be useful to chain
 * multiple algorithms back to back due to the different features each
 * algorithms compares.
 * 
 * @author Kilian
 *
 */
public class ChainAlgorithms {

	int background_count = 0;
	int pipe = 0;
	int id = 0;
	boolean pipe_is_end = true;
	// Images used for testing
	private HashMap<String, BufferedImage> images = new HashMap<>();

	public void defaultMatcher() {

		/*
		 * A single image matcher allows to compare two images against each other. The
		 * default matcher chains an average hash followed by a perceptive hash
		 */
		SingleImageMatcher matcher = new SingleImageMatcher();
		
		//Add hashing algorithms as you please. Both hashes will be queried
		matcher.addHashingAlgorithm(new AverageHash(64),.3);
		matcher.addHashingAlgorithm(new WaveletHash(32,3),.3);

		// Lets get two images
		BufferedImage img1 = images.get("ballon");
		BufferedImage img2 = images.get("copyright");

		// Check if the images are similar
		if (matcher.checkSimilarity(img1, img2)) {
			System.out.println("Ballon & Low Quality are likely duplicates");
		} else {
			System.out.println("Ballon & Low Quality are distinct images");
		}
	}


	/**
	 * Demonstrates how to fully configure a SingleImageMatcher. Choose own
	 * algorithms and thresholds
	 * 
	 * @param image1 First image to be matched against 2nd image
	 * @param image2 Second image to be matched against the first image
	 */
	public void chainAlgorithms(BufferedImage image1, BufferedImage image2) {

		/*
		 * Create multiple algorithms we want to test the images against
		 */

		HashingAlgorithm dHash = new DifferenceHash(64, Precision.Double);
		// When shall an image be classified as a duplicate [0 - keyLenght]
		// DHashes double precision doubles the key length supplied in the constructor
		double dHashThreshold = .15;

		HashingAlgorithm pHash = new PerceptiveHash(64);
		// When shall an image be counted as a duplicate? [0-1]
		double normalizedPHashThreshold = 0.15;
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

		if (background_image && (background_count < 10)) {
			background_count++;
		} else {
			pipe ++;
			if(pipe > 15){
				background_count = 0;
				pipe_is_end = true;
			}
		}

		if (background_count >= 10 && pipe_is_end) {
			background_count = 0;
			id++;
			pipe_is_end = false;
			pipe = 0;
			System.out.println("Pipe id: " + id);
		}

//		System.out.println("background_image: " + background_image);
	}

	public static void main(String[] args) {
		new ChainAlgorithms();
	}

	public ChainAlgorithms() {
		loadImages();

//		System.out.println("Example of thresholds for backgrounds: ");
//		chainAlgorithms(images.get("background"), images.get("background1"));

		try {
			Files.list(Paths.get("/Users/andrey.izzheurov/research/JImageHash/src/main/java/com/github/kilianB/examples/pipes"))
				.sorted()
				.forEach(this::accept);


		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void loadImages() {
		// Load images
		try {
			images.put("background", ImageIO.read(new FileInputStream("/Users/andrey.izzheurov/research/JImageHash/src/main/java/com/github/kilianB/examples/images/background1.jpg")).getSubimage(560, 300, 490, 380));
//			File outputfile = new File("/Users/andrey.izzheurov/research/JImageHash/src/main/java/com/github/kilianB/examples/images/image.jpg");
//			ImageIO.write(ImageIO.read(new FileInputStream("/Users/andrey.izzheurov/research/JImageHash/src/main/java/com/github/kilianB/examples/images/background1.jpg")).getSubimage(560, 300, 490, 380), "jpg", outputfile);
//			images.put("background1", ImageIO.read(new FileInputStream("/Users/andrey.izzheurov/research/JImageHash/src/main/java/com/github/kilianB/examples/images/background.jpg")));
//			images.put("highQuality", ImageIO.read(new FileInputStream("/Users/andrey.izzheurov/research/JImageHash/src/main/java/com/github/kilianB/examples/images/highQuality.jpg")));
//			images.put("lowQuality", ImageIO.read(new FileInputStream("/Users/andrey.izzheurov/research/JImageHash/src/main/java/com/github/kilianB/examples/images/lowQuality.jpg")));
//			images.put("thumbnail", ImageIO.read(getClass().getResourceAsStream("/Users/andrey.izzheurov/research/JImageHash/src/main/java/com/github/kilianB/examples/images/thumbnail.jpg")));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void accept(Path c) {
		try {
			System.out.println("id: " + id + ", " + c.toAbsolutePath().toString());
			BufferedImage bufferedImage = ImageIO.read(new FileInputStream(c.toAbsolutePath().toString())).getSubimage(560, 300, 490, 380);
			chainAlgorithms(images.get("background"), bufferedImage);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
