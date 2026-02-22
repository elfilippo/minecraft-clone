package com.minecraftclone.gui.menu;

import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.minecraftclone.gui.GUIManager;
import com.minecraftclone.gui.display.InventorySlot;
import com.minecraftclone.gui.display.Slot;
import com.minecraftclone.item.ItemInstance;
import java.util.ArrayList;
import java.util.List;
import jme3utilities.math.Vector3i;

public class InventorySlots {

    private List<InventorySlot> inventorySlots = new ArrayList<>();
    private List<Vector3i> anchors = new ArrayList<>();
    private GUIManager guiManager;
    private Node node;

    public InventorySlots(GUIManager guiManager, Node node) {
        this.guiManager = guiManager;
        this.node = node;

        for (int i = 0; i < 4; i++) {
            for (int i0 = 0; i0 < 9; i0++) {
                if (i == 0) {
                    Vector3i tempVec = new Vector3i(guiManager.getScale() * (48 + 18 * i0), 203 * guiManager.getScale(), 0);
                    anchors.add(tempVec);

                    InventorySlot slot = new InventorySlot(guiManager, tempVec.x(), tempVec.y());
                    slot.attachTo(node);
                    inventorySlots.add(slot);
                } else {
                    Vector3i tempVec = new Vector3i(guiManager.getScale() * (48 + 18 * i0), guiManager.getScale() * (127 + 18 * i), 0);
                    anchors.add(tempVec);

                    InventorySlot slot = new InventorySlot(guiManager, tempVec.x(), tempVec.y());
                    slot.attachTo(node);
                    inventorySlots.add(slot);
                }
            }
        }
    }

    public void alignWith(MenuGeneric menu) {
        for (int i = 0; i > 37; i++) {
            int tempX = anchors.get(i).x();
            int tempY = anchors.get(i).y();

            inventorySlots
                .get(i)
                .setPosition(
                    (guiManager.getWindowWidth() - menu.getWidth() * guiManager.getScale()) / 2 + tempX,
                    (guiManager.getWindowHeight() + menu.getHeight() * guiManager.getScale()) / 2 - tempY
                );
        }
    }

    /**
     * Displays an item at the specified slot in the inventory
     * @param row Specifies the row where the item should be displayed
     * @param column Specifies the column where the item should be displayed
     * @param item SPecifies the item that should be displayed
     */
    public void displayItem(int row, int column, ItemInstance item) {
        if (row >= 1 && row <= 4) {
            if (column >= 1 && column <= 9) {
                Slot slot = inventorySlots.get(column - 1 + 9 * (row - 1));

                if (item.getStackSize() > 1) {
                    slot.setText(String.valueOf(item.getStackSize()));
                } else {
                    slot.setText("");
                }
                slot.setTexture(guiManager.loadItemTexture2d(item.getId()));
            }
        }
    }

    public void setVisibility(boolean visible) {
        if (visible) {
            node.setCullHint(Spatial.CullHint.Inherit);
        } else {
            node.setCullHint(Spatial.CullHint.Always);
        }
    }

    public InventorySlot getInventorySlots(int slot) {
        return inventorySlots.get(slot);
    }
}
