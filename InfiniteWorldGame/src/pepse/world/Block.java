package pepse.world;

import danogl.GameObject;
import danogl.components.GameObjectPhysics;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

/**
 * Represents a block in the game world.
 * Blocks are immovable objects that can be used to build terrain and other
 * structures.
 */
public class Block extends GameObject {
	/** The size of a block in pixels */
	public static final int SIZE = 30;

	/**
	 * Constructs a new Block.
	 *
	 * @param topLeftCorner The position of the block's top-left corner
	 * @param renderable    The renderable object used to display the block
	 */
	public Block(Vector2 topLeftCorner, Renderable renderable, boolean withPhysics) {
		super(topLeftCorner, Vector2.ONES.mult(SIZE), renderable);
		if (withPhysics) {
			physics().preventIntersectionsFromDirection(Vector2.ZERO);
			physics().setMass(GameObjectPhysics.IMMOVABLE_MASS);
		}
	}
}