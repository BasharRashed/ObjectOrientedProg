package bricker.gameobjects;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.gui.WindowController;
import danogl.gui.rendering.Renderable;
import danogl.util.Counter;
import danogl.util.Vector2;

import java.util.ArrayList;
import java.util.List;

/**
 * VisualHeart is responsible for displaying a visual representation of the player's lives.
 * It manages its own lifecycle and also updates the displayed hearts to match the current lives.
 */
public class VisualHeart extends GameObject {
	/** The amount of space between each heart on screen.*/
	private static final float HEART_SPACING = 5f;
	/** used to define the vertical and horizontal padding from the edge of
	 *  the screen and the number label.
	 */
	private static final float HEART_MARGIN = 10f;
	/** used for the positioning of the first heart, to leave space for the numeric lives counter.*/
	private static final float NUMBER_WIDTH = 20f;

	private static final Vector2 HEART_SIZE = new Vector2(15, 15);

	private final Ball ball;
	private final GameObjectCollection objectCollection;
	private final WindowController windowController;
	private static final List<VisualHeart> activeHearts = new ArrayList<>();
	private static Counter livesCounter;
	private static Renderable heartImage;

	/**
	 * Creates a VisualHeart instance and adds it to the static list.
	 * @param objectCollection The collection to manage visual hearts.
	 * @param ball The ball object used to detect life loss.
	 * @param windowController The window for dimensions.
	 * @param topLeftCorner Initial position.
	 * @param dimensions Size of the heart.
	 * @param renderable Image of the heart.
	 */
	public VisualHeart(GameObjectCollection objectCollection,
					   Ball ball,
					   WindowController windowController,
					   Vector2 topLeftCorner,
					   Vector2 dimensions,
					   Renderable renderable) {
		super(topLeftCorner, dimensions, renderable);
		this.objectCollection = objectCollection;
		this.ball = ball;
		this.windowController = windowController;
	}

	/**
	 * Initializes the static configuration for all visual hearts.
	 */
	public static void configure(GameObjectCollection objectCollection,
								 Counter counter,
								 Renderable image,
								 WindowController windowController,
								 Ball ball) {
		VisualHeart.livesCounter = counter;
		VisualHeart.heartImage = image;
		updateHearts(objectCollection, windowController, ball);
	}

	/**
	 * Updates the visual heart display to reflect the current number of lives.
	 */
	public static void updateHearts(GameObjectCollection objectCollection,
									WindowController windowController,
									Ball ball) {
		for (VisualHeart heart : activeHearts) {
			objectCollection.removeGameObject(heart, Layer.BACKGROUND);
		}
		activeHearts.clear();

		for (int i = 0; i < livesCounter.value(); i++) {
			float x = NUMBER_WIDTH + HEART_MARGIN + i * (HEART_SIZE.x() + HEART_SPACING);
			float y = windowController.getWindowDimensions().y() - HEART_SIZE.y() - HEART_MARGIN;
			VisualHeart heart = new VisualHeart(objectCollection, ball, windowController,
					new Vector2(x, y), HEART_SIZE, heartImage);
			objectCollection.addGameObject(heart, Layer.BACKGROUND);
			activeHearts.add(heart);
		}
	}

	/**
	 * Removes the heart from the game if the ball has fallen below the screen.
	 * @return true if heart was removed, false otherwise.
	 */
	public boolean removeHeart() {
		float ballHeight = ball.getCenter().y();
		float windowHeight = windowController.getWindowDimensions().y();

		if (ballHeight > windowHeight) { // Ball fell below screen
			objectCollection.removeGameObject(this, Layer.BACKGROUND);
			activeHearts.remove(this);
			return true;
		}
		return false;
	}
}
