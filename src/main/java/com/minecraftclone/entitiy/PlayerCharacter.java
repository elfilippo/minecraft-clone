package com.minecraftclone.entitiy;

import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapText;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;
import com.minecraftclone.Main;
import com.minecraftclone.world.ActionInput;

public class PlayerCharacter {

    private final CharacterControl playerControl;
    private final Node playerNode;

    private final float stepHeight = 0.2f;
    private final float speed = 0.2f;
    private final boolean debugEnabled = false;
    private final Vector3f walkDir = new Vector3f();

    public PlayerCharacter(BulletAppState bulletAppState) {
        bulletAppState.setDebugEnabled(debugEnabled);

        var shape = new CapsuleCollisionShape(0.5f, 1.8f);
        var player = new CharacterControl(shape, stepHeight);
        player.setJumpSpeed(10f);
        player.setFallSpeed(20f);
        player.setGravity(30f);

        Node playerNode = new Node("Player");
        playerNode.addControl(player);
        bulletAppState.getPhysicsSpace().add(player);
        player.setPhysicsLocation(new Vector3f(5, 2, 2));

        this.playerControl = player;
        this.playerNode = playerNode;
    }

    public void tick(ActionInput input, Camera cam) {
        Vector3f forward = cam.getDirection().clone();
        forward.setY(0).normalizeLocal().multLocal(speed);
        Vector3f left = cam.getLeft().clone();
        left.setY(0).normalizeLocal().multLocal(speed);

        walkDir.set(0, 0, 0);

        if (input.isForward()) walkDir.addLocal(forward);
        if (input.isBackward()) walkDir.addLocal(forward.negate());
        if (input.isLeft()) walkDir.addLocal(left);
        if (input.isRight()) walkDir.addLocal(left.negate());

        playerControl.setWalkDirection(walkDir);
    }

    public Node getNode() {
        return playerNode;
    }

    public CharacterControl getPlayerControl() {
        return playerControl;
    }

    public Vector3f getPosition() {
        return playerControl.getPhysicsLocation();
    }

    public void shootRay() {
        // 1. Reset results list.
        CollisionResults results = new CollisionResults();
        // 2. Aim the ray from cam loc to cam direction.
        Ray ray = new Ray(cam.getLocation(), cam.getDirection());
        // 3. Collect intersections between Ray and Shootables in results list.
        shootables.collideWith(ray, results);
        // 4. Print the results
        System.out.println("----- Collisions? " + results.size() + "-----");
        for (int i = 0; i < results.size(); i++) {
            // For each hit, we know distance, impact point, name of geometry.
            float dist = results.getCollision(i).getDistance();
            Vector3f pt = results.getCollision(i).getContactPoint();
            String hit = results.getCollision(i).getGeometry().getName();
            System.out.println("* Collision #" + i);
            System.out.println("  You shot " + hit + " at " + pt + ", " + dist + " wu away.");
        }
        // 5. Use the results (we mark the hit object)
        if (results.size() > 0) {
            // The closest collision point is what was truly hit:
            CollisionResult closest = results.getClosestCollision();
            // Let's interact - we mark the hit with a red dot.
            mark.setLocalTranslation(closest.getContactPoint());
            rootNode.attachChild(mark);
        } else {
            // No hits? Then remove the red mark.
            rootNode.detachChild(mark);
        }
    }
}
