package ch.epfl.mmspg.testbed360.ui;

import android.util.Log;

import org.rajawali3d.materials.Material;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.primitives.RectangularPrism;

import java.util.ArrayList;

import ch.epfl.mmspg.testbed360.VRViewRenderer;

/**
 * @author Louis-Maxence Garret <louis-maxence.garret@epfl.ch>
 * @date 21/10/2017
 */

public class VRMenu extends RectangularPrism {
    private final static String TAG = "VRMenu";
    private final static float BUTTON_SPACING = 0.5f;
    private static int MENU_COUNTER = 0;

    private ArrayList<VRButton> buttons = new ArrayList<>();
    private float distance;
    private boolean following = true;

    private String tag;

    public VRMenu(float distance) {
        super(0, 0, 0);

        //TODO implement automatic height compensation when adding buttons
        setPosition(new Vector3(0,2,-distance));
        this.distance = distance;

        Material prismMaterial = new Material();
        prismMaterial.setColorInfluence(0);
        setTransparent(true);
        setMaterial(prismMaterial);

        tag = TAG + MENU_COUNTER++;
    }

    public void addButton(VRButton button) {
        if (button == null) {
            throw new IllegalArgumentException("VRButton cannot be null");
        }
        float nextYPos = computeNextButtonY();
        button.setParentMenu(this);
        buttons.add(button);
        addChild(button);

        button.moveUp(-nextYPos);
    }

    public void removeButton(VRButton button) {
        if (button == null) {
            throw new IllegalArgumentException("VRButton cannot be null");
        }
        buttons.remove(button);
        removeChild(button);
    }

    public VRButton getButton(int index) {
        if (index < 0 || buttons.size() <= index) {
            throw new IllegalArgumentException("Incorrect index : " + index);
        }
        return buttons.get(index);
    }

    public boolean onCardboardTrigger() {
        for (VRButton button : buttons) {
            if (button.onCardboardTrigger()) {
                return true;
            }
        }
        return false;
    }

    public void onDrawing(VRViewRenderer renderer) {
        setRotY(180 + renderer.getCurrentCamera().getRotY() * 180.0 / Math.PI);

        if(following) {
            setX(distance * Math.sin(renderer.getCurrentCamera().getRotY()));
            setZ(-distance * Math.cos(renderer.getCurrentCamera().getRotY()));
        }

        boolean consumed = false;
        for (VRButton button : buttons) {
            if (consumed) {
                button.setHovered(false);
            } else {
                consumed = renderer.isLookingAtObject(button,5);
                button.setHovered(consumed);
            }
            /*Vector3 pos = button.getPosition();
            button.setText(
                    "x="+Math.round(pos.x * 2) / 2.0 +
                    "\ny="+Math.round(pos.y * 2) / 2.0 +
                    "\nz="+Math.round(pos.z * 2) / 2.0
            );*/
        }

    }

    private float computeNextButtonY() {
        float y = 0;
        for (VRButton button : buttons) {
            y += button.getHeight() + BUTTON_SPACING;
        }
        return y;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }


}