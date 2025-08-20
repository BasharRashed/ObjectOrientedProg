package bricker.gameobjects;

import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.gui.WindowController;
import danogl.gui.rendering.TextRenderable;
import danogl.util.Vector2;
import danogl.util.Counter;

import java.awt.Color;

/**
 * NumericLivesCounter is a UI element that displays the number of remaining lives
 * using a numeric value. It automatically updates its text and color based on the current value.
 *
 * This class is fully encapsulated and manages its own creation and state update.
 */
public class NumericLivesCounter extends GameObject {

	/** Threshold for green color display (3 or more lives). */
	private static final int GREEN_THRESHOLD = 3;

	/** Threshold for yellow color display (2 lives). */
	private static final int YELLOW_THRESHOLD = 2;

	/** Threshold for red color display (1 or fewer lives). */
	private static final int RED_THRESHOLD = 1;

	/** Shared counter tracking the number of lives. */
	private final Counter livesCounter;

	/** Renderable text object that displays the current number of lives. */
	private final TextRenderable textRenderable;

	/**
	 * Private constructor used internally to initialize the numeric counter object.
	 *
	 * @param topLeftCorner The position to place the counter on screen.
	 * @param dimensions Dimensions of the display area.
	 * @param gameObjects Game object collection used to manage objects.
	 * @param livesCounter Counter object tracking lives.
	 */
	private NumericLivesCounter(Vector2 topLeftCorner, Vector2 dimensions,
								GameObjectCollection gameObjects, Counter livesCounter) {
		super(topLeftCorner, dimensions, null);
		this.livesCounter = livesCounter;
		this.textRenderable = new TextRenderable(String.valueOf(livesCounter.value()));
		this.renderer().setRenderable(textRenderable);
		updateColor();
	}

	/**
	 * Creates and adds a numeric lives counter to the game at the bottom-left corner.
	 *
	 * @param gameObjects Game object collection to manage the object lifecycle.
	 * @param livesCounter Shared lives counter.
	 * @param windowController Used to get window dimensions.
	 * @param dimensions Size of the counter.
	 */
	public static void configure(GameObjectCollection gameObjects, Counter livesCounter,
								 WindowController windowController, Vector2 dimensions) {
		float margin = 10f;
		float y = windowController.getWindowDimensions().y() - dimensions.y() - margin - 2f;
		Vector2 position = new Vector2(margin, y);
		NumericLivesCounter counterDisplay = new NumericLivesCounter(position,
				dimensions, gameObjects, livesCounter);
		gameObjects.addGameObject(counterDisplay, Layer.BACKGROUND);
	}

	/**
	 * Updates the displayed number and its color according to current life count.
	 * Automatically called each frame.
	 *
	 * @param deltaTime Time since last frame (not used).
	 */
	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		textRenderable.setString(String.valueOf(livesCounter.value()));
		updateColor();
	}

	/**
	 * Sets the color of the numeric display based on the number of lives.
	 * Green: 3+, Yellow: 2, Red: 1 or fewer.
	 */
	private void updateColor() {
		Color color;
		int lives = livesCounter.value();
		if (lives >= GREEN_THRESHOLD) {
			color = Color.GREEN;
		} else if (lives == YELLOW_THRESHOLD) {
			color = Color.YELLOW;
		} else {
			color = Color.RED;
		}
		textRenderable.setColor(color);
	}
}
