package ascii_art;

import ascii_output.ConsoleAsciiOutput;
import ascii_output.HtmlAsciiOutput;
import exceptions.EmptyCharsetException;
import exceptions.ImageProcessingException;
import image.Image;
import image_char_matching.SubImgCharMatcher;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

/**
 * A command-line shell interface for ASCII art generation.
 * This class provides an interactive command-line interface for converting
 * images to ASCII art, allowing users to configure various parameters such as
 * character set, resolution, output format, and rounding mode.
 */
public class Shell {
	// Command constants
	private static final String CMD_EXIT = "exit";
	private static final String CMD_CHARS = "chars";
	private static final String CMD_ADD = "add";
	private static final String CMD_REMOVE = "remove";
	private static final String CMD_RES = "res";
	private static final String CMD_ROUND = "round";
	private static final String CMD_OUTPUT = "output";
	private static final String CMD_ASCII_ART = "asciiArt";

	// Command arguments
	private static final String ARG_ALL = "all";
	private static final String ARG_SPACE = "space";
	private static final String ARG_UP = "up";
	private static final String ARG_DOWN = "down";

	// Output types
	private static final String OUTPUT_CONSOLE = "console";
	private static final String OUTPUT_HTML = "html";

	// Rounding modes
	private static final String ROUND_ABS = "abs";
	private static final String ROUND_UP = "up";
	private static final String ROUND_DOWN = "down";

	// Error messages
	private static final String ERROR_INCORRECT_COMMAND =
			"Did not execute due to incorrect command.";
	private static final String ERROR_NO_CHARS =
			"Did not add/remove any characters.";
	private static final String ERROR_INCORRECT_RESOLUTION =
			"Did not change resolution due to exceeding boundaries.";
	private static final String ERROR_INCORRECT_FORMAT =
			"Did not change resolution due to incorrect format.";
	private static final String ERROR_INCORRECT_ROUNDING =
			"Did not change rounding mode due to incorrect format.";
	private static final String ERROR_INCORRECT_OUTPUT =
			"Did not change output method due to incorrect format.";
	private static final String ERROR_IMAGE_PATH =
			"ERROR: Must provide exactly one image file path as argument.";
	private static final String ERROR_PREFIX = "ERROR: ";

	// Status messages
	private static final String PROMPT = ">>> ";
	private static final String RESOLUTION_SET = "Resolution set to ";
	private static final String ROUNDING_MODE_SET = "Rounding mode is set to ";
	private static final String DOT = ".";
	private static final String SPACE = " ";

	// State fields
	private Image image;
	private TreeSet<Character> charset = new TreeSet<>();
	private int resolution;
	private String outputType;
	private String roundingMode;

	/**
	 * Constructs a new Shell instance with default settings:
	 * - Character set: digits 0-9
	 * - Resolution: 2
	 * - Output type: console
	 * - Rounding mode: absolute
	 */
	public Shell() {
		// Set defaults
		this.charset = new TreeSet<>();
		for (char c = '0'; c <= '9'; c++)
			this.charset.add(c);
		this.resolution = 2;
		this.outputType = OUTPUT_CONSOLE;
		this.roundingMode = ROUND_ABS;
	}

	/**
	 * Runs the shell with the specified image file.
	 * Provides an interactive command-line interface for ASCII art generation.
	 *
	 * @param imageName The path to the image file to process
	 * @throws ImageProcessingException If the image cannot be loaded
	 */
	public void run(String imageName) {
		try {
			this.image = new Image(imageName);
		} catch (IOException e) {
			throw new ImageProcessingException("Failed to load image: " + e.getMessage(), e);
		}

		while (true) {
			System.out.print(PROMPT);
			String input = KeyboardInput.readLine();
			if (input.startsWith(CMD_EXIT)) {
				break;
			} else if (input.startsWith(CMD_CHARS)) {
				charsCommand();
			} else if (input.startsWith(CMD_ADD)) {
				addRemoveCommand(input, true);
			} else if (input.startsWith(CMD_REMOVE)) {
				addRemoveCommand(input, false);
			} else if (input.startsWith(CMD_RES)) {
				resolutionCommand(input);
			} else if (input.startsWith(CMD_ROUND)) {
				roundCommand(input);
			} else if (input.startsWith(CMD_OUTPUT)) {
				outputCommand(input);
			} else if (input.startsWith(CMD_ASCII_ART)) {
				try {
					asciiArtCommand(input);
				} catch (EmptyCharsetException e) {
					System.out.println(ERROR_PREFIX + e.getMessage());
				} catch (ImageProcessingException e) {
					System.out.println(ERROR_PREFIX + e.getMessage());
				}
			} else {
				System.out.println(ERROR_INCORRECT_COMMAND);
			}
		}
	}

	/*
	 * Displays the current character set.
	 */
	private void charsCommand() {
		for (char c : charset) {
			System.out.print(c + SPACE);
		}
		System.out.println();
	}

