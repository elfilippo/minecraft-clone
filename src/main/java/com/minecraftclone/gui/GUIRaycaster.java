package com.minecraftclone.gui;

import com.jme3.collision.CollisionResults;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;

public class GUIRaycaster {

    private Camera cam;
    private Node guiNode;

    protected GUIRaycaster(Camera cam, Node guiNode) {
        this.cam = cam;
        this.guiNode = guiNode;
    }

    public Geometry selectSlot(Vector2f cursorPosition) {
        CollisionResults results = new CollisionResults();

        Vector3f origin = cam.getWorldCoordinates(cursorPosition, 0f);
        Vector3f direction = cam.getWorldCoordinates(cursorPosition, 1f).subtractLocal(origin).normalizeLocal();

        Ray ray = new Ray(origin, direction);

        guiNode.collideWith(ray, results);

        if (results.size() > 0) {
            return results.getClosestCollision().getGeometry();
        }

        return null;
    }
}
