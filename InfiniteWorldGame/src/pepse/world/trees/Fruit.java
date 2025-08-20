package pepse.world.trees;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.components.ScheduledTask;
import danogl.gui.rendering.OvalRenderable;
import danogl.util.Vector2;
import pepse.world.Avatar;

import java.awt.*;

/**
 * Represents a fruit in the game world.
 * Fruits can be eaten by the avatar to gain energy and will respawn after a
 * delay.
 */
public class Fruit extends GameObject {
	/** The color of the fruit */
	public static final Color FRUIT_COLOR = Color.RED;
	private static final int SIZE = 30;
	private static final float ENERGY_GAIN = 10f;
	private static final float RESPAWN_TIME = 30f; // One cycle in seconds
	private  Vector2 originalPosition;
	private boolean isEaten = false;

	/**
	 * Constructs a new Fruit.
	 *
	 * @param center The center position of the fruit
	 */
	public Fruit(Vector2 center) {
		super(center, new Vector2(SIZE, SIZE), new OvalRenderable(FRUIT_COLOR));
		initializeFruit(center);
	}

	/**
	 * Handles collision with other game objects.
	 * When colliding with the avatar, the fruit is eaten and provides energy.
	 *
	 * @param other     The other game object involved in the collision
	 * @param collision The collision information
	 */
	@Override
	public void onCollisionEnter(GameObject other, Collision collision) {
		super.onCollisionEnter(other, collision);
		if (isCollisionWithAvatar(other) && !isEaten) {
			handleFruitEaten((Avatar) other);
		}
	}

	/**
	 * Updates the fruit's state.
	 * Ensures the fruit stays at its original position when not eaten.
	 *
	 * @param deltaTime The time elapsed since the last update
	 */
	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		if (!isEaten) {
			this.setCenter(originalPosition);
		}
	}

	/* Initializes the fruit's properties and physics */
	private void initializeFruit(Vector2 center) {
		this.setTag("fruit");
		this.originalPosition = center;
		configurePhysics();
	}

	/* Configures the fruit's physics properties */
	private void configurePhysics() {
		this.physics().setMass(0);
		this.physics().preventIntersectionsFromDirection(Vector2.ZERO);
	}

	/* Checks if the collision is with the avatar */
	private boolean isCollisionWithAvatar(GameObject other) {
		return other.getTag().equals("avatar");
	}

	/* Handles the fruit being eaten by the avatar */
	private void handleFruitEaten(Avatar avatar) {
		avatar.addEnergy(ENERGY_GAIN);
		hideFruit();
		scheduleRespawn();
	}

	/* Hides the fruit and marks it as eaten */
	private void hideFruit() {
		this.renderer().setOpaqueness(0);
		isEaten = true;
	}

	/* Schedules the fruit to respawn after the respawn delay */
	private void scheduleRespawn() {
		new ScheduledTask(
				this,
				RESPAWN_TIME,
				false,
				this::respawnFruit);
	}

	/* Respawns the fruit by making it visible again */
	private void respawnFruit() {
		this.renderer().setOpaqueness(1);
		isEaten = false;
	}
}