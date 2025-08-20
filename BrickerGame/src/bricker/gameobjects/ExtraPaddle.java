package bricker.gameobjects;

import bricker.brick_strategies.ExtraPaddleStrategy;
import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.collisions.GameObjectCollection;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

/**
 * Represents an extra paddle in the Bricker game.
 * This paddle behaves like the main paddle but disappears after a limited number of ball hits.
 * It is spawned as a temporary power-up and is removed from the game after 4 collisions.
 */
public class ExtraPaddle extends Paddle {

	/** Maximum number of ball collisions the extra paddle can sustain before disappearing. */
	private static final int MAX_HITS = 4;

	/** Tracks the number of times this paddle has been hit by a ball. */
	private int hitCount = 0;

	/** Reference to the game object collection, used to remove this paddle from the game. */
	private final GameObjectCollection gameObjects;

	/**
	 * Constructs a new ExtraPaddle instance.
	 *
	 * @param topLeftCorner Position of the paddle's top-left corner.
	 * @param dimensions Size of the paddle.
	 * @param renderable Image or graphic used to render the paddle.
	 * @param inputListener Listener for user input.
	 * @param gameObjects Reference to the game object collection.
	 */
	public ExtraPaddle(Vector2 topLeftCorner, Vector2 dimensions, Renderable renderable,
					   UserInputListener inputListener, GameObjectCollection gameObjects) {
		super(topLeftCorner, dimensions, renderable, inputListener);
		this.gameObjects = gameObjects;
	}

	/**
	 * Handles collision logic. If the extra paddle collides with a ball,
	 * increments the hit count and removes itself if the max hit count is reached.
	 *
	 * @param other The object this paddle collided with.
	 * @param collision Collision data.
	 */
	@Override
	public void onCollisionEnter(GameObject other, Collision collision) {
		super.onCollisionEnter(other, collision);
		if (other instanceof Ball) {
			hitCount++;
			if (hitCount >= MAX_HITS) {
				gameObjects.removeGameObject(this);
				ExtraPaddleStrategy.resetPaddleExists();
			}
		}
	}
}
