package image_char_matching;

import exceptions.EmptyCharsetException;
import java.util.*;

/**
 * A class that matches image brightness values to ASCII characters based on
 * their brightness.
 * This class maintains a mapping between normalized brightness values and
 * characters,
 * allowing for efficient character selection based on image brightness.
 *
 */
public class SubImgCharMatcher {
	/** Cache for storing raw brightness values of characters */
	private static final Map<Character, Double> rawCharBrightnessCache = new HashMap<>();

	/**
	 * Cache for storing normalized brightness values, keyed by charset
	 */
	private static final Map<String, double[]> normalizedBrightnessGlobalCache = new HashMap<>();

	/** The current rounding mode for brightness calculations */
	private static String roundingMode = "abs";

	/** The set of characters available for matching */
	private final char[] charset;

	/** List representation of the charset for easier manipulation */
	private final List<Character> charList;

	/** Key for the current charset's normalized brightness cache */
	private String normCacheKey = null;

	/** Minimum brightness value in the current charset */
	private double min = Double.MAX_VALUE;

	/** Maximum brightness value in the current charset */
	private double max = Double.MIN_VALUE;

	/** Mapping of brightness values to characters with that brightness */
	private TreeMap<Double, PriorityQueue<Character>> brightnessToCharsMap;

	/**
	 * Constructs a new SubImgCharMatcher with the specified charset.
	 *
	 * @param charset The set of characters to use for matching
	 */
	public SubImgCharMatcher(char[] charset) {
		this.charset = charset;
		this.charList = new ArrayList<>();
		for (char ch : charset) {
			charList.add(ch);
		}
		this.brightnessToCharsMap = new TreeMap<>();
		buildBrightnessMap();
	}

	/**
	 * Sets the rounding mode for brightness calculations.
	 * The mode determines how brightness values are rounded when selecting
	 * characters.
	 *
	 * @param mode The rounding mode ("abs", "up", or "down")
	 */
	public static void setRoundingMode(String mode) {
		if (mode.equals("abs") || mode.equals("up") || mode.equals("down")) {
			roundingMode = mode;
		}
	}

	/**
	 * Returns the character with the lowest ASCII value that best matches the given
	 * brightness.
	 * The selection is based on the current rounding mode and the available
	 * characters.
	 *
	 * @param brightness The brightness value to match
	 * @return The character that best matches the given brightness
	 * @throws EmptyCharsetException if no characters are available for matching
	 */
	public char getCharByImageBrightness(double brightness) {
		Map.Entry<Double, PriorityQueue<Character>> floor = brightnessToCharsMap.floorEntry(brightness);
		Map.Entry<Double, PriorityQueue<Character>> ceil = brightnessToCharsMap.ceilingEntry(brightness);

		if (floor == null && ceil == null) {
			throw new EmptyCharsetException();
		} else if (floor == null) {
			return ceil.getValue().peek();
		} else if (ceil == null) {
			return floor.getValue().peek();
		} else {
			switch (roundingMode) {
				case "up":
					return ceil.getValue().peek();
				case "down":
					return floor.getValue().peek();
				default: // "abs"
					double floorDiff = Math.abs(brightness - floor.getKey());
					double ceilDiff = Math.abs(brightness - ceil.getKey());
					if (floorDiff < ceilDiff) {
						return floor.getValue().peek();
					} else if (ceilDiff < floorDiff) {
						return ceil.getValue().peek();
					} else {
						char floorChar = floor.getValue().peek();
						char ceilChar = ceil.getValue().peek();
						return (floorChar < ceilChar) ? floorChar : ceilChar;
					}
			}
		}
	}

	/**
	 * Adds a character to the charset and updates the brightness mapping.
	 * Uses cached brightness values if available from previous processing.
	 *
	 * @param c The character to add
	 */
	public void addChar(char c) {
		charList.add(c);
		double brightness = getNormalizedBrightness(c, min, max);
		brightnessToCharsMap.computeIfAbsent(brightness, k -> new PriorityQueue<>()).add(c);
	}

