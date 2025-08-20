package pepse.world.daynight;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.WindowController;
import danogl.gui.rendering.OvalRenderable;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.awt.*;

/**
 * Represents a halo effect around the sun in the game world.
 * The halo follows the sun's movement and provides a visual enhancement.
 */
public class SunHalo {
	/** The color of the halo with alpha transparency */
	private static final Color HALO_COLOR = new Color(255, 255, 0, 20);
	/** The size of the halo in pixels */
	private static final float HALO_SIZE = 200f;

	/**
	 * Creates a halo effect that follows the sun.
	 *
	 * @param sun The sun GameObject that the halo will follow
	 * @return A GameObject representing the sun's halo
	 */
	public static GameObject create(GameObject sun) {
		GameObject halo = createHaloObject(sun);
		configureHaloProperties(halo);
		setupSunFollowing(halo, sun);
		return halo;
	}

	/* Creates the halo GameObject with the specified properties */
	private static GameObject createHaloObject(GameObject sun) {
		Renderable haloRenderable = new OvalRenderable(HALO_COLOR);
		return new GameObject(
				sun.getCenter(),
				new Vector2(HALO_SIZE, HALO_SIZE),
				haloRenderable);
	}

	/* Configures the halo's coordinate space and tag */
	private static void configureHaloProperties(GameObject halo) {
		halo.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
		halo.setTag("sunHalo");
	}

	/* Sets up the halo to follow the sun's position */
	private static void setupSunFollowing(GameObject halo, GameObject sun) {
		halo.addComponent(deltaTime -> {
			Vector2 sunCenter = sun.getCenter();
			halo.setCenter(sunCenter);
		});
	}
}