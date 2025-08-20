package ascii_art;

import image.Image;
import image.BlockSplitter;
import image_char_matching.SubImgCharMatcher;
import java.util.Arrays;
import java.awt.Color;

/**
 * An algorithm for converting images to ASCII art.
 * This class implements the core algorithm for converting images to ASCII art,
 * including caching mechanisms for performance optimization and block-based
 * processing of the input image.
 */
public class AsciiArtAlgorithm {
	// Cache-related fields
	private static int lastImageHash = 0;
	private static int lastResolution = -1;
	private static double[][] lastBlockBrightness = null;
	private static Image[][] lastBlocks = null;

	// Instance fields
	private final Image img;
	private final int charsInRow;
	private final char[] charset;
	private final int resolution;

	/**
	 * Constructs a new AsciiArtAlgorithm instance.
	 *
	 * @param img        The input image to convert to ASCII art
	 * @param charsInRow The number of characters to use per row in the output
	 * @param charset    The set of characters to use in the ASCII art
	 * @param res        The resolution of the output
	 */
	public AsciiArtAlgorithm(Image img, int charsInRow, char[] charset, int res) {
		this.img = img;
		this.charsInRow = charsInRow;
		this.charset = charset;
		this.resolution = res;
	}

	/**
	 * Runs the ASCII art conversion algorithm.
	 * The algorithm processes the image in blocks, computing brightness values
	 * and matching them to appropriate characters from the charset.
	 * Includes caching mechanism to avoid redundant computations.
	 *
	 * @return A 2D array of characters representing the ASCII art
	 */
	public char[][] run() {
		Image padded = img.padToPowerOfTwo();
		int imageHash = Arrays.deepHashCode(buildPixelArray(padded));
		boolean useCache = (lastImageHash == imageHash &&
				lastResolution == charsInRow && lastBlockBrightness != null);
		if (!useCache) {
			lastBlocks = BlockSplitter.splitIntoBlocks(padded, charsInRow);
			int numRows = lastBlocks.length;
			int numCols = lastBlocks[0].length;
			lastBlockBrightness = new double[numRows][numCols];
			for (int by = 0; by < numRows; by++) {
				for (int bx = 0; bx < numCols; bx++) {
					lastBlockBrightness[by][bx] =
							BlockSplitter.computeBlockBrightness(lastBlocks[by][bx]);
				}
			}
			lastImageHash = imageHash;
			lastResolution = charsInRow;
		}

		int numRows = lastBlockBrightness.length;
		int numCols = lastBlockBrightness[0].length;
		char[][] result = new char[numRows][numCols];
		SubImgCharMatcher matcher = new SubImgCharMatcher(charset);
		for (int by = 0; by < numRows; by++) {
			for (int bx = 0; bx < numCols; bx++) {
				result[by][bx] = matcher.getCharByImageBrightness(lastBlockBrightness[by][bx]);
			}
		}
		return result;
	}

	/**
	 * Creates a 2D array of Color objects from an Image.
	 * This method is used to generate a hash of the image for caching purposes.
	 *
	 * @param image The image to convert to a Color array
	 * @return A 2D array of Color objects representing the image pixels
	 */
	private Color[][] buildPixelArray(Image image) {
		int h = image.getHeight();
		int w = image.getWidth();
		Color[][] arr = new Color[h][w];
		for (int y = 0; y < h; y++) {
			for (int x = 0; x < w; x++) {
				arr[y][x] = image.getPixel(y, x);
			}
		}
		return arr;
	}
}
