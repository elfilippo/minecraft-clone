package com.minecraftclone.world;

import com.jme3.asset.AssetManager;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.renderer.Camera;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.minecraftclone.block.Block;
import com.minecraftclone.entitiy.EntityManager;
import com.minecraftclone.entitiy.PlayerCharacter;
import java.util.HashMap;
import java.util.Map;

public class World {

    private final Node rootNode;
    private final AssetManager assetManager;
    private final EntityManager entityManager;

    private final Map<String, Chunk> chunks = new HashMap<>();

    private Geometry geom;

    public World(Node rootNode, AssetManager assetManager, BulletAppState bulletAppState) {
        this.rootNode = rootNode;
        this.assetManager = assetManager;

        entityManager = new EntityManager(bulletAppState, rootNode);
    }

    public void tick(ActionInput input, Camera cam) {
        entityManager.tick(input, cam);
    }

    public void placeBlock(int x, int y, int z, Block block) {
        int chunkX = Math.floorDiv(x, Chunk.SIZE);
        int chunkY = Math.floorDiv(y, Chunk.SIZE);
        int chunkZ = Math.floorDiv(z, Chunk.SIZE);

        int localX = x % Chunk.SIZE;
        int localY = y % Chunk.SIZE;
        int localZ = z % Chunk.SIZE;

        String key = chunkX + "," + chunkY + "," + chunkZ;

        Chunk chunk = chunks.get(key);
        if (chunk == null) {
            chunk = new Chunk(chunkX, chunkY, chunkZ);
            chunks.put(key, chunk);
            rootNode.attachChild(chunk.getChunkNode());
        }

        Geometry geom = block.createGeometry(assetManager);

        geom.setLocalTranslation(x, y, z);

        chunk.setBlock(localX, localY, localZ, block, geom);
        this.geom = geom;
    }

    public void addCollision(BulletAppState bulletAppState) {
        CollisionShape shape = CollisionShapeFactory.createMeshShape(geom);
        RigidBodyControl body = new RigidBodyControl(shape, 0);
        geom.addControl(body);
        bulletAppState.getPhysicsSpace().add(body);
    }

    public PlayerCharacter getPlayerCharacter() {
        return entityManager.getPlayerCharacter();
    }
}
