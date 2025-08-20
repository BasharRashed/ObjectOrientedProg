package bricker.brick_strategies;

import bricker.gameobjects.Ball;
import bricker.gameobjects.ExtraPaddle;
import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.gui.ImageReader;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.Renderable;
import danogl.util.Counter;
import danogl.util.Vector2;

/**
 * A collision strategy that adds a second paddle (extra paddle) when triggered.
 * The extra paddle appears once and is removed after a limited number of hits.
 * Ensures only one extra paddle is present on screen at any time.
 * Also removes the brick and decrements the global brick counter.
 *
 * @author
 */
public class ExtraPaddleStrategy implements CollisionStrategy {

	/** Dimensions of the extra paddle. */
	private static final Vector2 PADDLE_DIMENSIONS = new Vector2(100, 15);

	/** Tracks whether an extra paddle currently exists in the game. */
	private static boolean paddleExists = false;

	/** Reference to the game's object collection. */
	private final GameObjectCollection gameObjects;

	/** Used to load paddle image. */
	private final ImageReader imageReader;

	/** Input listener for the extra paddle. */
	private final UserInputListener inputListener;

	/** Counter for tracking number of bricks remaining. */
	private final Counter bricksCounter;

	/** The instance of the extra paddle. */
	private ExtraPaddle extraPaddle;

	/** Tracks whether the extra paddle was added to the game. */
	private boolean paddleAdded;

	/**
	 * Constructs a new ExtraPaddleStrategy.
	 *
	 * @param gameObjects Collection of all game objects.
	 * @param imageReader Loads paddle image.
	 * @param inputListener Input handler for user controls.
	 * @param bricksCounter Counter for bricks in the game.
	 */
	public ExtraPaddleStrategy(GameObjectCollection gameObjects,
							   ImageReader imageReader,
							   UserInputListener inputListener,
							   Counter bricksCounter) {
		this.gameObjects = gameObjects;
		this.imageReader = imageReader;
		this.inputListener = inputListener;
		this.bricksCounter = bricksCounter;

		Renderable paddleImage = imageReader.readImage("assets/paddle.png",
				true);
		this.extraPaddle = new ExtraPaddle(Vector2.ZERO, PADDLE_DIMENSIONS,
				paddleImage, inputListener, gameObjects);
		this.paddleAdded = false;
	}

	/**
	 * Handles brick collision by removing the brick and spawning an extra paddle if one isn't active.
	 *
	 * @param other The object that triggered the collision (usually the ball).
	 * @param collisionedObject The brick that was hit.
	 */
	@Override
	public void onCollision(GameObject other, GameObject collisionedObject) {
		if (other.getTag().equals("Ball") || other.getTag().equals("Puck")) {
			if (gameObjects.removeGameObject(collisionedObject, Layer.STATIC_OBJECTS)) {
				bricksCounter.decrement();
			}

			if (!paddleAdded && !paddleExists) {
				extraPaddle.setCenter(new Vector2(350, 250)); // Centered for 700x500 window
				gameObjects.addGameObject(extraPaddle);
				paddleAdded = true;
				paddleExists = true;
			}
		}
	}

	/**
	 * Resets the static paddleExists flag, allowing another extra paddle to be added.
	 */
	public static void resetPaddleExists() {
		paddleExists = false;
	}
}
