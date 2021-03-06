// Copyright (C) 2017 ECOLE POLYTECHNIQUE FEDERALE DE LAUSANNE, Switzerland
// Multimedia Signal Processing Group
//
// This program is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//
// This program is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
// GNU General Public License for more details.
//
// You should have received a copy of the GNU General Public License
// along with this program. If not, see <http://www.gnu.org/licenses/>.
//

package ch.epfl.mmspg.testbed360.ui;

import android.support.annotation.NonNull;

import org.rajawali3d.materials.Material;
import org.rajawali3d.math.vector.Vector3;
import org.rajawali3d.primitives.RectangularPrism;

import java.util.ArrayList;

import ch.epfl.mmspg.testbed360.VRViewRenderer;

/** A menu to hold multiples {@link VRButton}s and {@link VRLongText}, ordered vertically. Is responsible
 * for propagating a trigger event to the {@link VRUI} elements it is the parent of.
 * @author Louis-Maxence Garret <louis-maxence.garret@epfl.ch>
 * @date 21/10/2017
 */

public class VRMenu extends RectangularPrism implements VRUI {
    private final static String TAG = "VRMenu";
    private final static float BUTTON_SPACING = 0.2f;
    private final static float STANDARD_DISTANCE = 20f;

    private static int MENU_COUNTER = 0;

    private ArrayList<VRButton> buttons = new ArrayList<>();
    private float distance;
    private boolean following = true;

    private boolean isRecycled = false;

    private String tag;

    public VRMenu() {
        super(0, 0, 0);
        this.distance = STANDARD_DISTANCE;
        setPosition(new Vector3(0, 2, -distance));


        Material prismMaterial = new Material();
        prismMaterial.setColorInfluence(0);
        setTransparent(true);
        setMaterial(prismMaterial);

        tag = TAG + MENU_COUNTER++;
    }

    /**
     * Adds a {@link VRButton} as a child of this {@link VRMenu}
     * @param button the {@link VRButton} to add
     */
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

    /**see {@link #addButton(VRButton)}
     */
    public void addAllButtons(@NonNull VRButton... buttons) {
        for (VRButton button : buttons) {
            addButton(button);
        }
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

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean onCardboardTrigger() {
        for (VRButton button : buttons) {
            if (button.onCardboardTrigger()) {
                return true;
            }
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onDrawing(VRViewRenderer renderer) {
        setRotY(180 + renderer.getCurrentCamera().getRotY() * 180.0 / Math.PI);

        if (following) {
            setX(distance * Math.sin(renderer.getCurrentCamera().getRotY()));
            setZ(-distance * Math.cos(renderer.getCurrentCamera().getRotY()));
        }

        boolean consumed = false;
        for (VRButton button : buttons) {
            if (consumed) {
                button.setHovered(false);
            } else {
                consumed = renderer.isLookingAtObject(button, 3);
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

    /**
     * Computes the position to give to the next {@link VRUI} element added to this menu
     * @return the y position to give to the element
     */
    private float computeNextButtonY() {
        float y = 0;
        for (VRButton button : buttons) {
            y += button.getHeight() + BUTTON_SPACING;
        }
        return y;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void recycle() {
        isRecycled = true;
        setVisible(false);
        for (VRButton button : buttons) {
            button.recycle();
        }
        buttons.clear();
        destroy();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isRecycled() {
        return isRecycled;
    }
}
