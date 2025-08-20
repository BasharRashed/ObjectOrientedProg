package bricker.brick_strategies;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.util.Counter;

/**
 * A basic collision strategy that removes the collided object (e.g. a brick)
 * and decrements the shared brick counter. This is the default behavior when
 * no special effect is needed.
 * Typically used to represent standard brick-breaking logic.
 *
 */
public class BasicCollisionStrategy implements CollisionStrategy {

	/** Collection of game objects, used to remove the brick. */
	private final GameObjectCollection objectCollection;

	/** Counter tracking remaining bricks in the game. */
	private final Counter bricksCounter;

	/**
	 * Constructs a basic collision strategy.
	 *
	 * @param objectCollection Collection containing the brick.
	 * @param bricksCounter Shared counter to track brick removal.
	 */
	public BasicCollisionStrategy(GameObjectCollection objectCollection, Counter bricksCounter) {
		this.objectCollection = objectCollection;
		this.bricksCounter = bricksCounter;
	}

	/**
	 * Removes the brick from the game and decrements the brick counter if successful.
	 *
	 * @param thisObject The object the strategy is applied to (e.g., a brick).
	 * @param otherObject The object that collided with the brick (typically a ball).
	 */
	@Override
	public void onCollision(GameObject thisObject, GameObject otherObject) {
		if (objectCollection.removeGameObject(otherObject, Layer.STATIC_OBJECTS)) {
			bricksCounter.decrement();
		}
	}
}
