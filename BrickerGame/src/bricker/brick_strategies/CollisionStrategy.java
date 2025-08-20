package bricker.brick_strategies;

import danogl.GameObject;

/**
 * An interface for defining custom collision behaviors in the Bricker game.
 * Each implementing strategy defines what happens when a GameObject (like a brick)
 * is hit by another GameObject (typically a ball).
 * This enables flexible and modular collision effects such as power-ups, visual changes,
 * or compound behaviors.
 *
 */
public interface CollisionStrategy {

	/**
	 * Called when a collision occurs.
	 *
	 * @param other The object that collided with the owner of this strategy (e.g., the ball).
	 * @param collisionedObject The object to which this strategy is attached (e.g., a brick).
	 */
	void onCollision(GameObject other, GameObject collisionedObject);
}
