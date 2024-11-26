package com.joe.learning.ocr;

import java.io.File;
import java.util.Arrays;

import com.azure.ai.vision.imageanalysis.ImageAnalysisClient;
import com.azure.ai.vision.imageanalysis.ImageAnalysisClientBuilder;
import com.azure.ai.vision.imageanalysis.models.DetectedTextLine;
import com.azure.ai.vision.imageanalysis.models.DetectedTextWord;
import com.azure.ai.vision.imageanalysis.models.ImageAnalysisResult;
import com.azure.ai.vision.imageanalysis.models.VisualFeatures;
import com.azure.core.credential.KeyCredential;
import com.azure.core.util.BinaryData;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class OcrAzureVisionApp {

	public static void main(String args[]) {

		String imagePath = "data/sample_image_ocr.png";
		doOcr(imagePath, System.getProperty("ApiUrl"), System.getProperty("ApiKey"));
	}

	private static void doOcr(String imagePath, String apiUrl, String apiKey) {
		try {
			ImageAnalysisClient client = new ImageAnalysisClientBuilder().endpoint(apiUrl)
					.credential(new KeyCredential(apiKey)).buildClient();

			// sync
			ImageAnalysisResult result = client.analyze(BinaryData.fromFile(new File(imagePath).toPath()),
					Arrays.asList(VisualFeatures.READ), // visualFeatures
					null); // options: There are no options for READ visual feature

			// Print analysis results to the console
			log.info("Image analysis results:");
			log.info(" Read:");
			for (DetectedTextLine line : result.getRead().getBlocks().get(0).getLines()) {
				log.info("   Line: '" + line.getText() + "', Bounding polygon " + line.getBoundingPolygon());
				for (DetectedTextWord word : line.getWords()) {
					log.info("     Word: '" + word.getText() + "', Bounding polygon " + word.getBoundingPolygon()
							+ ", Confidence " + String.format("%.4f", word.getConfidence()));
				}
			}
			log.info("------");
			for (DetectedTextLine line : result.getRead().getBlocks().get(0).getLines()) {
				log.info(line.getText());
			}
		} catch (Exception e) {
			log.error("Exception: " + e.getMessage(), e);
		}
	}
}
