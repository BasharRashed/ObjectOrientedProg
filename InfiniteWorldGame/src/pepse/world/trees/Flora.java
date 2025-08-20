package pepse.world.trees;

import danogl.GameObject;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.util.ColorSupplier;
import pepse.world.Terrain;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Flora extends GameObject {
	private final Terrain terrain;
	private final Random rand;
	private final List<Tree> trees;
	private final int chunkX;

	// Increased tree spacing for more natural distribution
	public static final int TREE_SPACING = Tree.BLOCK_SIZE * 16;
	private static final int TREE_SPAWN_PROBABILITY = 90;
	private static final Color FLORA_COLOR = new Color(50, 200, 30);

	public Flora(Terrain terrain, int seed, int chunkX) {
		super(Vector2.ZERO, Vector2.ZERO, new RectangleRenderable(FLORA_COLOR));
		this.terrain = terrain;
		this.chunkX = chunkX;
		this.rand = new Random(seed + chunkX);
		this.trees = new ArrayList<>();
	}

	/**
	 * Generate all trees (trunk + leaves) between minX (inclusive) and maxX
	 * (exclusive).
	 * Places a tree in each column with a weighted coin toss (recommended by
	 * assignment).
	 */
	public List<GameObject> createInRange(int minX, int maxX) {
		List<GameObject> allParts = new ArrayList<>();
		for (int x = minX; x < maxX; x += TREE_SPACING) {
			if (rand.nextInt(100) >= TREE_SPAWN_PROBABILITY) {
				float groundY = (float) terrain.groundHeightAt(x);
				int treeSeed = rand.nextInt() + (int)x;
				Tree tree = new Tree(new Vector2(x, groundY), treeSeed);
				trees.add(tree);
				allParts.addAll(tree.getParts());
			}
		}
		return allParts;
	}

	/**
	 * Gets all trees in this flora.
	 * @return List of trees
	 */
	public List<Tree> getTrees() {
		return trees;
	}

	/**
	 * Updates the flora and all its trees.
	 * @param deltaTime Time elapsed since last update
	 */
	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		for (Tree tree : trees) {
			tree.update(deltaTime);
		}
	}
}