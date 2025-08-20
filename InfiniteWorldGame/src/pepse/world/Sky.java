package pepse.world;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;

import java.awt.*;

/**
 * Represents the sky in the game world.
 * Handles the creation and configuration of the sky background.
 */
public class Sky {
	private static final Color BASIC_SKY_COLOR = Color.decode("#80C6E5");
	private static final String SKY_TAG = "sky";

	/**
	 * Creates a new sky GameObject that spans the entire window.
	 * The sky is set to follow the camera coordinates.
	 *
	 * @param windowDimensions The dimensions of the game window
	 * @return A GameObject representing the sky
	 */
	public static GameObject create(Vector2 windowDimensions) {
		GameObject sky = new GameObject(
				Vector2.ZERO,
				windowDimensions,
				new RectangleRenderable(BASIC_SKY_COLOR));
		configureSkyObject(sky);
		return sky;
	}

	/* Configures the sky GameObject with camera coordinates and tag */
	private static void configureSkyObject(GameObject sky) {
		sky.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
		sky.setTag(SKY_TAG);
	}
}