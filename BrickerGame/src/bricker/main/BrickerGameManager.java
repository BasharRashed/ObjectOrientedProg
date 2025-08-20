package bricker.main;

import bricker.brick_strategies.*;
import bricker.gameobjects.Ball;
import bricker.gameobjects.Brick;
import bricker.gameobjects.VisualHeart;
import bricker.gameobjects.Paddle;
import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.components.CoordinateSpace;
import danogl.gui.*;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import bricker.gameobjects.NumericLivesCounter;
import danogl.util.Counter;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * BrickerGameManager is the main game controller class for the Bricker game.
 * It initializes game objects, handles game state transitions, and manages user interactions.
 *
 * @see GameManager
 */
public class BrickerGameManager extends GameManager {
	/** Screen width size. */
	public static final int SCREEN_WIDTH = 700;

	/** Screen height size. */
	public static final int SCREEN_HEIGHT = 500;

	/** Number of starting diagonal directions. */
	private static final int RANDOM_DIAGONAL_NUMBER = 4;

	/** Default number of brick rows. */
	private static final int DEFAULT_ROWS = 7;

	/** Default number of bricks per row. */
	private static final int DEFAULT_BRICKS_PER_ROW = 8;

	/** Constant height of each brick in pixels. */
	private static final float BRICK_HEIGHT = 15f;

	/** Padding from wall edges to game objects. */
	private static final float WALL_PADDING = 10f;

	/** Vertical padding between brick rows. */
	private static final float BRICK_VERTICAL_PADDING = 3f;

	/** Padding from top of screen to top brick row. */
	private static final float TOP_PADDING = 10f;

	/** Starting number of lives. */
	private static final int STARTING_LIVES  = 3;

	/** Main ball speed. */
	private static final float BALL_SPEED  = 200f;

	/** Winning Prompt. */
	private static final String WINNING_PROMPT = "You Win! Play again?";

	/** Winning Prompt. */
	private static final String LOSING_PROMPT = "You Lose! Play again?";


	/** Actual number of rows of bricks in the current game. */
	private final int numberOfRows;

	/** Actual number of bricks per row in the current game. */
	private final int bricksPerRow;

	/** Counter tracking the number of lives remaining. */
	private Counter livesCounter;

	/** Reference to the main ball in the game. */
	private Ball ball;

	/** Shared static counter tracking remaining bricks in the game. */
	private static Counter bricksCounter;

	/** List of visual heart icons on the screen representing lives. */
	private List<VisualHeart> hearts = new ArrayList<>();

	/** The main user-controlled paddle object. */
	private Paddle mainPaddle;

	/** Window controller for dialog and window interactions. */
	private WindowController windowController;

	/** Reader to load images. */
	private ImageReader imageReader;

	/** Reader to load sound clips. */
	private SoundReader soundReader;

	/** Visual numeric display of lives left. */
	private NumericLivesCounter livesCounterDisplay;

	/** Listener for user input (e.g., keyboard). */
	private UserInputListener inputListener;

	/** Factory that builds collision strategies for bricks. */
	private StrategyFactory strategyFactory;

	/**
	 * Constructor allowing custom brick layout.
	 * @param windowTitle Title of the game window.
	 * @param windowDimensions Dimensions of the game window.
	 * @param numberOfRows Number of brick rows.
	 * @param bricksPerRow Number of bricks per row.
	 */

	public BrickerGameManager(String windowTitle, Vector2 windowDimensions,
							  int numberOfRows, int bricksPerRow) {
		super(windowTitle, windowDimensions);
		this.numberOfRows = numberOfRows;
		this.bricksPerRow = bricksPerRow;
	}

