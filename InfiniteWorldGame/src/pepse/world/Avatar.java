package pepse.world;

import danogl.GameObject;
import danogl.collisions.Collision;
import danogl.gui.ImageReader;
import danogl.gui.UserInputListener;
import danogl.gui.rendering.AnimationRenderable;
import danogl.gui.rendering.Renderable;
import danogl.util.Vector2;

import java.awt.event.KeyEvent;
import java.util.function.Consumer;

/**
 * Represents the player's avatar in the game.
 * Handles movement, jumping, energy management, and animation states.
 */
public class Avatar extends GameObject {
	private static final float VELOCITY_X = 350;
	private static final float VELOCITY_Y = -650;
	private static final float GRAVITY = 600;
	private static final float MAX_ENERGY = 100f;
	private static final float ENERGY_GAIN = 1f;
	private static final float RUN_ENERGY_COST = 0.5f;
	private static final float JUMP_ENERGY_COST = 10f;
	private static final float REPLENISH_DELAY = 0.1f;
	private static final double ANIMATION_TIME = 0.2;
	private static final String AVATAR_TAG = "avatar";
	private static final String IDLE_PICS_PREFIX = "idle";
	private static final String RUN_PICS_PREFIX = "run";
	private static final String JUMP_PICS_PREFIX = "jump";

	/**
	 * Represents the different movement states of the avatar.
	 */
	private enum MovementState {
		IDLE, RUNNING, JUMPING
	}

	private float energy;
	private float timeSinceLastAction;
	private MovementState currentState;
	private UserInputListener inputListener;
	private AnimationRenderable idleAnimation;
	private AnimationRenderable runningAnimation;
	private AnimationRenderable jumpingAnimation;
	private Consumer<Vector2> jumpObserver;
	private boolean isFacingLeft = false;
	private boolean isMoving = false;

	/**
	 * Constructs a new Avatar.
	 *
	 * @param topLeftCorner The position of the avatar's top-left corner
	 * @param inputListener The input listener for handling user input
	 * @param imageReader   The image reader for loading avatar sprites
	 */
	public Avatar(Vector2 topLeftCorner,
				  UserInputListener inputListener,
				  ImageReader imageReader) {
		super(topLeftCorner, new Vector2(60, 85),
				imageReader.readImage("assets/idle_0.png", true));
		initializeAvatar(inputListener, imageReader);
	}

	/**
	 * Sets an observer to be notified when the avatar jumps.
	 *
	 * @param observer The observer to be notified
	 */
	public void setJumpObserver(Consumer<Vector2> observer) {
		this.jumpObserver = observer;
	}

	/**
	 * Gets the current energy level of the avatar.
	 *
	 * @return The current energy level
	 */
	public float getEnergy() {
		return energy;
	}

	/**
	 * Adds energy to the avatar's current energy level.
	 *
	 * @param amount The amount of energy to add
	 */
	public void addEnergy(float amount) {
		energy = Math.min(MAX_ENERGY, energy + amount);
	}

	/**
	 * Updates the avatar's state based on input and physics.
	 *
	 * @param deltaTime The time elapsed since the last update
	 */
	@Override
	public void update(float deltaTime) {
		super.update(deltaTime);
		handleMovement();
		updateEnergy(deltaTime);
		handleJump();
		updateAnimationState();
	}

	/**
	 *
	 * @param other The GameObject with which a collision occurred.
	 * @param collision Information regarding this collision.
	 *                  A reasonable elastic behavior can be achieved with:
	 *                  setVelocity(getVelocity().flipped(collision.getNormal()));
	 */
	@Override
	public void onCollisionEnter(GameObject other, Collision collision) {
		super.onCollisionEnter(other, collision);
		if(other.getTag().equals("topground")){
			this.transform().setVelocityY(0);
		}
	}


	/* Initializes the avatar's properties and animations */
	private void initializeAvatar(UserInputListener inputListener, ImageReader imageReader) {
		physics().preventIntersectionsFromDirection(Vector2.ZERO);
		transform().setAccelerationY(GRAVITY);
		this.setTag(AVATAR_TAG);
		this.inputListener = inputListener;
		this.energy = MAX_ENERGY;
		this.timeSinceLastAction = 0f;
		this.currentState = MovementState.IDLE;
		initializeAnimations(imageReader);
	}

