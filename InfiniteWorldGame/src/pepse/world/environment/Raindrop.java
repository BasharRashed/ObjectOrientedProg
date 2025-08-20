package pepse.world.environment;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import danogl.components.Transition;
import java.awt.Color;


/**
 * Represents a raindrop in the game world.
 * Raindrops fall from the sky and fade out over time.
 */
public class Raindrop extends GameObject {
	/** The color of the raindrop */
	private static final Color RAIN_COLOR = Color.BLUE;
	private static final float RAIN_SIZE = 10f;
	private static final float FALL_SPEED = 200f;
	private static final float FADE_TIME = 1f;

	/**
	 * Constructs a new Raindrop.
	 *
	 * @param topLeft The top-left position of the raindrop
	 */
	public Raindrop(Vector2 topLeft) {
		super(topLeft, new Vector2(RAIN_SIZE, RAIN_SIZE),
				new RectangleRenderable(RAIN_COLOR));
		setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
		initializeRaindrop();

	}

	/* Initializes the raindrop's properties and behavior */
	private void initializeRaindrop() {
		setVelocity(new Vector2(0, FALL_SPEED));
		setTag("raindrop");
		setupFadeTransition();

	}

	/* Sets up the fade out transition for the raindrop */
	private void setupFadeTransition() {
		new Transition<Float>(
				this,
				this::setTransparency,
				1f,
				0f,
				Transition.LINEAR_INTERPOLATOR_FLOAT,
				FADE_TIME,
				Transition.TransitionType.TRANSITION_ONCE,
				() -> setTag("toRemove"));
	}

	/* Sets the transparency of the raindrop */
	private void setTransparency(float alpha) {
		renderer().setOpaqueness(alpha);
	}
}