	/**
	 * Initializes the game with required game objects.
	 * @param imageReader Utility to read images.
	 * @param soundReader Utility to read sound clips.
	 * @param inputListener Listener for user input.
	 * @param windowController Controller for window dialogs.
	 */
	@Override
	public void initializeGame(ImageReader imageReader, SoundReader soundReader,
							   UserInputListener inputListener,
							   WindowController windowController) {
		// Standard initialization of game resources
		this.imageReader = imageReader;
		this.soundReader = soundReader;
		this.inputListener = inputListener;
		super.initializeGame(imageReader, soundReader, inputListener, windowController);
		this.windowController = windowController;
		Vector2 windowDimensions = windowController.getWindowDimensions();
		bricksCounter = new Counter(numberOfRows * bricksPerRow);
		livesCounter = new Counter(STARTING_LIVES);

		createBall(imageReader, soundReader, windowDimensions);
		createPaddle(imageReader, inputListener, windowDimensions);
		createWalls(windowDimensions);
		createBackground(imageReader, windowController);

		Renderable heartImage = imageReader.readImage("assets/heart.png", true);
		VisualHeart.configure(gameObjects(), livesCounter, heartImage, windowController, ball);

		Vector2 size = new Vector2(15, 15);
		NumericLivesCounter.configure(gameObjects(), livesCounter, windowController, size);
		strategyFactory = new StrategyFactory(
				gameObjects(), imageReader, soundReader, windowController,
				bricksCounter, ball, inputListener, mainPaddle, livesCounter);

		createBricks(imageReader, windowController);
	}

	/**
	 * Application entry point.
	 * @param args Optional arguments specifying rows and columns of bricks.
	 */
	public static void main(String[] args) {
		if (args.length == 2) {
			int bricksPerRow = Integer.parseInt(args[0]);
			int numberOfRows = Integer.parseInt(args[1]);
			BrickerGameManager game = new BrickerGameManager("Bricker",
					new Vector2(SCREEN_WIDTH, SCREEN_HEIGHT),
					numberOfRows, bricksPerRow);
			game.run();
		} else {
			BrickerGameManager game = new BrickerGameManager("Bricker",
					new Vector2(SCREEN_WIDTH, SCREEN_HEIGHT),
					DEFAULT_ROWS, DEFAULT_BRICKS_PER_ROW);
			game.run();
		}
	}

