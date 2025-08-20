package pepse.world.daynight;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;
import pepse.world.Block;

import java.awt.*;

/**
 * Represents the night effect in the game world.
 * Creates a dark overlay that fades in and out to simulate day/night cycle.
 */
public class Night {
    /** The opacity of the night overlay at its darkest */
    private static final float NIGHT_OPACITY = 0.5f;
    /** The color of the night overlay */
    private static final Color NIGHT_COLOR = Color.BLACK;
    /** The start opacity for the night transition */
    private static final float START_OPACITY = 0f;
    /** The duration ratio for the night transition (half of the full cycle) */
    private static final float CYCLE_DURATION_RATIO = 0.5f;
    private static final String NIGHT_TAG = "nightTime";

    /**
     * Creates a night overlay that fades in and out.
     *
     * @param windowDimensions The dimensions of the game window
     * @param cycleLength      The time in seconds for one complete day/night cycle
     * @return A GameObject representing the night overlay
     */
    public static GameObject create(Vector2 windowDimensions, float cycleLength) {
        GameObject night = createNightObject(windowDimensions);
        new Transition<Float>(
                night,
                night.renderer()::setOpaqueness,
                START_OPACITY,
                NIGHT_OPACITY,
                Transition.CUBIC_INTERPOLATOR_FLOAT,
                cycleLength * CYCLE_DURATION_RATIO,
                Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
                null);
        return night;
    }

    /* Creates the night overlay GameObject */
    private static GameObject createNightObject(Vector2 windowDimensions) {
        Renderable nightRenderable = new RectangleRenderable(NIGHT_COLOR);
        GameObject night = new GameObject(Vector2.ZERO, windowDimensions, nightRenderable);
        night.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
        night.setTag(NIGHT_TAG);
        return night;
    }


}