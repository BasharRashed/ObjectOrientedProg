package pepse.world;

import danogl.GameObject;
import danogl.components.CoordinateSpace;
import danogl.gui.rendering.TextRenderable;
import danogl.util.Vector2;

import java.awt.*;
import java.util.function.Supplier;

/**
 * Represents the energy display in the game.
 * Shows the current energy level of the avatar as a percentage.
 */
public class EnergyDisplay extends GameObject {
    private static final Vector2 DISPLAY_DIMENSIONS = new Vector2(150, 50);
    private static final Vector2 DISPLAY_TOP_LEFT = new Vector2(10, 10);
    private static final String TEXT_FONT = "Arial";

    private final Supplier<Float> energySupplier;
    private TextRenderable textRenderable;

    /**
     * Constructs a new EnergyDisplay.
     *
     * @param energySupplier A supplier that provides the current energy level
     */
    public EnergyDisplay(Supplier<Float> energySupplier) {
        super(DISPLAY_TOP_LEFT, DISPLAY_DIMENSIONS, null);
        this.energySupplier = energySupplier;
        initializeDisplay();
    }

    /* Initializes the display with text renderer and camera coordinates */
    private void initializeDisplay() {
        this.textRenderable = new TextRenderable("", TEXT_FONT, false, false);
        this.renderer().setRenderable(textRenderable);
        this.setCoordinateSpace(CoordinateSpace.CAMERA_COORDINATES);
    }

    /**
     * Updates the energy display with the current energy level.
     *
     * @param deltaTime The time elapsed since the last update
     */
    @Override
    public void update(float deltaTime) {
        super.update(deltaTime);
        updateEnergyText();
    }

    /* Updates the displayed energy text with the current percentage */
    private void updateEnergyText() {
        int energyPercentage = (int) Math.ceil(energySupplier.get());
        textRenderable.setString(energyPercentage + "%");
    }
}