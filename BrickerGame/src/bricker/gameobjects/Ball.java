package bricker.gameobjects;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.gui.Sound;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

/**
 * Represents the ball object in the Bricker game.
 * Handles collision response and supports a temporary "turbo mode"
 * that increases speed and changes the ball's appearance for a few collisions.
 *
 * Turbo mode is triggered externally and resets automatically after a number of bounces.
 */
public class Ball extends GameObject {

	/** Speed multiplier applied when turbo mode is activated. */
	private static final float TURBO_SPEED_MULTIPLIER = 1.4f;

	/** Number of collisions after which turbo mode deactivates. */
	private static final int COLLISIONS_BEFORE_RESET = 6;

	/** Total number of collisions this ball has encountered. */
	private int collisionCounter;

	/** Sound to play on collision. */
	private Sound collisionSound;

	/** Whether turbo mode is currently active. */
	private boolean isTurboActive = false;

	/** The value of collisionCounter at the moment turbo mode was activated. */
	private int collisionCountAtActivation = 0;

	/** Image of the ball in normal mode. */
	private Renderable normalBallImage;

	/** Image of the ball in turbo mode. */
	private Renderable turboBallImage;

	/**
	 * Constructs a new Ball instance.
	 *
	 * @param topLeftCorner Initial position of the ball.
	 * @param dimensions Size of the ball.
	 * @param renderable Image to render for the ball in normal state.
	 * @param collisionSound Sound to play on collision.
	 */
	public Ball(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable, Sound collisionSound) {
		super(topLeftCorner, dimensions, renderable);
		this.collisionSound = collisionSound;
		this.normalBallImage = renderable;
		this.setTag("Ball");
	}

	/**
	 * Sets the image used for rendering the ball in turbo mode.
	 *
	 * @param turboBallImage Renderable image for turbo ball.
	 */
	public void setTurboBallImage(Renderable turboBallImage) {
		this.turboBallImage = turboBallImage;
	}

	/**
	 * Activates turbo mode: speeds up the ball and changes its image.
	 */
	public void activateTurbo() {
		if (!isTurboActive) {
			Vector2 currentVelocity = getVelocity();
			setVelocity(currentVelocity.mult(TURBO_SPEED_MULTIPLIER));
			renderer().setRenderable(turboBallImage);
			isTurboActive = true;
			collisionCountAtActivation = collisionCounter;
		}
	}

	/**
	 * Handles the ball's response to collisions. Reflects velocity and tracks collision count.
	 * Deactivates turbo mode after a set number of post-activation collisions.
	 *
	 * @param other The object the ball collided with.
	 * @param collision Collision details.
	 */
	@Override
	public void onCollisionEnter(GameObject other, Collision collision) {
		super.onCollisionEnter(other, collision);
		Vector2 newVelocity = getVelocity().flipped(collision.getNormal());
		setVelocity(newVelocity);
		collisionCounter++;
		collisionSound.play();

		// Reset turbo mode if enough collisions have passed
		if (isTurboActive && (collisionCounter - collisionCountAtActivation > COLLISIONS_BEFORE_RESET)) {
			Vector2 currentVelocity = getVelocity();
			setVelocity(currentVelocity.mult(1.0f / TURBO_SPEED_MULTIPLIER));
			renderer().setRenderable(normalBallImage);
			isTurboActive = false;
		}
	}

	/**
	 * Returns the number of collisions this ball has encountered.
	 *
	 * @return Collision count.
	 */
	public int getCollisionCounter() {
		return collisionCounter;
	}
}
