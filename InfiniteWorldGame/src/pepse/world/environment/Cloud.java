package pepse.world.environment;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.components.Transition;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Represents a cloud in the game world.
 * Clouds move across the screen and can create raindrops.
 */
public class Cloud {
	/** The base color of the cloud */
	private static final Color BASE_CLOUD_COLOR = new Color(255, 255, 255);
	private static final int BLOCK_SIZE = 20;
	private static final int MIN_RAINDROPS = 3;
	private static final int MAX_RAINDROPS = 7;
	private static final Random random = new Random();
	private static List<GameObject> cloudBlocks;
	private static danogl.collisions.GameObjectCollection gameObjects;
	private static List<Transition<Float>> cloudTransitions; // Store transitions for each cloud

	/** The shape of the cloud represented as a 2D grid of 0s and 1s */
	private static final List<List<Integer>> SHAPE = List.of(
			List.of(0, 1, 1, 0, 0, 0),
			List.of(1, 1, 1, 1, 1, 1),
			List.of(1, 1, 1, 1, 1, 1),
			List.of(1, 1, 1, 1, 1, 1),
			List.of(0, 1, 1, 1, 0, 0),
			List.of(0, 0, 0, 0, 0, 0));

	/**
	 * Creates a moving cloud that animates in a straight horizontal line.
	 *
	 * @param windowDimensions The dimensions of the game window
	 * @param cycleLength      How long (in seconds) for the cloud to cross the
	 *                         screen
	 * @param gameObjects      The game's object collection
	 * @return List of GameObjects representing the cloud blocks
	 */
	public static List<GameObject> create(Vector2 windowDimensions, float cycleLength,
										  danogl.collisions.GameObjectCollection gameObjects) {
		Cloud.gameObjects = gameObjects;
		cloudBlocks = new ArrayList<>();
		cloudTransitions = new ArrayList<>();
		List<Vector2> blockOffsets = new ArrayList<>();

		float minX = -SHAPE.get(0).size() * BLOCK_SIZE;
		float initialY = windowDimensions.y() * 0.15f;

		// Create cloud blocks
		for (int row = 0; row < SHAPE.size(); row++) {
			for (int col = 0; col < SHAPE.get(row).size(); col++) {
				if (SHAPE.get(row).get(col) == 1) {
					createCloudBlock(row, col, minX, initialY, cloudBlocks, blockOffsets);
				}
			}
		}

		// Set up cloud movement
		animateCloud(cloudBlocks, blockOffsets, windowDimensions, cycleLength);

		return cloudBlocks;
	}

	/**
	 * Creates a rain effect when the avatar jumps.
	 */
	public static void createRain() {
		if (cloudBlocks != null && !cloudBlocks.isEmpty()) {
			int numDrops = generateRandomDropCount();
			createRaindrops(cloudBlocks.get(0).getCenter(), numDrops);
		}
	}

	/* Initializes the cloud blocks and their positions */
	private static void initializeCloudBlocks(Vector2 windowDimensions,
											  List<GameObject> cloudBlocks, List<Vector2> blockOffsets) {
		int cloudWidth = SHAPE.get(0).size() * BLOCK_SIZE;
		float initialY = windowDimensions.y() * 0.15f;
		float minX = -cloudWidth;

		for (int row = 0; row < SHAPE.size(); row++) {
			List<Integer> cols = SHAPE.get(row);
			for (int col = 0; col < cols.size(); col++) {
				if (cols.get(col) == 1) {
					createCloudBlock(row, col, minX, initialY, cloudBlocks, blockOffsets);
				}
			}
		}
	}

	/* Creates a single cloud block at the specified position */
	private static void createCloudBlock(int row, int col, float minX, float initialY,
										 List<GameObject> cloudBlocks, List<Vector2> blockOffsets) {
		Vector2 offset = new Vector2(col * BLOCK_SIZE, row * BLOCK_SIZE);
		blockOffsets.add(offset);

		GameObject block = new GameObject(
				new Vector2(minX + offset.x(), initialY + offset.y()),
				new Vector2(BLOCK_SIZE, BLOCK_SIZE),
				new RectangleRenderable(ColorSupplier.approximateMonoColor(BASE_CLOUD_COLOR)));
		block.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
		block.setTag("cloud");
		// Keep vertical position fixed
		block.addComponent(deltaTime -> {
			Vector2 currentPos = block.getCenter();
			block.setCenter(new Vector2(currentPos.x(), initialY + offset.y()));
		});
		cloudBlocks.add(block);
	}

	/* Sets up the cloud's movement animation */
	private static void animateCloud(List<GameObject> cloudBlocks, List<Vector2> blockOffsets,
									 Vector2 windowDimensions, float cycleLength) {
		if (cloudBlocks.isEmpty()) {
			return;
		}

		float minX = -SHAPE.get(0).size() * BLOCK_SIZE;
		float maxX = windowDimensions.x();
		float initialY = windowDimensions.y() * 0.15f;

		// Create transition for each cloud block
		for (int i = 0; i < cloudBlocks.size(); i++) {
			final int index = i;
			Transition<Float> transition = new Transition<>(
					cloudBlocks.get(index),
					(Float baseX) -> updateCloudPosition(baseX, cloudBlocks, blockOffsets, initialY, index),
					minX,
					maxX,
					Transition.LINEAR_INTERPOLATOR_FLOAT,
					cycleLength,
					Transition.TransitionType.TRANSITION_LOOP,
					null);
			cloudTransitions.add(transition);
		}
	}

	/* Updates the position of a specific cloud block */
	private static void updateCloudPosition(float baseX, List<GameObject> cloudBlocks,
											List<Vector2> blockOffsets, float initialY, int index) {
		GameObject block = cloudBlocks.get(index);
		Vector2 offset = blockOffsets.get(index);
		block.setTopLeftCorner(new Vector2(baseX + offset.x(), initialY + offset.y()));
	}

	/* Generates a random number of raindrops to create */
	private static int generateRandomDropCount() {
		return random.nextInt(MAX_RAINDROPS - MIN_RAINDROPS + 1) + MIN_RAINDROPS;
	}

	/* Creates raindrops at random positions within the cloud */
	private static void createRaindrops(Vector2 cloudPos, int numDrops) {
		for (int i = 0; i < numDrops; i++) {
			float xOffset = random.nextFloat() * ((SHAPE.get(0).size() - 2) * BLOCK_SIZE);
			float yOffset = (SHAPE.size() * (BLOCK_SIZE / 2f)) + random.nextFloat() * BLOCK_SIZE;
			Vector2 dropPos = new Vector2(cloudPos.x() + xOffset, cloudPos.y() + yOffset);

			Raindrop drop = new Raindrop(dropPos);
			drop.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
			gameObjects.addGameObject(drop, danogl.collisions.Layer.BACKGROUND + 1);
		}
	}
	public static void cleanUpRaindrops() {
		for (GameObject obj : gameObjects) {
			if ("toRemove".equals(obj.getTag())) {
				gameObjects.removeGameObject(obj);
				System.out.println("removed");
			}
		}
	}
}