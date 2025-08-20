package bricker.gameobjects;

import bricker.brick_strategies.ExtraHeartStrategy;
import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

/**
 * Represents a falling heart that the paddle can collect during gameplay.
 * When collected, it triggers the associated ExtraHeartStrategy to grant an extra life.
 * This object only collides with the main paddle.
 */
public class FallingHeart extends GameObject {

	/** The paddle that can collect this heart. */
	private final GameObject mainPaddle;

	/** Strategy to execute when the heart is collected. */
	private final ExtraHeartStrategy strategy;

	/**
	 * Constructs a FallingHeart object.
	 *
	 * @param topLeftCorner Position where the heart starts falling.
	 * @param dimensions Size of the heart.
	 * @param renderable Visual image for the heart.
	 * @param mainPaddle The paddle object that can collect the heart.
	 * @param strategy Strategy to apply when the heart is collected.
	 */
	public FallingHeart(Vector2 topLeftCorner,
						Vector2 dimensions,
						Renderable renderable,
						GameObject mainPaddle,
						ExtraHeartStrategy strategy) {
		super(topLeftCorner, dimensions, renderable);
		this.mainPaddle = mainPaddle;
		this.strategy = strategy;
	}

	/**
	 * Restricts collision detection to the paddle only.
	 *
	 * @param other The other game object involved in the collision.
	 * @return true if the other object is the main paddle.
	 */
	@Override
	public boolean shouldCollideWith(GameObject other) {
		return other == mainPaddle;
	}

	/**
	 * Called when the falling heart collides with the paddle.
	 * Triggers the heart collection strategy.
	 *
	 * @param other The other object involved in the collision.
	 * @param collision Details about the collision.
	 */
	@Override
	public void onCollisionEnter(GameObject other, Collision collision) {
		if (other == mainPaddle) {
			strategy.handleHeartCollection(this);
		}
	}
}
