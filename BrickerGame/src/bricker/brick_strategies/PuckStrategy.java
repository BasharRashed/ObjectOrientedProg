package bricker.brick_strategies;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.gui.ImageReader;
import danogl.gui.Sound;
import danogl.gui.SoundReader;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import danogl.util.Counter;
import bricker.gameobjects.Ball;

import java.util.Random;

/**
 * A collision strategy that, upon brick collision with a ball, spawns two additional "puck" balls.
 * These pucks inherit a smaller size, randomized directions, and a fixed speed, creating gameplay variation.
 * Also removes the collided brick and decrements the brick counter if removal is successful.
 */
public class PuckStrategy implements CollisionStrategy {
	/** Constant puck dimensions. */
	private static final Vector2 PUCK_SIZE = new Vector2(15, 15);

	/** Fixed speed at which each puck travels. */
	private static final float PUCK_SPEED = 200;

	/** Collection of game objects in the game world. */
	private final GameObjectCollection gameObjects;

	/** Visual representation for each puck. */
	private final Renderable puckImage;

	/** Sound to play when a puck is spawned. */
	private final Sound puckSound;

	/** Counter tracking number of bricks remaining. */
	private final Counter bricksCounter;

	/**
	 * Constructs a new PuckStrategy.
	 *
	 * @param gameObjects Collection of all game objects.
	 * @param imageReader Used to load puck image.
	 * @param soundReader Used to load puck sound effect.
	 * @param bricksCounter Shared counter of remaining bricks.
	 */
	public PuckStrategy(GameObjectCollection gameObjects, ImageReader imageReader, SoundReader soundReader,
						Counter bricksCounter) {
		this.gameObjects = gameObjects;
		this.puckImage = imageReader.readImage("assets/mockBall.png", true);
		this.puckSound = soundReader.readSound("assets/blop.wav");
		this.bricksCounter = bricksCounter;
	}

	/**
	 * Handles the brick collision. Removes the brick, spawns two pucks with random directions,
	 * and plays a sound effect.
	 *
	 * @param other The ball colliding with the brick.
	 * @param collisionedObject The brick being hit.
	 */
	@Override
	public void onCollision(GameObject other, GameObject collisionedObject) {
		if (other.getTag().equals("Ball") || other.getTag().equals("Puck")) {
			// Only decrement counter if the brick was successfully removed
			if (gameObjects.removeGameObject(collisionedObject, Layer.STATIC_OBJECTS)) {
				bricksCounter.decrement();
			}

			// Spawn two puck balls with randomized velocities
			Random rand = new Random();
			for (int i = 0; i < 2; i++) {
				Ball puck = new Ball(Vector2.ZERO, PUCK_SIZE, puckImage, puckSound);
				puck.setTag("Puck");
				puck.setCenter(collisionedObject.getCenter());

				float angle = rand.nextFloat() * (float)Math.PI;
				float velocityX = (float) Math.cos(angle) * PUCK_SPEED;
				float velocityY = (float) Math.sin(angle) * PUCK_SPEED;
				Vector2 velocity = new Vector2(velocityX, velocityY);
				puck.setVelocity(velocity);

				gameObjects.addGameObject(puck);
			}

			puckSound.play();
		}
	}
}
