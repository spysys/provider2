package com.nexign.pipeeventsprovider.examples;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;

import com.nexign.pipeeventsprovider.hash.Hash;
import com.nexign.pipeeventsprovider.hashAlgorithms.AverageHash;
import com.nexign.pipeeventsprovider.hashAlgorithms.HashingAlgorithm;
import com.nexign.pipeeventsprovider.hashAlgorithms.PerceptiveHash;

/**
 * An example demonstrating how two images can be compared at a time using a single algorithm
 * 
 * @author Kilian
 *
 */
public class CompareImages {

	// Key bit resolution
	private int keyLength = 64;

	// Pick an algorithm
	private HashingAlgorithm hasher = new AverageHash(keyLength);

	// Images used for testing
	private HashMap<String, BufferedImage> images = new HashMap<>();

	
	public CompareImages() {

		loadImages();

		// Compare each picture to each other
		images.forEach((imageName, image) -> {
			images.forEach((imageName2, image2) -> {
				formatOutput(imageName, imageName2, compareTwoImages(image, image2));
			});
		});
	}

	/**
	 * Compares the similarity of two images.
	 * @param image1	First image to be matched against 2nd image
	 * @param image2	The second image
	 * @return	true if the algorithm defines the images to be similar.
	 */
	public boolean compareTwoImages(BufferedImage image1, BufferedImage image2) {

		//Generate the hash for each image
		Hash hash1 = hasher.hash(image1);
		Hash hash2 = hasher.hash(image2);

		//Compute a similarity score
		// Ranges between 0 - 1. The lower the more similar the images are.
		double similarityScore = hash1.normalizedHammingDistance(hash2);

		return similarityScore < 0.4d;
	}
	
	/**
	 * Compares the similarity of two images.
	 * @param image1	First image to be matched against 2nd image
	 * @param image2	The second image
	 * @return	true if the algorithm defines the images to be similar.
	 * @throws IOException IOerror occurred during image loading
	 */
	public static void main(String[] args) throws IOException {
		// Key bit resolution
		int keyLength = 256;

		// Pick an algorithm
		HashingAlgorithm hasher = new PerceptiveHash(keyLength);
		File file1 = new File("/Users/andrey.izzheurov/research/JImageHash/src/main/java/com/github/kilianB/examples/images/background.jpg");
		File file2 = new File("/Users/andrey.izzheurov/research/JImageHash/src/main/java/com/github/kilianB/examples/images/pipe3.jpg");
		//Generate the hash for each image
		Hash hash1 = hasher.hash(file1);
		Hash hash2 = hasher.hash(file2);

		// Ranges between [0 - keyLength]. The lower the more similar the images are.
		int similarityScore = hash1.hammingDistance(hash2);

		System.out.println(similarityScore);
	}

	
	//Utility function
	private void formatOutput(String image1, String image2, boolean similar) {
		String format = "| %-11s | %-11s | %-8b |%n";
		System.out.printf(format, image1, image2, similar);
	}
	
	private void loadImages() {
		// Load images
		try {
			images.put("background+pipe", ImageIO.read(new FileInputStream("/Users/andrey.izzheurov/research/JImageHash/src/main/java/com/github/kilianB/examples/images/background+pipe.jpg")));
			images.put("background", ImageIO.read(new FileInputStream("/Users/andrey.izzheurov/research/JImageHash/src/main/java/com/github/kilianB/examples/images/background.jpg")));
			images.put("background1", ImageIO.read(new FileInputStream("/Users/andrey.izzheurov/research/JImageHash/src/main/java/com/github/kilianB/examples/images/background1.jpg")));
			images.put("pipe1", ImageIO.read(new FileInputStream("/Users/andrey.izzheurov/research/JImageHash/src/main/java/com/github/kilianB/examples/images/pipe1.jpg")));
			images.put("pipe3", ImageIO.read(new FileInputStream("/Users/andrey.izzheurov/research/JImageHash/src/main/java/com/github/kilianB/examples/images/pipe3.jpg")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//Print header
		System.out.println("|   Image 1   |   Image 2   | Similar  |");
	}

//	public static void main(String[] args) {
//		new CompareImages();
//	}

}
