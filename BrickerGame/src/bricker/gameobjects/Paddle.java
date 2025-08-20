package bricker.gameobjects;

import bricker.main.BrickerGameManager;
import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.awt.event.KeyEvent;

/**
 * Represents the user-controlled paddle in the Bricker game.
 * The paddle moves left and right in response to keyboard input and is bounded within the screen.
 *
 */
public class Paddle extends GameObject {

	/** Speed at which the paddle moves horizontally, in pixels per second. */
	private static final float MOVEMENT_SPEED = 400f;

	/** Listener for keyboard input events. */
	private final UserInputListener inputListener;

	/**
	 * Constructs a new Paddle instance.
	 *
	 * @param topLeftCorner Position of the paddle's top-left corner.
	 * @param dimensions Width and height of the paddle.
	 * @param renderable Image or graphic to render the paddle.
	 * @param inputListener Listener for keyboard input (e.g., arrow keys).
	 */
	public Paddle(Vector2 topLeftCorner, Vector2 dimensions,
				  Renderable renderable, UserInputListener inputListener) {
		super(topLeftCorner, dimensions, renderable);
		this.inputListener = inputListener;
	}

	/**
	 * Updates the paddle's position each frame based on user input.
	 * Constrains the paddle within the horizontal screen bounds.
	 *
	 * @param deltaTime Time passed since last frame.
	 */
	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);

		Vector2 movementDir = Vector2.ZERO;
		if (inputListener.isKeyPressed(KeyEvent.VK_LEFT)) {
			movementDir = movementDir.add(Vector2.LEFT);
		}
		if (inputListener.isKeyPressed(KeyEvent.VK_RIGHT)) {
			movementDir = movementDir.add(Vector2.RIGHT);
		}
		setVelocity(movementDir.mult(MOVEMENT_SPEED));

		Vector2 topLeft = getTopLeftCorner();

		// Prevent paddle from exiting left boundary
		if (topLeft.x() < 5) {
			setTopLeftCorner(new Vector2(5, topLeft.y()));
			setVelocity(Vector2.ZERO);
		}

		// Prevent paddle from exiting right boundary (700 is screen width)
		float rightBound = BrickerGameManager.SCREEN_WIDTH - getDimensions().x() - 5;
		if (topLeft.x() > rightBound) {
			setTopLeftCorner(new Vector2(rightBound, topLeft.y()));
			setVelocity(Vector2.ZERO);
		}
	}
}
