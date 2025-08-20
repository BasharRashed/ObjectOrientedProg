package bricker.brick_strategies;

import bricker.gameobjects.Ball;
import bricker.gameobjects.FallingHeart;
import bricker.gameobjects.VisualHeart;
import bricker.gameobjects.Paddle;
import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.gui.ImageReader;
import danogl.gui.WindowController;
import danogl.gui.rendering.Renderable;
import danogl.util.Counter;
import danogl.util.Vector2;

import java.util.ArrayList;
import java.util.List;

/**
 * A collision strategy that spawns a falling heart power-up when the brick is hit.
 * If collected by the paddle, the heart increases the player's life count
 * sand adds a visual heart.
 * It ensures the number of total hearts does not exceed a defined maximum.
 * Also removes the brick and decrements the brick counter on collision.
 */
public class ExtraHeartStrategy implements CollisionStrategy {
    /** Maximum number of lives allowed. */
    private static final int MAX_HEARTS = 4;

    /** Falling speed for the heart. */
    private static final float FALLING_SPEED = 100;

    /** Dimensions of each heart icon. */
    private static final Vector2 HEART_SIZE = new Vector2(15, 15);

    private final GameObjectCollection gameObjects;
    private final ImageReader imageReader;
    private final WindowController windowController;
    private final Counter bricksCounter;
    private final Paddle mainPaddle;
    private final Counter livesCounter;
    private final List<VisualHeart> hearts;
    private final Ball ball;

    /** List to keep track of falling heart objects in the game. */
    private final List<GameObject> fallingHearts = new ArrayList<>();

    /**
     * Constructs an ExtraHeartStrategy.
     *
     * @param gameObjects Collection of all game objects.
     * @param imageReader Image loader.
     * @param windowController Window control and dimensions.
     * @param bricksCounter Global counter for remaining bricks.
     * @param mainPaddle Paddle that can collect hearts.
     * @param livesCounter Life counter to increment upon heart collection.
     * @param hearts List of visual hearts displayed.
     * @param ball Reference to the game's main ball.
     */
    public ExtraHeartStrategy(GameObjectCollection gameObjects,
                              ImageReader imageReader,
                              WindowController windowController,
                              Counter bricksCounter,
                              Paddle mainPaddle,
                              Counter livesCounter,
                              List<VisualHeart> hearts,
                              Ball ball) {
        this.gameObjects = gameObjects;
        this.imageReader = imageReader;
        this.windowController = windowController;
        this.bricksCounter = bricksCounter;
        this.mainPaddle = mainPaddle;
        this.livesCounter = livesCounter;
        this.hearts = hearts;
        this.ball = ball;
    }

    /**
     * Handles collision with a brick. Removes the brick, decrements counter,
     * and creates a falling heart.
     * @param other The object that collided (usually the ball).
     * @param thisObject The brick being hit.
     */
    @Override
    public void onCollision(GameObject other, GameObject thisObject) {
        if (other.getTag().equals("Ball") || other.getTag().equals("Puck")) {
            if (gameObjects.removeGameObject(thisObject, Layer.STATIC_OBJECTS)) {
                bricksCounter.decrement();
            }

            createFallingHeart(thisObject.getCenter());
        }
    }

    /*
     * Creates a falling heart at a specified position if under the max heart limit.
     */
    private void createFallingHeart(Vector2 position) {
        if (livesCounter.value() + fallingHearts.size() >= MAX_HEARTS) {
            return;
        }

        Renderable heartImage = imageReader.readImage("assets/heart.png",
                true);
        Vector2 heartPosition = position.subtract(HEART_SIZE.mult(0.5f));

        FallingHeart fallingHeart = new FallingHeart(
                heartPosition,
                HEART_SIZE,
                heartImage,
                mainPaddle,
                this
        );
        fallingHeart.setVelocity(Vector2.DOWN.mult(FALLING_SPEED));
        gameObjects.addGameObject(fallingHeart);
        fallingHearts.add(fallingHeart);
    }

    /**
     * Called when a falling heart is collected. Increments lives,
     * adds visual heart, and removes falling heart.
     *
     * @param heartObject The collected heart object.
     */
    public void handleHeartCollection(GameObject heartObject) {
        if (livesCounter.value() < MAX_HEARTS) {
            livesCounter.increment();
            VisualHeart.updateHearts(gameObjects, windowController, ball);
        }
        gameObjects.removeGameObject(heartObject);
        fallingHearts.remove(heartObject);
    }




}
