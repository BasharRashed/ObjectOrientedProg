package pepse.world.trees;

import danogl.GameObject;
import danogl.components.ScheduledTask;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;

import java.awt.*;
import java.util.Random;

/**
 * Represents a leaf in the game world.
 * Leaves are animated objects that sway back and forth with a slight delay.
 */
public class Leaf extends GameObject {
	/** The base color of the leaf */
	public static final Color LEAF_COLOR = new Color(50, 200, 30);

	private static final float MIN_DELAY = 0.2f;
	private static final float MAX_DELAY = 0.7f;
	private static final float MIN_ANGLE = -15f;
	private static final float MAX_ANGLE = 15f;
	private static final float TRANSITION_DURATION = 1.2f;

	/**
	 * Constructs a new Leaf.
	 *
	 * @param topLeftCorner The position of the leaf's top-left corner
	 * @param size          The size of the leaf
	 */
	public Leaf(Vector2 topLeftCorner, int size) {
		super(topLeftCorner, new Vector2(size, size),
				new RectangleRenderable(ColorSupplier.approximateColor(LEAF_COLOR)));
		initializeLeafAnimation();
	}

	/* Initializes the leaf's swaying animation with a random delay */
	private void initializeLeafAnimation() {
		Random rand = new Random();
		float delay = MIN_DELAY + rand.nextFloat() * (MAX_DELAY - MIN_DELAY);
		createSwayingAnimation(delay);
	}

	/* Creates the swaying animation transition for the leaf */
	private void createSwayingAnimation(float delay) {
		new ScheduledTask(
				this,
				delay,
				false,
				() -> new Transition<Float>(
						this,
						angle -> this.renderer().setRenderableAngle(angle),
						MIN_ANGLE,
						MAX_ANGLE,
						Transition.LINEAR_INTERPOLATOR_FLOAT,
						TRANSITION_DURATION,
						Transition.TransitionType.TRANSITION_BACK_AND_FORTH,
						null));
	}
}