package pepse;
import danogl.GameManager;
import danogl.GameObject;
import danogl.collisions.Layer;
import danogl.gui.ImageReader;
import danogl.gui.SoundReader;
import danogl.gui.UserInputListener;
import danogl.gui.WindowController;
import danogl.gui.rendering.Camera;
import danogl.gui.rendering.RectangleRenderable;
import danogl.util.Vector2;
import pepse.world.*;
import pepse.world.daynight.Night;
import pepse.world.daynight.Sun;
import pepse.world.daynight.SunHalo;
import pepse.world.environment.Cloud;
import pepse.world.trees.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;


/**

 * Main game manager class for the Pepse game.

 * Handles initialization and management of all game objects, including terrain,

 * flora, avatar, day/night cycle, and environmental effects.

 */

public class PepseGameManager extends GameManager {

	private static final float DAY_NIGHT_CYCLE_LENGTH = 30f;

	private static final float CHAR_HEIGHT = 85f;

	private Random rand;

	private Terrain terrain;

	private Avatar avatar;

	private Camera camera;

	private WindowController window;

	private static final int CHUNKS_LOAD_DISTANCE = 5;

	private Set<Integer> activeChunks = new HashSet<>();

	private Map<Integer, List<GameObject>> chunkGameObjects = new HashMap<>();

	private Flora flora; // Add Flora instance



	/**

	 * Main entry point for the game.

	 *

	 * @param args Command line arguments (unused)

	 */

	public static void main(String[] args) {

		new PepseGameManager().run();

	}



	/**

	 * Initializes the game by setting up all game objects and systems.

	 *

	 * @param imageReader      Used to read images from disk

	 * @param soundReader      Used to read sound files from disk

	 * @param inputListener    Used to handle user input

	 * @param windowController Used to control the game window

	 */

	@Override

	public void initializeGame(ImageReader imageReader,
							   SoundReader soundReader,
							   UserInputListener inputListener,
							   WindowController windowController) {

		super.initializeGame(imageReader, soundReader, inputListener, windowController);

		windowController.setTargetFramerate(60);

		this.rand = new Random();

		this.window = windowController;

		initializeSky(windowController);

		initializeTerrain(windowController);

		this.flora = new Flora(terrain, rand.nextInt(), 0); // Initialize Flora with random seed

		initializeAvatar(windowController, inputListener, imageReader);

		initializeDayNightCycle(windowController);

	}



	/*

	 * Initializes the sky background.

	 *

	 * @param windowController Used to get window dimensions

	 */

	private void initializeSky(WindowController windowController) {

		GameObject sky = Sky.create(new Vector2(windowController.getWindowDimensions().x(),

				windowController.getWindowDimensions().y()));

		gameObjects().addGameObject(sky, Layer.BACKGROUND-2);

	}



	/*

	 * Initializes the terrain and adds it to the game.
	 *
	 * @param windowController Used to get window dimensions
	 */
	private void initializeTerrain(WindowController windowController) {
		int terrainSeed = rand.nextInt();
		terrain = new Terrain(new Vector2(windowController.getWindowDimensions()), terrainSeed);
	}

	/*
	 * Initializes the player avatar and sets up the camera to follow it.
	 * @param windowController Used to get window dimensions
	 * @param inputListener Used to handle user input
	 * @param imageReader Used to read avatar images
	 */

	private void initializeAvatar(WindowController windowController,
								  UserInputListener inputListener,
								  ImageReader imageReader) {

		float spawnX = windowController.getWindowDimensions().x() / 2f;

		avatar = new Avatar(new Vector2(spawnX, terrain.groundHeightAt(spawnX) - CHAR_HEIGHT),

				inputListener, imageReader);
		gameObjects().addGameObject(avatar, Layer.DEFAULT);
		setupCamera(windowController, spawnX);
		setupEnergyDisplay();
		updateChunks(spawnX);
	}



	/*

	 * Sets up the camera to follow the avatar.
	 * @param windowController Used to get window dimensions
	 * @param spawnX The x-coordinate where the avatar spawns
	 */

	private void setupCamera(WindowController windowController, float spawnX) {
		float initialGroundHeight = terrain.groundHeightAt(spawnX);

		float cameraHeight = initialGroundHeight - 200;

		GameObject cameraTarget = new GameObject(

				new Vector2(spawnX, cameraHeight),

				new Vector2(1, 1),

				new RectangleRenderable(java.awt.Color.BLACK));


		avatar.addComponent(deltaTime -> {

			cameraTarget.setCenter(new Vector2(avatar.getCenter().x(), cameraHeight));

		});



		this.camera = new Camera(cameraTarget,

				new Vector2(windowController
						.getWindowDimensions().x() * 0.55f - spawnX, 0),

				windowController.getWindowDimensions(),

				windowController.getWindowDimensions());

		setCamera(camera);

	}



	/*

	 * Sets up the energy display for the avatar.

	 */

	private void setupEnergyDisplay() {

		EnergyDisplay energyDisplay = new EnergyDisplay(avatar::getEnergy);

		gameObjects().addGameObject(energyDisplay, Layer.UI);

	}



	/*

	 * Initializes the day/night cycle and environmental effects.

	 *

	 * @param windowController Used to get window dimensions

	 */

