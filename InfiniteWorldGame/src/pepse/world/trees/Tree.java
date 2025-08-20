package pepse.world.trees;

import danogl.GameObject;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;
import pepse.world.Block;

import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Represents a tree in the game world.
 * Handles the creation of tree components including trunk, leaves, and fruits.
 */
public class Tree extends GameObject {
	/** Size of each block in the tree */
	public static final int BLOCK_SIZE = 30;
	private static final Color TRUNK_COLOR = new Color(100, 50, 20);
	private static final int MIN_TRUNK_HEIGHT = 4;
	private static final int MAX_TRUNK_HEIGHT = 9;

	private static final int CANOPY_WIDTH_BLOCKS = 7;
	private static final int CANOPY_HEIGHT_BLOCKS = 6;
	private static final int MIN_LEAVES = 22;
	private static final int MAX_LEAVES = 32;

	private final List<GameObject> parts;
	private final Random rand;
	private final Vector2 position;
	private final int seed;

	/**
	 * Creates a new tree at the specified position.
	 *
	 * @param position The position of the tree's base
	 * @param seed     The seed for random number generation
	 */
	public Tree(Vector2 position, int seed) {
		super(position, new Vector2(BLOCK_SIZE, BLOCK_SIZE),
				new RectangleRenderable(ColorSupplier.approximateColor(TRUNK_COLOR)));
		this.position = position;
		this.seed = seed;
		this.rand = new Random(seed);
		this.parts = new ArrayList<>();
		initializeTree();
	}

	/**
	 * Initializes the tree by creating its trunk, leaves, and fruits.
	 */
	private void initializeTree() {
		int trunkHeight = generateTrunkHeight();
		createTrunk(trunkHeight);
		float trunkTopY = position.y() - trunkHeight * BLOCK_SIZE;
		createCanopy(trunkTopY);
	}

	/**
	 * Gets all parts of the tree.
	 * @return List of game objects representing the tree's parts
	 */
	public List<GameObject> getParts() {
		return parts;
	}

	/* Generates a random trunk height within the defined range */
	private int generateTrunkHeight() {
		return rand.nextInt(
				MAX_TRUNK_HEIGHT - MIN_TRUNK_HEIGHT + 1) + MIN_TRUNK_HEIGHT;
	}

	/* Creates the tree trunk as a vertical stack of blocks */
	private void createTrunk(int trunkHeight) {
		for (int i = 0; i < trunkHeight; i++) {
			Vector2 blockPos = new Vector2(position.x(),
					position.y() - (i + 1) * BLOCK_SIZE);
			GameObject trunkBlock = new Block(blockPos,
					new RectangleRenderable(ColorSupplier
							.approximateColor(TRUNK_COLOR)),true);
			trunkBlock.setTag("treeTrunk");
			parts.add(trunkBlock);
		}
	}

	/* Creates the tree canopy including leaves and fruits */
	private void createCanopy(float trunkTopY) {
		float canopyTopY = trunkTopY - (CANOPY_HEIGHT_BLOCKS / 2f) * BLOCK_SIZE;
		float canopyStartX = position.x() - (CANOPY_WIDTH_BLOCKS / 2f) * BLOCK_SIZE;

		Set<Point> leafPositions = generateLeafPositions();
		createLeaves(leafPositions, canopyStartX, canopyTopY);
		createFruits(canopyStartX, canopyTopY);
	}

	/* Generates random positions for leaves within the canopy */
	private Set<Point> generateLeafPositions() {
		Set<Point> leafPositions = new HashSet<>();
		int leavesToPlace = rand.nextInt(MAX_LEAVES - MIN_LEAVES + 1) + MIN_LEAVES;

		while (leafPositions.size() < leavesToPlace) {
			int lx = rand.nextInt(CANOPY_WIDTH_BLOCKS);
			int ly = rand.nextInt(CANOPY_HEIGHT_BLOCKS);
			leafPositions.add(new Point(lx, ly));
		}
		return leafPositions;
	}

	/* Creates leaf objects at the specified positions */
	private void createLeaves(Set<Point> leafPositions,
							  float canopyStartX, float canopyTopY) {
		for (Point p : leafPositions) {
			Vector2 leafPos = new Vector2(
					canopyStartX + p.x * BLOCK_SIZE,
					canopyTopY + p.y * BLOCK_SIZE);
			Leaf leaf = new Leaf(leafPos, BLOCK_SIZE);
			leaf.setTag("treeLeaf");
			parts.add(leaf);
		}
	}

	/* Creates fruit objects in the canopy */
	private void createFruits(float canopyStartX, float canopyTopY) {
		int numFruits = 1 + rand.nextInt(3);
		Set<Point> fruitPositions = new HashSet<>();

		while (fruitPositions.size() < numFruits) {
			int fx = rand.nextInt(CANOPY_WIDTH_BLOCKS);
			int fy = rand.nextInt(CANOPY_HEIGHT_BLOCKS);
			fruitPositions.add(new Point(fx, fy));
		}

		for (Point p : fruitPositions) {
			float fruitX = canopyStartX + p.x * BLOCK_SIZE + BLOCK_SIZE / 2f;
			float fruitY = canopyTopY + p.y * BLOCK_SIZE + BLOCK_SIZE / 2f;
			Fruit fruit = new Fruit(new Vector2(fruitX, fruitY));
			fruit.setTag("treeFruit");
			parts.add(fruit);
		}
	}

	/**
	 * Updates the tree and all its parts.
	 * @param deltaTime Time elapsed since last update
	 */
	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		for (GameObject part : parts) {
			part.update(deltaTime);
		}
	}
}