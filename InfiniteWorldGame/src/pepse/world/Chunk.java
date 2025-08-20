package pepse.world;



import danogl.GameObject;
import java.util.ArrayList;

import java.util.List;



/**

 * Represents a chunk of terrain in the game world.

 * Each chunk contains blocks and their positions within a specific x-coordinate

 * range.

 */

public class Chunk {

    private final int chunkX;

    private final List<Block> blocks;

    private final List<GameObject> flora;

    private boolean isActive;

    public static final int CHUNK_SIZE = 10 * Block.SIZE;

    private boolean isLoaded;



    /**

     * Creates a new chunk at the specified x-coordinate.

     *

     * @param chunkX The x-coordinate of the chunk (in chunk units)

     */

    public Chunk(int chunkX) {

        this.chunkX = chunkX;

        this.blocks = new ArrayList<>();

        this.flora = new ArrayList<>();

        this.isActive = false;

        this.isLoaded = false;

    }



    /**

     * Gets the x-coordinate of this chunk.

     *

     * @return The chunk's x-coordinate

     */

    public int getChunkX() {

        return chunkX;

    }



    /**

     * Gets the minimum x-coordinate of this chunk in world space.

     *

     * @return The minimum x-coordinate

     */

    public int getMinX() {

        return chunkX * CHUNK_SIZE;

    }



    /**

     * Gets the maximum x-coordinate of this chunk in world space.

     *

     * @return The maximum x-coordinate

     */

    public int getMaxX() {

        return (chunkX + 1) * CHUNK_SIZE;

    }



    /**

     * Gets all blocks in this chunk.

     *

     * @return List of blocks

     */

    public List<Block> getBlocks() {

        return new ArrayList<>(blocks); // Return a copy to prevent external modification

    }



    /**

     * Gets all flora (trees, etc.) in this chunk.

     *

     * @return List of flora game objects

     */

    public List<GameObject> getFlora() {

        return new ArrayList<>(flora); // Return a copy to prevent external modification

    }



    /**

     * Adds a block to this chunk.

     *

     * @param block The block to add

     */

    public void addBlock(Block block) {

        blocks.add(block);

    }



    /**

     * Checks if this chunk is currently active.

     *

     * @return True if the chunk is active, false otherwise

     */

    public boolean isActive() {

        return isActive;

    }



    /**

     * Sets whether this chunk is loaded in memory.

     *

     * @param loaded True if the chunk is loaded, false otherwise

     */

    public void setLoaded(boolean loaded) {

        this.isLoaded = loaded;

    }



    /**

     * Checks if this chunk is loaded in memory.

     *

     * @return True if the chunk is loaded, false otherwise

     */

    public boolean isLoaded() {

        return isLoaded;

    }



    /**

     * Clears all game objects from this chunk to free memory.

     */

    public void clear() {

        blocks.clear();

        flora.clear();

        isLoaded = false;

        isActive = false;

    }



    /**

     * Calculates which chunk a given x-coordinate belongs to.

     *

     * @param x The x-coordinate in world space

     * @return The chunk x-coordinate

     */

    public static int getChunkXForCoordinate(float x) {

        return (int) Math.floor(x / CHUNK_SIZE);

    }

}