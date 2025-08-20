package pepse.world.daynight;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.WindowController;
import danogl.gui.rendering.Camera;
import danogl.gui.rendering.OvalRenderable;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.awt.*;

/**
 * Represents the sun in the game world.
 * The sun moves in a circular path across the sky and includes a halo effect.
 */
public class Sun {
	/** The color of the sun */
	private static final Color SUN_COLOR = Color.YELLOW;
	/** The size of the sun in pixels */
	private static final float SUN_SIZE = 100f;
	/** The radius of the sun's circular path */
	private static final float SUN_RADIUS = 300f;
	/** The color of the sun's halo with alpha transparency */
	private static final Color HALO_COLOR = new Color(255, 255, 0, 20);
	/** The size of the halo in pixels */
	private static final float HALO_SIZE = 200f;
	/** The vertical position of the sun relative to window height */
	private static final float SUN_HEIGHT_RATIO = 0.6f;
	/** The start angle for the sun's movement */
	private static final float START_ANGLE = 0f;
	/** The end angle for the sun's movement (full circle) */
	private static final float END_ANGLE = 360f;

	/**
	 * Creates a sun that moves in a circular path across the sky.
	 *
	 * @param windowDimensions The dimensions of the game window
	 * @param cycleLength      The time in seconds for one complete cycle
	 * @return A GameObject representing the sun
	 */
	public static GameObject create(Vector2 windowDimensions, float cycleLength) {
		Vector2 centerPoint = new Vector2(
				windowDimensions.x() / 2,
				windowDimensions.y() * SUN_HEIGHT_RATIO);
		GameObject sun = createSunObject(centerPoint);
		GameObject halo = createHaloObject(centerPoint, sun);
		setupSunMovement(sun, centerPoint, cycleLength);
		return sun;
	}



	/* Creates the sun GameObject with the specified properties */
	private static GameObject createSunObject(Vector2 centerPoint) {
		Renderable sunRenderable = new OvalRenderable(SUN_COLOR);
		GameObject sun = new GameObject(
				centerPoint,
				new Vector2(SUN_SIZE, SUN_SIZE),
				sunRenderable);
		sun.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);

		sun.addComponent(deltaTime -> {
			float fixedY = centerPoint.y();
			Vector2 currentPos = sun.getCenter();
			sun.setCenter(new Vector2(currentPos.x(), fixedY));
			float currentAngle = sun.renderer().getRenderableAngle();
			sun.renderer().setRenderableAngle(currentAngle + 30f * deltaTime);
		});

		return sun;
	}

	/* Creates the halo GameObject that follows the sun */
	private static GameObject createHaloObject(Vector2 centerPoint, GameObject sun) {
		Renderable haloRenderable = new OvalRenderable(HALO_COLOR);
		GameObject halo = new GameObject(
				centerPoint,
				new Vector2(HALO_SIZE, HALO_SIZE),
				haloRenderable);
		halo.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
		halo.setTag("sunHalo");
		halo.addComponent(deltaTime -> {
			Vector2 currentPos = halo.getCenter();
			halo.setCenter(new Vector2(currentPos.x(), centerPoint.y()));
		});
		halo.addComponent(deltaTime -> halo.setCenter(sun.getCenter()));
		return halo;
	}

	/* Sets up the sun's circular movement */
	private static void setupSunMovement(GameObject sun, Vector2 centerPoint, float cycleLength) {
		Vector2 initialSunCenter = new Vector2(
				centerPoint.x(),
				centerPoint.y() - SUN_RADIUS);
		Vector2 cycleCenter = centerPoint;

		new Transition<Float>(
				sun,
				(Float angle) -> sun.setCenter
						(initialSunCenter.subtract(cycleCenter).rotated(angle).add(cycleCenter)),
				START_ANGLE,
				END_ANGLE,
				Transition.LINEAR_INTERPOLATOR_FLOAT,
				cycleLength,
				Transition.TransitionType.TRANSITION_LOOP,
				null);
	}

}