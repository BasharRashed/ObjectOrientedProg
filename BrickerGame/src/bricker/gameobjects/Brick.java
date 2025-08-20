package bricker.gameobjects;

import bricker.brick_strategies.CollisionStrategy;
import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

/**
 * Represents a single brick in the Bricker game.
 * When hit, it activates a custom collision strategy which may include effects like spawning power-ups.
 * The behavior of each brick is determined by its assigned CollisionStrategy.
 */
public class Brick extends GameObject {

	/** Strategy to execute upon collision with this brick. */
	private CollisionStrategy strategy;

	/**
	 * Constructs a new Brick object.
	 *
	 * @param topLeftCorner Position of the brick in window coordinates.
	 * @param dimensions Size of the brick (width and height).
	 * @param renderable Image or texture to render for the brick.
	 * @param strategy Collision strategy that defines what happens when the brick is hit.
	 */
	public Brick(Vector2 topLeftCorner, Vector2 dimensions,
				 Renderable renderable, CollisionStrategy strategy) {
		super(topLeftCorner, dimensions, renderable);
		this.strategy = strategy;
	}

	/**
	 * Called when another object collides with this brick.
	 * Delegates the behavior to the assigned collision strategy.
	 *
	 * @param other The object that collided with this brick.
	 * @param collision Details of the collision event.
	 */
	@Override
	public void onCollisionEnter(GameObject other, Collision collision) {
		super.onCollisionEnter(other, collision);
		strategy.onCollision(other, this);
	}
}

