package image;

import exceptions.ImageProcessingException;
import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * A class representing an image with color pixel data.
 * This class provides functionality for loading, manipulating, and saving
 * images.
 * It supports operations such as padding images to power-of-two dimensions
 * and accessing individual pixel colors.
 *
 * @author Dan Nirel
 */
public class Image {

    /** 2D array storing the color data for each pixel in the image */
    private final Color[][] pixelArray;

    /** The width of the image in pixels */
    private final int width;

    /** The height of the image in pixels */
    private final int height;

    /**
     * Constructs a new Image by loading it from a file.
     *
     * @param filename The path to the image file to load
     * @throws IOException If the image file cannot be read
     */
    public Image(String filename) throws IOException {
        BufferedImage im = ImageIO.read(new File(filename));
        width = im.getWidth();
        height = im.getHeight();

        pixelArray = new Color[height][width];
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                pixelArray[i][j] = new Color(im.getRGB(j, i));
            }
        }
    }

    /**
     * Constructs a new Image from existing pixel data.
     *
     * @param pixelArray 2D array of Color objects representing the image pixels
     * @param width      The width of the image in pixels
     * @param height     The height of the image in pixels
     */
    public Image(Color[][] pixelArray, int width, int height) {
        this.pixelArray = pixelArray;
        this.width = width;
        this.height = height;
    }

    /**
     * Returns the width of the image.
     *
     * @return The width of the image in pixels
     */
    public int getWidth() {
        return width;
    }

    /**
     * Returns the height of the image.
     *
     * @return The height of the image in pixels
     */
    public int getHeight() {
        return height;
    }

    /**
     * Returns the color of a specific pixel in the image.
     *
     * @param x The x-coordinate of the pixel
     * @param y The y-coordinate of the pixel
     * @return The Color object representing the pixel's color
     */
    public Color getPixel(int x, int y) {
        return pixelArray[x][y];
    }

    /**
     * Saves the image to a file in JPEG format.
     * The file will be saved with the provided filename and .jpeg extension.
     *
     * @param fileName The name of the file to save the image to (without extension)
     * @throws ImageProcessingException If the image cannot be saved
     */
    public void saveImage(String fileName) {
        BufferedImage bufferedImage = new BufferedImage(pixelArray[0].length, pixelArray.length,
                BufferedImage.TYPE_INT_RGB);
        for (int x = 0; x < pixelArray.length; x++) {
            for (int y = 0; y < pixelArray[x].length; y++) {
                bufferedImage.setRGB(y, x, pixelArray[x][y].getRGB());
            }
        }
        File outputfile = new File(fileName + ".jpeg");
        try {
            ImageIO.write(bufferedImage, "jpeg", outputfile);
        } catch (IOException e) {
            throw new ImageProcessingException("Failed to save image: " + e.getMessage(), e);
        }
    }

    /**
     * (added to API)
     * Pads the image so that its width and height are powers of 2.
     * The original image is centered in the padded area, with white pixels
     * filling the remaining space.
     *
     * @return A new Image instance with dimensions that are powers of 2
     */
    public Image padToPowerOfTwo() {
        int currentWidth = getWidth();
        int currentHeight = getHeight();
        int newWidth = nextPowerOfTwo(currentWidth);
        int newHeight = nextPowerOfTwo(currentHeight);

        if (currentWidth == newWidth && currentHeight == newHeight) {
            return this; // No padding needed
        }

        int padLeft = (newWidth - currentWidth) / 2;
        int padTop = (newHeight - currentHeight) / 2;

        Color[][] newPixels = new Color[newHeight][newWidth];
        // Fill with white
        for (int y = 0; y < newHeight; y++) {
            for (int x = 0; x < newWidth; x++) {
                newPixels[y][x] = new Color(255, 255, 255);
            }
        }
        // Copy old image
        for (int y = 0; y < currentHeight; y++) {
            for (int x = 0; x < currentWidth; x++) {
                newPixels[y + padTop][x + padLeft] = getPixel(y, x);
            }
        }
        return new Image(newPixels, newWidth, newHeight);
    }

    /*
     * Checks if a number is a power of 2.
     */
    private boolean isPowerOfTwo(int x) {
        return (x > 0 && (x & (x - 1)) == 0);
    }

    /*
     * Returns the next power of 2 that is greater than or equal to the given
     * number.
     * If the input is already a power of 2, returns the input.
     */
    private int nextPowerOfTwo(int x) {
        if (isPowerOfTwo(x)) {
            return x;
        }
        int power = 1;
        while (power < x) {
            power *= 2;
        }
        return power;
    }
}