	/**
	 * Removes a character from the charset and updates the brightness mapping.
	 *
	 * @param c The character to remove
	 */
	public void removeChar(char c) {
		charList.remove(Character.valueOf(c));
		double brightness = getNormalizedBrightness(c, min, max);
		PriorityQueue<Character> queue = brightnessToCharsMap.get(brightness);
		if (queue != null) {
			queue.remove(c);
			if (queue.isEmpty()) {
				brightnessToCharsMap.remove(brightness);
			}
		}
	}

	/**
	 * Builds a unique cache key for the current charset.
	 *
	 * @return A string key for the sorted charset
	 */
	private String buildNormCacheKey() {
		char[] sortedCharset = charset.clone();
		Arrays.sort(sortedCharset);
		return new String(sortedCharset);
	}

	/**
	 * Calculates the raw brightness of a character.
	 * Uses cached values if available from previous processing.
	 *
	 * @param c The character to calculate brightness for
	 * @return The raw brightness value of the character
	 */
	private double getRawBrightness(char c) {
		if (rawCharBrightnessCache.containsKey(c)) {
			return rawCharBrightnessCache.get(c);
		} else {
			int whiteCounter = 0;
			boolean[][] booleanArray = CharConverter.convertToBoolArray(c);
			for (int i = 0; i < booleanArray.length; i++) {
				for (int j = 0; j < booleanArray[i].length; j++) {
					if (booleanArray[i][j]) {
						whiteCounter++;
					}
				}
			}
			int arraySize = booleanArray.length * booleanArray[0].length;
			double brightness = (double) whiteCounter / arraySize;
			rawCharBrightnessCache.put(c, brightness);
			return brightness;
		}
	}

	/**
	 * Builds the mapping between brightness values and characters.
	 * Creates a TreeMap where each brightness value maps to a priority queue of
	 * characters.
	 */
	private void buildBrightnessMap() {
		brightnessToCharsMap.clear();
		double[] normalized = getNormalizedBrightnessForCharset();
		for (int i = 0; i < charList.size(); i++) {
			double brightness = normalized[i];
			brightnessToCharsMap.computeIfAbsent(brightness, k -> new PriorityQueue<>()).add(charList.get(i));
		}
	}

	/**
	 * Calculates normalized brightness values for all characters in the charset.
	 * Uses cached values if available for the current image and charset.
	 *
	 * @return An array of normalized brightness values corresponding to the charset
	 */
	private double[] getNormalizedBrightnessForCharset() {
		normCacheKey = buildNormCacheKey();
		if (normalizedBrightnessGlobalCache.containsKey(normCacheKey)) {
			return normalizedBrightnessGlobalCache.get(normCacheKey);
		}
		int n = charList.size();
		min = Double.MAX_VALUE;
		max = Double.MIN_VALUE;
		for (int i = 0; i < n; i++) {
			double b = getRawBrightness(charList.get(i));
			if (b < min)
				min = b;
			if (b > max)
				max = b;
		}
		double[] normalized = new double[n];
		for (int i = 0; i < n; i++) {
			normalized[i] = getNormalizedBrightness(charList.get(i), min, max);
		}
		normalizedBrightnessGlobalCache.put(normCacheKey, normalized);
		return normalized;
	}

	/**
	 * Calculates the normalized brightness for a single character.
	 * Used when adding a single character to the charset.
	 *
	 * @param c   The character to normalize
	 * @param min The minimum brightness value in the charset
	 * @param max The maximum brightness value in the charset
	 * @return The normalized brightness value
	 */
	private double getNormalizedBrightness(char c, double min, double max) {
		double cBrightness = getRawBrightness(c);
		if (max == min) {
			return 0.0;
		}
		return (cBrightness - min) / (max - min);
	}
}