	/**
	 * Main game loop update method.
	 * @param deltaTime Time since last update.
	 */
	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		if (inputListener.wasKeyPressedThisFrame(KeyEvent.VK_W)) {
			bricksCounter.reset();
		}
		CheckGameEnd();
	}

	/*
	 * Checks if the game is over due to win or loss.
	 */
	private void CheckGameEnd() {
		String prompt = "";
		if (bricksCounter.value() <= 0) {
			prompt += WINNING_PROMPT;
			if (windowController.openYesNoDialog(prompt)) {
				resetGameState();
			} else {
				windowController.closeWindow();
			}
		}
		if (livesCounter.value() > 0) {
			for (GameObject obj : gameObjects().objectsInLayer(Layer.BACKGROUND)) {
				if (obj instanceof VisualHeart) {
					VisualHeart heart = (VisualHeart) obj;
					if (heart.removeHeart()) {
						livesCounter.decrement();
						VisualHeart.updateHearts(gameObjects(), windowController, ball);
						reposition(windowController.getWindowDimensions());
						break;
					}
				}
			}
		} else {
			prompt += LOSING_PROMPT;
			if (windowController.openYesNoDialog(prompt)) {
				resetGameState();
			} else {
				windowController.closeWindow();
			}
		}
	}

	/*
	 * Resets the game to its initial state.
	 */
	private void resetGameState() {
		for (GameObject obj : gameObjects().objectsInLayer(Layer.DEFAULT)) {
			gameObjects().removeGameObject(obj);
		}
		for (GameObject obj : gameObjects().objectsInLayer(Layer.STATIC_OBJECTS)) {
			gameObjects().removeGameObject(obj, Layer.STATIC_OBJECTS);
		}
		for (GameObject obj : gameObjects().objectsInLayer(Layer.BACKGROUND)) {
			gameObjects().removeGameObject(obj, Layer.BACKGROUND);
		}

		livesCounter.reset();
		livesCounter.increment();
		livesCounter.increment();
		livesCounter.increment();

		initializeGame(imageReader, soundReader, inputListener, windowController);
	}

	/*
	 * Creates a black wall GameObject and adds it to the game.
	 * @param topLeftCorner Position of the wall's top-left corner.
	 * @param dimensions Size of the wall.
	 * @param color Color of the wall.
	 */
	private void createWall(Vector2 topLeftCorner, Vector2 dimensions, Color color) {
		GameObject wall = new GameObject(topLeftCorner, dimensions, new RectangleRenderable(color));
		gameObjects().addGameObject(wall);
	}

	/*
	 * Reposition the ball into the middle of the screen at a random direction. */
	private void reposition(Vector2 windowDimensions) {
		Vector2[] directions = {
				new Vector2(-1, -1),
				new Vector2(1, -1),
				new Vector2(-1, 1),
				new Vector2(1, 1)
		};
		Vector2 direction = directions[new Random().nextInt(RANDOM_DIAGONAL_NUMBER)];
		ball.setVelocity(direction.mult(BALL_SPEED));
		ball.renderer().setRenderable(imageReader.readImage("assets/ball.png",
				true));
		ball.setCenter(windowDimensions.mult(0.5f));
	}

	/*
	 * Constructs all brick GameObjects and places them in the world.
	 */
	private void createBricks(ImageReader imageReader, WindowController windowController) {
		Vector2 windowDimensions = windowController.getWindowDimensions();
		float availableWidth = windowDimensions.x() - (2 * WALL_PADDING);
		float brickWidth = (availableWidth - (bricksPerRow - 1) * WALL_PADDING) / bricksPerRow;
		Renderable brickImage = imageReader.readImage("assets/brick.png",
				false);

		for (int row = 0; row < numberOfRows; row++) {
			for (int col = 0; col < bricksPerRow; col++) {
				float x = WALL_PADDING + col * (brickWidth + WALL_PADDING);
				float y = TOP_PADDING + row * (BRICK_HEIGHT + BRICK_VERTICAL_PADDING);
				Vector2 brickPos = new Vector2(x, y);
				Vector2 brickDimensions = new Vector2(brickWidth, BRICK_HEIGHT);
				CollisionStrategy strategy = strategyFactory.buildStrategy();
				GameObject brick = new Brick(brickPos, brickDimensions, brickImage, strategy);
				gameObjects().addGameObject(brick, Layer.STATIC_OBJECTS);
			}
		}
	}

	/*
	 * Creates the main Ball*/
	private void createBall(ImageReader imageReader, SoundReader soundReader,
							Vector2 windowDimensions) {
		Renderable ballImage = imageReader.readImage("assets/ball.png", true);
		Sound soundCollision = soundReader.readSound("assets/blop.wav");
		ball = new Ball(Vector2.ZERO, new Vector2(20, 20), ballImage, soundCollision);
		reposition(windowDimensions);
		gameObjects().addGameObject(ball);
	}

	/*
	 * Creates the main Paddle.*/
	private void createPaddle(ImageReader imageReader,
							  UserInputListener inputListener, Vector2 windowDimensions) {
		Renderable paddleImage = imageReader.readImage("assets/paddle.png",
				true);
		mainPaddle = new Paddle(Vector2.ZERO, new Vector2(100, 15), paddleImage, inputListener);
		mainPaddle.setCenter(new Vector2(windowDimensions.x() / 2, (int) (windowDimensions.y() - 45)));
		gameObjects().addGameObject(mainPaddle);
	}

	/*
	 * Creates Top/Right/Left walls for the game.*/
	private void createWalls(Vector2 windowDimensions) {
		createWall(Vector2.ZERO, new Vector2(5, windowDimensions.y()), Color.black);
		createWall(new Vector2(windowDimensions.x() - 4, 0), new Vector2(5,
				windowDimensions.y()), Color.black);
		createWall(Vector2.ZERO, new Vector2(windowDimensions.x(), 5), Color.black);
	}

	/*
	 * Creates the background for the game.*/
	private void createBackground(ImageReader imageReader, WindowController windowController) {
		Renderable backgroundImage = imageReader.readImage("assets/DARK_BG2_small.jpeg",
				false);
		GameObject background = new GameObject(Vector2.ZERO,
				windowController.getWindowDimensions(), backgroundImage);
		background.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
		gameObjects().addGameObject(background, Layer.BACKGROUND);
	}

}
