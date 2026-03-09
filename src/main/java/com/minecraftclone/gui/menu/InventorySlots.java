package com.minecraftclone.gui.menu;

import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.minecraftclone.gui.GUIManager;
import com.minecraftclone.gui.display.Slot;
import java.util.ArrayList;
import java.util.List;
import jme3utilities.math.Vector3i;

public class InventorySlots {

    private List<Slot> inventorySlots = new ArrayList<>();
    private List<Vector3i> anchors = new ArrayList<>();
    private GUIManager guiManager;
    private Node node;

    public InventorySlots(GUIManager guiManager, Node node) {
        this.guiManager = guiManager;
        this.node = node;

        for (int i = 0; i < 4; i++) {
            for (int i0 = 0; i0 < 9; i0++) {
                if (i == 0) {
                    Vector3i tempVec = new Vector3i(
                        guiManager.getScale() * (48 + 18 * i0),
                        203 * guiManager.getScale(),
                        0
                    );
                    anchors.add(tempVec);

                    Slot slot = new Slot(guiManager, tempVec.x(), tempVec.y());
                    slot.getDisplay().setUserData("index", i0 * 9 + i);
                    slot.attachTo(node);
                    inventorySlots.add(slot);
                } else {
                    Vector3i tempVec = new Vector3i(
                        guiManager.getScale() * (48 + 18 * i0),
                        guiManager.getScale() * (127 + 18 * i),
                        0
                    );
                    anchors.add(tempVec);

                    Slot slot = new Slot(guiManager, tempVec.x(), tempVec.y());
                    slot.getDisplay().setUserData("index", i0 * 9 + i);
                    slot.attachTo(node);
                    inventorySlots.add(slot);
                }
            }
        }
    }

    public void alignWith(MenuGeneric menu) {
        for (int i = 0; i < 36; i++) {
            int tempX = anchors.get(i).x();
            int tempY = anchors.get(i).y();

            inventorySlots
                .get(i)
                .setPosition(
                    (guiManager.getWindowWidth() - menu.getWidth()) / 2 + tempX,
                    (guiManager.getWindowHeight() + menu.getHeight()) / 2 - tempY
                );
        }
    }

    /**
     * Displays an item at the specified slot in the inventory
     * @param index Specifies Slot where an Item shoul dbe displayed
     * @param item SPecifies the item that should be displayed
     */
    public void displayItem(int index, String id, int amount) {
        if (index >= 0 && index <= 35) {
            Slot slot = inventorySlots.get(index);

            if (amount > 1) {
                slot.setText(String.valueOf(amount));
            } else {
                slot.setText("");
            }
            slot.setTexture(guiManager.loadItemTexture2d(id));
        }
    }

    public void setVisibility(boolean visible) {
        if (visible) {
            node.setCullHint(Spatial.CullHint.Inherit);
        } else {
            node.setCullHint(Spatial.CullHint.Always);
        }
    }

    public Slot getSlot(int slot) {
        return inventorySlots.get(slot);
    }

    public int getClickedSlotIndex(Vector2f cursorPosition) {
        for (int i = 0; i < inventorySlots.size(); i++) {
            Vector3f tempPos = inventorySlots.get(i).getPosition();

            if (
                cursorPosition.x >= tempPos.x &&
                cursorPosition.x <= tempPos.x + inventorySlots.get(i).getWidth() &&
                cursorPosition.y >= tempPos.y &&
                cursorPosition.y <= tempPos.y + inventorySlots.get(i).getHeight()
            ) return i;
        }
        return -1;
    }
}