	/*
	 * Adds or removes characters from the character set based on the input command.
	 *
	 * @param input    The command input string
	 * @param addition true for adding characters, false for removing
	 */
	private void addRemoveCommand(String input, boolean addition) {
		String[] tokens = input.trim().split("\\s+");
		if (tokens.length == 1) {
			System.out.println(ERROR_NO_CHARS);
			return;
		}
		String arg = tokens[1];
		if (arg.equals(ARG_ALL)) {
			for (char c = 32; c <= 126; c++)
				if (addition)
					charset.add(c);
				else
					charset.remove(c);
		} else if (arg.equals(ARG_SPACE)) {
			if (addition)
				charset.add(' ');
			else
				charset.remove(' ');
		} else if (arg.length() == 1) {
			if (addition)
				charset.add(arg.charAt(0));
			else
				charset.remove(arg.charAt(0));
		} else if (arg.length() == 3 && arg.charAt(1) == '-') {
			char start = (char) Math.min(arg.charAt(0), arg.charAt(2));
			char end = (char) Math.max(arg.charAt(0), arg.charAt(2));
			for (char c = start; c <= end; c++)
				if (addition)
					charset.add(c);
				else
					charset.remove(c);
		} else {
			System.out.println(ERROR_NO_CHARS);
		}
	}

	/*
	 * Handles resolution commands, allowing users to increase or decrease the
	 * resolution.
	 *
	 * @param input The command input string
	 */
	private void resolutionCommand(String input) {
		String[] tokens = input.trim().split("\\s+");
		if (tokens.length == 1) {
			if (tokens[0].startsWith(CMD_RES)) {
				System.out.println(RESOLUTION_SET + resolution + DOT);
				return;
			}
		}
		if (!(tokens[1].startsWith(ARG_UP) || tokens[1].startsWith(ARG_DOWN))) {
			System.out.println(ERROR_INCORRECT_FORMAT);
			return;
		}
		int minCharsInRow = Math.max(1, image.getWidth() / image.getHeight());
		int maxCharsInRow = image.getWidth();
		if (tokens[1].equals(ARG_UP)) {
			if (resolution * 2 > maxCharsInRow) {
				System.out.println(ERROR_INCORRECT_RESOLUTION);
				return;
			}
			resolution *= 2;
		} else if (tokens[1].equals(ARG_DOWN)) {
			if (resolution / 2 < minCharsInRow) {
				System.out.println(ERROR_INCORRECT_RESOLUTION);
				return;
			}
			resolution /= 2;
		}
		System.out.println(RESOLUTION_SET + resolution + DOT);
	}

	/*
	 * Handles rounding mode commands, allowing users to set the rounding behavior.
	 *
	 * @param input The command input string
	 */
	private void roundCommand(String input) {
		String[] tokens = input.trim().split("\\s+");
		if (tokens.length == 1) {
			System.out.println(ROUNDING_MODE_SET + roundingMode + DOT);
			return;
		}
		String mode = tokens[1].toLowerCase();
		if (mode.equals(ROUND_ABS) || mode.equals(ROUND_UP) || mode.equals(ROUND_DOWN)) {
			roundingMode = mode;
			SubImgCharMatcher.setRoundingMode(mode);
			System.out.println(ROUNDING_MODE_SET + mode + DOT);
		} else {
			System.out.println(ERROR_INCORRECT_ROUNDING);
		}
	}

	/*
	 * Handles output type commands, allowing users to switch between console and
	 * HTML output.
	 *
	 * @param input The command input string
	 */
	private void outputCommand(String input) {
		String[] tokens = input.trim().split("\\s+");
		if (tokens.length != 2 ||
				!(tokens[1].equals(OUTPUT_CONSOLE) || tokens[1].equals(OUTPUT_HTML))) {
			System.out.println(ERROR_INCORRECT_OUTPUT);
			return;
		}
		outputType = tokens[1];
	}

	/*
	 * Generates ASCII art from the current image using the configured settings.
	 *
	 * @param input The command input string
	 * 
	 * @throws EmptyCharsetException If the character set is too small
	 * 
	 * @throws ImageProcessingException If there's an error processing the image
	 */
	private void asciiArtCommand(String input) {
		if (charset.size() < 2) {
			throw new EmptyCharsetException("Charset is too small. Need at least 2 characters.");
		}
		// Convert charset to array for the algorithm
		char[] charsetArray = new char[charset.size()];
		int i = 0;
		for (char c : charset)
			charsetArray[i++] = c;

		// Run the algorithm
		AsciiArtAlgorithm algo = new AsciiArtAlgorithm(image, resolution, charsetArray, resolution);
		char[][] asciiResult = algo.run();

		if (outputType.equals(OUTPUT_CONSOLE)) {
			ConsoleAsciiOutput out = new ConsoleAsciiOutput();
			out.out(asciiResult);
		} else if (outputType.equals(OUTPUT_HTML)) {
			HtmlAsciiOutput out = new HtmlAsciiOutput("out.html", "Courier New");
			out.out(asciiResult);
		}
	}

	/**
	 * Main entry point for the ASCII art shell.
	 *
	 * @param args Command line arguments - must contain exactly one image file path
	 */
	public static void main(String[] args) {
		if (args.length != 1) {
			System.out.println(ERROR_IMAGE_PATH);
			return;
		}
		Shell shell = new Shell();
		try {
			shell.run(args[0]);
		} catch (ImageProcessingException e) {
			System.out.println(ERROR_PREFIX + e.getMessage());
			System.exit(1);
		}
	}
}
