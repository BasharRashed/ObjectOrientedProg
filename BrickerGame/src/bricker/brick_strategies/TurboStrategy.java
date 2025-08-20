package bricker.brick_strategies;

import bricker.gameobjects.Ball;
import danogl.GameObject;
import danogl.collisions.GameObjectCollection;
import danogl.collisions.Layer;
import danogl.gui.ImageReader;
import danogl.gui.rendering.Renderable;
import danogl.util.Counter;

/**
 * A collision strategy that triggers turbo mode for the main ball upon brick collision.
 * Turbo mode increases the ball's speed and changes its appearance temporarily.
 * Also removes the collided brick and decrements the global brick counter.
 *
 * This strategy assumes it is used in conjunction with a brick collision event.
 */
public class TurboStrategy implements CollisionStrategy {

    /** Reader used to load the turbo ball image. */
    private final ImageReader imageReader;

    /** Global brick counter to decrement when a brick is destroyed. */
    private final Counter bricksCounter;

    /** Collection of all game objects, used to remove bricks. */
    private final GameObjectCollection gameObjects;

    /** Turbo mode visual for the ball. */
    private Renderable turboBallImage;

    /** Reference to the game's main ball. */
    private final Ball mainBall;

    /**
     * Constructs a TurboStrategy instance.
     *
     * @param gameObjects Collection of all game objects.
     * @param imageReader Image reader to load the turbo ball sprite.
     * @param bricksCounter Counter tracking remaining bricks.
     * @param ball Reference to the game's main ball.
     */
    public TurboStrategy(GameObjectCollection gameObjects,
                         ImageReader imageReader, Counter bricksCounter, Ball ball) {
        this.imageReader = imageReader;
        this.gameObjects = gameObjects;
        this.bricksCounter = bricksCounter;
        this.turboBallImage = imageReader.readImage("assets/redball.png", true);
        this.mainBall = ball;
    }

    /**
     * Handles the collision between the ball and a brick. Removes the brick and
     * activates turbo mode on the main ball if it's the collider.
     *
     * @param thisObject The object that owns this strategy (typically a brick).
     * @param otherObject The object it collided with (expected to be the ball).
     */
    @Override
    public void onCollision(GameObject thisObject, GameObject otherObject) {
        if (gameObjects.removeGameObject(otherObject, Layer.STATIC_OBJECTS)) {
            bricksCounter.decrement();
        }
        if (thisObject.getTag().equals("Ball")) {
            mainBall.setTurboBallImage(turboBallImage);
            mainBall.activateTurbo();
        }
    }
}
