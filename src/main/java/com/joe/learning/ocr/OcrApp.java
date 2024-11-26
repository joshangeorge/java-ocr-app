package com.joe.learning.ocr;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.ITesseract.RenderedFormat;
import net.sourceforge.tess4j.OCRResult;
import net.sourceforge.tess4j.Tesseract1;
import net.sourceforge.tess4j.TesseractException;

@Slf4j
public class OcrApp {

	public static void main(String args[]) {
		String imagePath = "data/sample_image_ocr.png";
		doOcr(imagePath, null, null);
	}

	private static void doOcr(String imagePath, String dataPath, String language) {
		try {
			// ITesseract instance = new Tesseract(); // JNA Interface Mapping
			ITesseract instance = new Tesseract1(); // JNA Direct Mapping
			// default eng
			if (language != null) {
				instance.setLanguage(language);
			}
			// path to tessdata directory
			if (dataPath != null) {
				instance.setDatapath(dataPath);
			} else {
				String customDataPath = "target/data";
				Files.createDirectories(Path.of(customDataPath));
				if (!Files.exists(Path.of(customDataPath + "/eng.traineddata"))) {
					InputStream in = (new URI(
							"https://raw.githubusercontent.com/tesseract-ocr/tessdata/refs/heads/main/eng.traineddata"))
							.toURL().openStream();
					Files.copy(in, Paths.get(customDataPath + "/eng.traineddata"), StandardCopyOption.REPLACE_EXISTING);
				}
				instance.setDatapath(customDataPath);
			}
			File imageFile = new File(imagePath);
			log.info(imageFile.getAbsolutePath());
			// tesseract.setVariable("tessedit_create_tsv", "true");

			String text = instance.doOCR(imageFile);
			System.out.print(text);

			log.info("-------");

			List<RenderedFormat> formats = Arrays.asList(RenderedFormat.values());
			String opPath = imagePath.replaceAll("\\..*", "");
			Files.deleteIfExists(Path.of(opPath + "-results/**"));
			Files.createDirectories(Path.of(opPath + "-results"));
			OCRResult result = instance.createDocumentsWithResults(imagePath, opPath + "-results/out", formats, 0);
			log.info("{}", result);
		} catch (TesseractException e) {
			log.error("Tesseract exception: " + e.getMessage(), e);
		} catch (IOException e) {
			log.error("Io exception: " + e.getMessage(), e);
		} catch (URISyntaxException e) {
			log.error("Uri syntax exception: " + e.getMessage(), e);
		} catch (Exception e) {
			log.error("Exception: " + e.getMessage(), e);
		}
	}
}