	/* Initializes all animation states for the avatar */
	private void initializeAnimations(ImageReader imageReader) {
		this.idleAnimation = createAnimation(imageReader, IDLE_PICS_PREFIX, 4);
		this.runningAnimation = createAnimation(imageReader, RUN_PICS_PREFIX, 6);
		this.jumpingAnimation = createAnimation(imageReader, JUMP_PICS_PREFIX, 4);
	}

	/* Creates an animation from a sequence of images */
	private AnimationRenderable createAnimation(ImageReader imageReader, String prefix, int frameCount) {
		Renderable[] frames = new Renderable[frameCount];
		for (int i = 0; i < frameCount; i++) {
			frames[i] = imageReader.readImage(String.format("assets/%s_%d.png", prefix, i), true);
		}
		return new AnimationRenderable(frames, ANIMATION_TIME);
	}

	/* Updates the avatar's movement based on user input */
	private void handleMovement() {
		float xVelocity = 0;

		if (inputListener.isKeyPressed(KeyEvent.VK_LEFT) && energy >= RUN_ENERGY_COST) {
			xVelocity -= VELOCITY_X;
			isMoving = true;
			isFacingLeft = true;
			timeSinceLastAction = 0f;
		}
		if (inputListener.isKeyPressed(KeyEvent.VK_RIGHT) && energy >= RUN_ENERGY_COST) {
			xVelocity += VELOCITY_X;
			isMoving = true;
			isFacingLeft = false;
			timeSinceLastAction = 0f;
		}

		transform().setVelocityX(xVelocity);
	}

	/* Updates the avatar's energy level based on actions and time */
	private void updateEnergy(float deltaTime) {
		if (inputListener.isKeyPressed(KeyEvent.VK_LEFT) ||
				inputListener.isKeyPressed(KeyEvent.VK_RIGHT)) {
			energy = Math.max(0, energy - RUN_ENERGY_COST);
		} else if (timeSinceLastAction >= REPLENISH_DELAY && currentState == MovementState.IDLE) {
			energy = Math.min(MAX_ENERGY, energy + ENERGY_GAIN);
		} else {
			timeSinceLastAction += deltaTime;
		}
	}

	/* Handles jumping mechanics and notifies observers */
	private void handleJump() {
		if (inputListener.isKeyPressed(KeyEvent.VK_SPACE) &&
				getVelocity().y() == 0 &&
				energy >= JUMP_ENERGY_COST) {
			transform().setVelocityY(VELOCITY_Y);
			energy -= JUMP_ENERGY_COST;
			timeSinceLastAction = 0f;
			notifyJumpObserver();
		}
	}

	/* Notifies the jump observer if one exists */
	private void notifyJumpObserver() {
		if (jumpObserver != null) {
			jumpObserver.accept(getCenter());
		}
	}

	/* Updates the animation state based on movement and velocity */
	private void updateAnimationState() {
		MovementState newState = determineMovementState();
		updateAnimation(newState, isFacingLeft);
	}

	/* Determines the current movement state based on velocity and input */
	private MovementState determineMovementState() {
		if (getVelocity().y() != 0) {
			return MovementState.JUMPING;
		} else if (inputListener.isKeyPressed(KeyEvent.VK_LEFT) ||
				inputListener.isKeyPressed(KeyEvent.VK_RIGHT)) {
			return MovementState.RUNNING;
		}
		return MovementState.IDLE;
	}



	/* Updates the current animation based on movement state and direction */
	private void updateAnimation(MovementState newState, boolean isFacingLeft) {
		if (newState != currentState) {
			currentState = newState;
			switch (newState) {
				case IDLE:
					renderer().setRenderable(idleAnimation);
					break;
				case RUNNING:
					renderer().setRenderable(runningAnimation);
					break;
				case JUMPING:
					renderer().setRenderable(jumpingAnimation);
					break;
			}
		}
		renderer().setIsFlippedHorizontally(isFacingLeft);
	}
}