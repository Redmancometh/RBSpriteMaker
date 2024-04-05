package com.bcurry.sprites;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

import javax.imageio.ImageIO;

import lombok.Data;

/**
 * 
 * 
 * @author Redmancometh
 *
 */
@Data
public class SpriteProcessor {

	/**
	 * 
	 * Doing them one line at a time to guarantee order, and in case I want to
	 * annotate any.
	 *
	 */
	private int width;
	private int height;
	private int padding;
	private int imgPerRow;
	private List<File> inputFiles;
	private String outputFile;

	public SpriteProcessor(int width, int height, int imgPerRow, List<File> inputFiles, String outputFile) {
		this.width = width;
		this.height = height;
		this.imgPerRow = imgPerRow;
		this.outputFile = outputFile;
		this.inputFiles = inputFiles;
	}

	public void process() {
		System.out.println("Beginning processing");
		int totalHeight = (inputFiles.size() / imgPerRow) * (height);
		int totalWidth = (imgPerRow + 1) * width;
		BufferedImage out = new BufferedImage(totalWidth, totalHeight, BufferedImage.TYPE_INT_ARGB);
		Graphics2D g = (Graphics2D) out.getGraphics();
		AtomicInteger imageInRow = new AtomicInteger(0);
		AtomicInteger column = new AtomicInteger(0);
		int x = 0;
		for (File imageFile : inputFiles) {
			try {
				System.out.println(x++);
				if (imageFile.isDirectory())
					continue;
				if (!imageFile.getName().endsWith("png"))
					continue;
				BufferedImage img = ImageIO.read(imageFile);
				int yAmt = imageInRow.get() * width;
				int xAmt = column.get() * height;
				g.drawImage(img, xAmt, yAmt, null);
				if (column.get() >= imgPerRow) {
					column.set(0);
					imageInRow.getAndIncrement();
					continue;
				}
				column.getAndIncrement();
			} catch (IOException e) {
				e.printStackTrace();
				System.out.println("An error occurred: please provide the stacktrace above to Redmancometh");
			}
		}
		try {
			ImageIO.write(out, "PNG", new File(outputFile));
			Logger.getLogger("parserlog").info("Wrote file " + outputFile);
		} catch (IOException e) {
			e.printStackTrace();
			Logger.getLogger("parserlog").warning(
					"Output file failed to write! Are you trying to use a directory you don't have write access to?");
		}
	}

}