	private void initializeDayNightCycle(WindowController
												 windowController) {

		initializeClouds(windowController);

		initializeSun(windowController);

		initializeNight(windowController);

	}



	/*

	 * Initializes the cloud system.

	 *

	 * @param windowController Used to get window dimensions

	 */

	private void initializeClouds(WindowController windowController) {

		List<GameObject> cloud = Cloud.create(windowController
						.getWindowDimensions(), DAY_NIGHT_CYCLE_LENGTH,

				gameObjects());

		for (GameObject cloudBlock : cloud) {

			gameObjects().addGameObject(cloudBlock, Layer.BACKGROUND + 2);

		}

		avatar.setJumpObserver(pos -> Cloud.createRain());

	}



	/*

	 * Initializes the sun and its halo effect.

	 *

	 * @param windowController Used to get window dimensions

	 */

	private void initializeSun(WindowController windowController) {

		GameObject sun = Sun.create(windowController
				.getWindowDimensions(), DAY_NIGHT_CYCLE_LENGTH);

		gameObjects().addGameObject(sun, Layer.BACKGROUND-1);

		GameObject sunHalo = SunHalo.create(sun);

		gameObjects().addGameObject(sunHalo, Layer.BACKGROUND-1);

	}



	/*

	 * Initializes the night effect.

	 *

	 * @param windowController Used to get window dimensions

	 */

	private void initializeNight(WindowController windowController) {

		GameObject night = Night.create(windowController.
				getWindowDimensions(), (float) DAY_NIGHT_CYCLE_LENGTH);

		gameObjects().addGameObject(night, Layer.FOREGROUND);

	}



	/**

	 * Updates the game state each frame.

	 *

	 * @param deltaTime Time elapsed since last update

	 */

	@Override

	public void update(float deltaTime) {

		super.update(deltaTime);



		// Update chunks based on avatar position

		if (avatar != null) {

			updateChunks(avatar.getCenter().x() + Chunk.CHUNK_SIZE);

		}

	}



	/**

	 * Updates the active chunks based on the given x-coordinate.

	 *

	 * @param x The x-coordinate to center the chunk loading around

	 */

	private void updateChunks(float x) {

		int centerChunk = Chunk.getChunkXForCoordinate(x);

		Set<Integer> newActiveChunks = new HashSet<>();





		for (int i = -CHUNKS_LOAD_DISTANCE; i <= CHUNKS_LOAD_DISTANCE; i++) {

			newActiveChunks.add(centerChunk + i);

		}


		Set<Integer> chunksToLoad = new HashSet<>(newActiveChunks);

		chunksToLoad.removeAll(activeChunks);

		for (int chunkX : chunksToLoad) {

			loadChunk(chunkX);

		}


		Set<Integer> chunksToUnload = new HashSet<>(activeChunks);

		chunksToUnload.removeAll(newActiveChunks);

		for (int chunkX : chunksToUnload) {

			unloadChunk(chunkX);

		}


		activeChunks = newActiveChunks;

	}


	/**
	 * Loads a chunk and its associated game objects.
	 * @param chunkX The x-coordinate of the chunk to load
	 */

	private void loadChunk(int chunkX) {
		if (chunkGameObjects.containsKey(chunkX)) {

			return;

		}
		List<GameObject> chunkObjects = new ArrayList<>();

		int minX = (chunkX-1) * Chunk.CHUNK_SIZE;

		int maxX = (chunkX + 1) * Chunk.CHUNK_SIZE;



		List<Block> blocks = terrain.createInRange(minX, maxX);

		for (Block block : blocks) {
			if(block.getTag().equals("topground")) {
				gameObjects().addGameObject(block, Layer.STATIC_OBJECTS);
			}
			else{
				gameObjects().addGameObject(block, Layer.STATIC_OBJECTS);
			}

			chunkObjects.add(block);

		}


		Flora chunkFlora = new Flora(terrain, terrain.getSeed(), chunkX);

		List<GameObject> forest = chunkFlora.createInRange(minX, maxX);
		for (GameObject part : forest) {

			String tag = part.getTag();

			if (tag != null) {

				switch (tag) {

					case "treeTrunk":

						gameObjects().addGameObject(part, Layer.STATIC_OBJECTS);

						break;

					case "treeLeaf", "treeFruit":

						gameObjects().addGameObject(part, Layer.DEFAULT);

						break;

				}

				chunkObjects.add(part);

			}

		}

		gameObjects().addGameObject(chunkFlora, Layer.DEFAULT);
		chunkObjects.add(chunkFlora);
		chunkGameObjects.put(chunkX, chunkObjects);

	}



	/**

	 * Unloads a chunk and removes its associated game objects.

	 *

	 * @param chunkX The x-coordinate of the chunk to unload

	 */

	private void unloadChunk(int chunkX) {

		List<GameObject> objects = chunkGameObjects.remove(chunkX);

		if (objects != null) {

			for (GameObject obj : objects) {

				gameObjects().removeGameObject(obj, Layer.STATIC_OBJECTS);

				gameObjects().removeGameObject(obj, Layer.DEFAULT);

				gameObjects().removeGameObject(obj, Layer.BACKGROUND);

				gameObjects().removeGameObject(obj, Layer.FOREGROUND);

			}

		}

	}

}