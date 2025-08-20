package image;

import java.awt.*;

/**
 * A utility class for splitting images into blocks and computing block
 * brightness.
 * This class provides functionality to divide an image into equal-sized square
 * blocks
 * and calculate the average brightness of each block using the standard
 * grayscale
 * conversion formula.
 */
public class BlockSplitter {
	/**
	 * Splits an image into a grid of equal-sized square blocks.
	 * The blocks are created by dividing the image into a grid where the number of
	 * blocks per row and column is specified. Each block maintains the same size,
	 * and any remaining space is padded with white pixels.
	 *
	 * @param image        The image to split into blocks
	 * @param blocksPerRow The number of blocks to create in each row and column
	 * @return A 2D array of Image objects representing the blocks
	 */
	public static Image[][] splitIntoBlocks(Image image, int blocksPerRow) {
		int imgWidth = image.getWidth();
		int imgHeight = image.getHeight();

		// Calculate block size to maintain square aspect ratio
		int blockWidth = Math.max(imgWidth, imgHeight) / blocksPerRow;
		int blocksPerCol = blocksPerRow;

		Image[][] blocks = new Image[blocksPerCol][blocksPerRow];

		for (int by = 0; by < blocksPerCol; by++) {
			for (int bx = 0; bx < blocksPerRow; bx++) {
				Color[][] pixels = new Color[blockWidth][blockWidth];
				for (int y = 0; y < blockWidth; y++) {
					for (int x = 0; x < blockWidth; x++) {
						int srcY = by * blockWidth + y;
						int srcX = bx * blockWidth + x;
						// Ensure we don't go out of bounds and handle padding
						if (srcY < imgHeight && srcX < imgWidth) {
							pixels[y][x] = image.getPixel(srcY, srcX);
						} else {
							// Pad with white if out of bounds
							pixels[y][x] = new Color(255, 255, 255);
						}
					}
				}
				blocks[by][bx] = new Image(pixels, blockWidth, blockWidth);
			}
		}
		return blocks;
	}

	/**
	 * Computes the average brightness of an image block.
	 * The brightness is calculated using the standard grayscale conversion formula:
	 * 0.2126 * R + 0.7152 * G + 0.0722 * B
	 * The result is normalized to a value between 0 and 1.
	 *
	 * @param block The image block to compute brightness for
	 * @return A double value between 0 and 1 representing the average brightness
	 */
	public static double computeBlockBrightness(Image block) {
		int height = block.getHeight();
		int width = block.getWidth();
		double sum = 0.0;
		int count = 0;

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				Color color = block.getPixel(y, x); // row,col
				double greyscale = color.getRed() * 0.2126 +
						color.getGreen() * 0.7152 +
						color.getBlue() * 0.0722;
				sum += greyscale;
				count++;
			}
		}
		double avg = sum / count;
		return avg / 255.0;
	}

}
