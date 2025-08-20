package bricker.brick_strategies;

import bricker.gameobjects.Ball;
import bricker.gameobjects.Paddle;
import danogl.collisions.GameObjectCollection;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
import danogl.util.Counter;

import java.util.ArrayList;
import java.util.Random;

/**
 * A factory class for generating random collision strategies in the Bricker game.
 * The factory encapsulates logic for creating both single and composite (DoubleStrategy) behaviors.
 * It ensures a mix of basic and special strategies with configurable depth to avoid infinite recursion.
 *
 * This helps in producing varied brick behaviors upon collision, enhancing gameplay dynamics.
 *
 * @author
 */
public class StrategyFactory {

	/** Collection of game objects to operate on. */
	private final GameObjectCollection gameObjects;

	/** Used to load images for power-ups and effects. */
	private final ImageReader imageReader;

	/** Used to load sound effects. */
	private final SoundReader soundReader;

	/** Handles window interactions such as screen size. */
	private final WindowController windowController;

	/** Global counter of remaining bricks. */
	private final Counter bricksCounter;

	/** Reference to the main game ball. */
	private final Ball ball;

	/** Handles user input (keyboard). */
	private final UserInputListener inputListener;

	/** Random generator for selecting strategies. */
	private final Random random;

	/** Reference to the main paddle for paddle-related strategies. */
	private final Paddle mainPaddle;

	/** Lives counter for heart-based strategies. */
	private final Counter livesCounter;

	/**
	 * Constructs a StrategyFactory.
	 *
	 * @param gameObjects Collection of all game objects.
	 * @param imageReader Image loader.
	 * @param soundReader Sound loader.
	 * @param windowController Window information and controls.
	 * @param bricksCounter Tracks remaining bricks.
	 * @param ball The game's main ball.
	 * @param inputListener Listens for user input.
	 * @param mainPaddle Reference to the main paddle.
	 * @param livesCounter Tracks remaining lives.
	 */
	public StrategyFactory(GameObjectCollection gameObjects,
						   ImageReader imageReader,
						   SoundReader soundReader,
						   WindowController windowController,
						   Counter bricksCounter,
						   Ball ball,
						   UserInputListener inputListener,
						   Paddle mainPaddle,
						   Counter livesCounter) {
		this.gameObjects = gameObjects;
		this.imageReader = imageReader;
		this.soundReader = soundReader;
		this.windowController = windowController;
		this.bricksCounter = bricksCounter;
		this.ball = ball;
		this.inputListener = inputListener;
		this.random = new Random();
		this.mainPaddle = mainPaddle;
		this.livesCounter = livesCounter;
	}

	/**
	 * Public interface to get a new collision strategy.
	 * Always starts with depth 1.
	 *
	 * @return A randomly chosen CollisionStrategy.
	 */
	public CollisionStrategy buildStrategy() {
		return createRandomStrategy(1);
	}

	/*
	 * Creates a random strategy that will be used in a brick.
	 * supports double/triple strategies per brick.
	 * Ensures a mix of basic, special, and double strategies.
	 */
	private CollisionStrategy createRandomStrategy(int depth) {
		if (depth >= 3) {
			return new BasicCollisionStrategy(gameObjects, bricksCounter);
		}

		int roll = random.nextInt(10); // 0-9

		switch (roll) {
			case 0,1,2,3,4:  // 50% chance for basic strategy
				return new BasicCollisionStrategy(gameObjects, bricksCounter);
			case 5:
				return new PuckStrategy(gameObjects, imageReader, soundReader, bricksCounter);
			case 6:
				return new ExtraPaddleStrategy(gameObjects, imageReader, inputListener, bricksCounter);
			case 7:
				return new ExtraHeartStrategy(gameObjects, imageReader, windowController, bricksCounter,
						mainPaddle, livesCounter, new ArrayList<>(), ball);
			case 8:
				return new TurboStrategy(gameObjects, imageReader, bricksCounter, ball);
			case 9:
				CollisionStrategy a = createNonBasicStrategy(depth + 1);
				CollisionStrategy b = createNonBasicStrategy(depth + 1);

				// 50% chance to use two strategies, 50% to use three
				if (random.nextBoolean()) {
					// Two strategies only
					return new DoubleStrategy(a, b);
				} else {
					CollisionStrategy c = createNonBasicStrategy(depth + 1);
					// Nesting DoubleStrategy to allow 3 strategies total
					return new DoubleStrategy(a, new DoubleStrategy(b, c));
				}
			default:
				return new BasicCollisionStrategy(gameObjects, bricksCounter);
		}
	}

	/*
	 * Builds a non-basic strategy (used in DoubleStrategy creation).
	 */
	private CollisionStrategy createNonBasicStrategy(int depth) {
		if (depth >= 3) {
			return new BasicCollisionStrategy(gameObjects, bricksCounter);
		}

		int roll = random.nextInt(5); // 0-4 among non-basic types

		switch (roll) {
			case 0:
				return new PuckStrategy(gameObjects, imageReader, soundReader, bricksCounter);
			case 1:
				return new ExtraPaddleStrategy(gameObjects, imageReader, inputListener, bricksCounter);
			case 2:
				return new ExtraHeartStrategy(gameObjects, imageReader, windowController, bricksCounter,
						mainPaddle, livesCounter, new ArrayList<>(), ball);
			case 3:
				return new TurboStrategy(gameObjects, imageReader, bricksCounter, ball);
			default: // it shouldn't reach here, but for safety, it will just return a normal brick.
				return new BasicCollisionStrategy(gameObjects, bricksCounter);
		}
	}
}