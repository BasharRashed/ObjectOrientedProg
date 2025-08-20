package bricker.brick_strategies;

import danogl.GameObject;

/**
 * A collision strategy that applies two other collision strategies in sequence.
 * When a collision occurs, both strategies are triggered in order.
 * Useful for combining effects like spawning power-ups and modifying game state simultaneously.
 */
public class DoubleStrategy implements CollisionStrategy {

	/** The first strategy to apply upon collision. */
	private final CollisionStrategy first;

	/** The second strategy to apply upon collision. */
	private final CollisionStrategy second;

	/**
	 * Constructs a DoubleStrategy with two strategies.
	 *
	 * @param first The first strategy to execute.
	 * @param second The second strategy to execute.
	 */
	public DoubleStrategy(CollisionStrategy first, CollisionStrategy second) {
		this.first = first;
		this.second = second;
	}

	/**
	 * Executes both encapsulated strategies in order when a collision occurs.
	 *
	 * @param thisObject The object that owns this strategy (e.g. a brick).
	 * @param otherObject The object it collided with (e.g. a ball).
	 */
	@Override
	public void onCollision(GameObject thisObject, GameObject otherObject) {
		first.onCollision(thisObject, otherObject);
		second.onCollision(thisObject, otherObject);
	}
}
