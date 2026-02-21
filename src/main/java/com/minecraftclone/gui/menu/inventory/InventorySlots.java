package com.minecraftclone.gui.menu.inventory;

import com.jme3.math.Vector2f;
import com.minecraftclone.gui.GUIManager;
import com.minecraftclone.gui.display.InventorySlot;
import com.minecraftclone.gui.menu.MenuGeneric;
import java.util.ArrayList;
import java.util.List;
import jme3utilities.math.Vector3i;

public class InventorySlots {

    private List<InventorySlot> inventorySlots = new ArrayList<>();
    private List<Vector3i> anchors = new ArrayList<>();
    private GUIManager guiManager;

    InventorySlots(GUIManager guiManager) {
        this.guiManager = guiManager;
        for (int i = 0; i < 4; i++) {
            for (int i0 = 0; i0 < 9; i0++) {
                if (i == 0) {
                    Vector3i tempVec = new Vector3i(guiManager.getScale() * (48 + 18 * i0), 203 * guiManager.getScale(), 0);
                    anchors.add(tempVec);

                    InventorySlot slot = new InventorySlot(guiManager, tempVec.x(), tempVec.y());
                    slot.attachTo(inventoryNode);
                    inventorySlots.add(slot);
                } else {
                    Vector3i tempVec = new Vector3i(guiManager.getScale() * (48 + 18 * i0), guiManager.getScale() * (127 + 18 * i), 0);
                    anchors.add(tempVec);

                    InventorySlot slot = new InventorySlot(guiManager, tempVec.x(), tempVec.y());
                    slot.attachTo(inventoryNode);
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
                    tempX + (guiManager.getWindowWidth() - menu.getWidth()) / 2,
                    (guiManager.getWindowHeight() + menu.getHeight()) / 2 - tempY
                );
        }
    }
}
