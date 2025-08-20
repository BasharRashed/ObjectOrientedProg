package pepse.world;





import danogl.gui.rendering.RectangleRenderable;

import danogl.gui.rendering.Renderable;

import danogl.util.Vector2;

import pepse.util.ColorSupplier;

import pepse.util.NoiseGenerator;



import java.awt.*;

import java.util.*;

import java.util.List;

import static pepse.world.Block.SIZE;



/**

 * Represents the terrain in the game world.

 * Handles terrain generation, height calculations, and block creation.

 */

public class Terrain {

	public static final Color BASE_GROUND_COLOR = new Color(212, 123, 74);

	private static final int TERRAIN_DEPTH = 25;

	private static final int MAX_LOADED_CHUNKS = 20;

	private final Vector2 windowDimensions;

	private final int groundHeightAtX0;

	private final NoiseGenerator noiseGenerator;

	private final Map<Integer, Chunk> chunks;

	private final int seed;

	private final Queue<Integer> loadedChunkOrder;



	/**

	 * Constructs a new Terrain instance.
	 * @param windowDimensions The dimensions of the game window

	 * @param seed             The seed for terrain generation
	 */

	public Terrain(Vector2 windowDimensions, int seed) {

		this.windowDimensions = windowDimensions;
		this.groundHeightAtX0 = (int) (windowDimensions.y() * 2 / 3);
		this.noiseGenerator = new NoiseGenerator(seed, groundHeightAtX0);
		this.chunks = new HashMap<>();
		this.seed = seed;
		this.loadedChunkOrder = new LinkedList<>();
	}



	/**
	 * Gets the seed used for terrain generation.
	 * @return The terrain seed
	 */

	public int getSeed() {
		return seed;
	}



	/**

	 * Calculates the ground height at a given x-coordinate.

	 *

	 * @param x The x-coordinate to calculate height for

	 * @return The height of the ground at the specified x-coordinate

	 */

	public float groundHeightAt(float x) {
		float noise = (float) noiseGenerator.noise(Math.abs(x), SIZE * 7);
		return groundHeightAtX0 + noise;
	}



	/**
	 * Gets or creates a chunk at the specified x-coordinate.
	 * @param chunkX The chunk x-coordinate
	 * @return The chunk at the specified coordinate
	 */

	public Chunk getChunk(int chunkX) {
		Chunk existingChunk = chunks.get(chunkX);
		if (existingChunk != null) {
			if (!existingChunk.isLoaded()) {
				loadChunk(existingChunk);
			}
			return existingChunk;
		}
		Chunk newChunk = generateChunk(chunkX);
		chunks.put(chunkX, newChunk);
		loadChunk(newChunk);
		return newChunk;

	}




	/**

	 * Creates terrain blocks within the specified x-coordinate range.
	 * @param minX The minimum x-coordinate
	 * @param maxX The maximum x-coordinate
	 * @return A list of blocks representing the terrain
	 */

	public List<Block> createInRange(int minX, int maxX) {
		List<Block> blocks = new ArrayList<>();
		int startChunk = Chunk.getChunkXForCoordinate(minX);
		int endChunk = Chunk.getChunkXForCoordinate(maxX);
		for (int chunkX = startChunk; chunkX <= endChunk; chunkX++) {
			Chunk chunk = getChunk(chunkX);
			blocks.addAll(chunk.getBlocks());
		}
		return blocks;
	}

	/**
	 * Loads a chunk into memory.
	 * @param chunk The chunk to load
	 */

	private void loadChunk(Chunk chunk) {

		while (loadedChunkOrder.size() >= MAX_LOADED_CHUNKS) {
			int oldestChunkX = loadedChunkOrder.poll();

			Chunk oldestChunk = chunks.get(oldestChunkX);

			if (oldestChunk != null && !oldestChunk.isActive()) {
				unloadChunk(oldestChunk);
				chunks.remove(oldestChunkX);
			}
		}
		if (!chunk.isLoaded()) {
			int minX = chunk.getMinX();
			int maxX = chunk.getMaxX();
			for (int x = minX; x < maxX; x += Block.SIZE) {
				float height = calculateBlockHeight(x);
				createVerticalBlockColumn(x, height, chunk);
			}
			chunk.setLoaded(true);
			loadedChunkOrder.add(chunk.getChunkX());
		}
	}

	/*
	 * Unloads a chunk from memory.
	 * @param chunk The chunk to unload
	 */
	private void unloadChunk(Chunk chunk) {

		if (chunk.isLoaded()) {
			chunk.clear();
			chunk.setLoaded(false);
			chunks.remove(chunk.getChunkX()); // Remove from chunks map to ensure regeneration
		}
	}
	/*
	 * Generates a new chunk at the specified x-coordinate.
	 * @param chunkX The chunk x-coordinate
	 * @return The newly generated chunk
	 */

	private Chunk generateChunk(int chunkX) {
		return new Chunk(chunkX);
	}


	/* Calculates the height for a block at the given x-coordinate */

	private float calculateBlockHeight(int x) {

		return (float) (Math.floor((groundHeightAt(x) / Block.SIZE)) * Block.SIZE);

	}

	/* Creates a vertical column of blocks at the specified x-coordinate */

	private void createVerticalBlockColumn(int x, float height, Chunk chunk) {

		for (int i = 0; i < TERRAIN_DEPTH; i++) {
			float y = height + i * Block.SIZE;
			if (y >= windowDimensions.y() + SIZE) {
				break;
			}

			if(i<=1){
				createBlock(x,y,chunk,"topground");
			}
			else{

				createBlock(x,y,chunk,"ground");
			}


		}

	}

	/* Creates a single block at the specified coordinates */

	private void createBlock(int x, float y, Chunk chunk, String tag) {

		Renderable renderable = new RectangleRenderable(ColorSupplier.approximateColor(BASE_GROUND_COLOR));

		Block block = new Block(new Vector2(x, y), renderable,true);

		block.setTag("ground");

		chunk.addBlock(block);

	}
}