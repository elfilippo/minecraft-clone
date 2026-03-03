package com.minecraftclone.render;

import com.jme3.asset.AssetManager;
import com.jme3.material.Material;
import com.jme3.math.Vector2f;
import com.jme3.texture.Image;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture2D;
import com.jme3.util.BufferUtils;
import com.minecraftclone.block.BlockRegistry;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * builds a single texture atlas from all block textures at startup.
 * texture list is sourced from BlockRegistry, which reads blocks.yml —
 * adding a new block and its textures to blocks.yml is all that's needed.
 * all chunk geometry uses atlas UVs, giving one draw call per chunk.
 */
public final class BlockAtlas {

    private static final Logger LOGGER = Logger.getLogger(BlockAtlas.class.getName());

    //IS: assumed size of each individual block texture in pixels
    private static final int TILE_SIZE = 16;

    //IS: map from texture name to its UV region in the atlas (bottom-left, top-right)
    private static final Map<String, Vector2f[]> UV_MAP = new HashMap<>();

    //IS: the single atlas material shared by all chunk geometries
    private static Material atlasMaterial;

    //IS: magenta fallback UV used when a texture is not in the atlas
    private static Vector2f[] fallbackUV;

    private static boolean built = false;

    /**
     * stitches all textures from BlockRegistry into one atlas image and creates
     * the shared material. must be called after BlockRegistry.load() and before
     * any chunk building starts.
     * @param assetManager
     */
    public static void build(AssetManager assetManager) {
        if (built) return;
        built = true;

        //DOES: collect all unique texture names registered by blocks.yml
        List<String> textures = new ArrayList<>(BlockRegistry.getAllTextures());
        int count = textures.size();

        //IS: number of tiles per row — smallest square grid that fits all textures
        int tilesPerRow = (int) Math.ceil(Math.sqrt(count));

        //IS: total atlas pixel dimensions
        int atlasSize = tilesPerRow * TILE_SIZE;

        LOGGER.info("BlockAtlas: stitching " + count + " textures into " + atlasSize + "x" + atlasSize + " atlas");

        //IS: atlas pixel buffer (RGBA, 4 bytes per pixel)
        ByteBuffer atlasBuffer = BufferUtils.createByteBuffer(atlasSize * atlasSize * 4);

        //DOES: fill atlas with transparent black initially
        for (int i = 0; i < atlasSize * atlasSize * 4; i++) {
            atlasBuffer.put((byte) 0);
        }

        //DOES: stitch each texture into the atlas and record its UV region
        for (int i = 0; i < count; i++) {
            String name = textures.get(i);

            //IS: tile grid position for this texture
            int tileCol = i % tilesPerRow;
            int tileRow = i / tilesPerRow;

            //IS: top-left pixel offset of this tile within the atlas
            int pixelX = tileCol * TILE_SIZE;
            int pixelY = tileRow * TILE_SIZE;

            boolean success = copyTexture(assetManager, name, atlasBuffer, pixelX, pixelY, atlasSize);

            if (!success) {
                //DOES: fill with magenta so missing textures are visually obvious
                fillMagenta(atlasBuffer, pixelX, pixelY, atlasSize);
                LOGGER.warning("BlockAtlas: missing texture '" + name + "' — using magenta placeholder");
            }

            //DOES: calculate UV coordinates for this tile in the atlas
            //INFO: JME UV origin is bottom-left, so V is flipped relative to pixel Y
            float u1 = (float) pixelX / atlasSize;
            float u2 = (float) (pixelX + TILE_SIZE) / atlasSize;
            float v1 = (float) pixelY / atlasSize;
            float v2 = (float) (pixelY + TILE_SIZE) / atlasSize;

            UV_MAP.put(name, new Vector2f[] { new Vector2f(u1, v1), new Vector2f(u2, v2) });
        }

        //DOES: store fallback UV pointing at the first tile
        //INFO: used when remap() is called with an unregistered texture name
        if (!UV_MAP.isEmpty()) {
            fallbackUV = UV_MAP.values().iterator().next();
        } else {
            fallbackUV = new Vector2f[] { new Vector2f(0, 0), new Vector2f(1, 1) };
        }

        atlasBuffer.rewind();

        //DOES: create JME image and texture from the assembled buffer
        Image atlasImage = new Image(
            Image.Format.RGBA8,
            atlasSize,
            atlasSize,
            atlasBuffer,
            com.jme3.texture.image.ColorSpace.sRGB
        );

        Texture2D atlasTexture = new Texture2D(atlasImage);

        //INFO: nearest mag filter preserves pixel art look; trilinear for mipmaps at distance
        //INFO: repeat wrap allows greedy mesh UVs to tile correctly across merged quads
        atlasTexture.setMagFilter(Texture.MagFilter.Nearest);
        atlasTexture.setMinFilter(Texture.MinFilter.Trilinear);
        atlasTexture.setWrap(Texture.WrapMode.EdgeClamp);

        //DOES: create the one shared material used by all chunk geometries
        atlasMaterial = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        atlasMaterial.setTexture("ColorMap", atlasTexture);
    }

    /**
     * returns the single atlas material shared by all chunk geometries.
     * @return atlas material
     */
    public static Material getMaterial() {
        if (atlasMaterial == null) throw new IllegalStateException(
            "BlockAtlas.build() must be called before getMaterial()"
        );
        return atlasMaterial;
    }

    /**
     * remaps a local UV coordinate into atlas UV space for a given texture.
     * localU and localV may exceed 1 for greedy mesh quads — this tiles the texture.
     * @param textureName texture name without .png extension
     * @param localU      local U coordinate (may be > 1 for tiled greedy quads)
     * @param localV      local V coordinate (may be > 1 for tiled greedy quads)
     * @return remapped atlas UV coordinate
     */
    public static Vector2f remap(String textureName, float localU, float localV) {
        Vector2f[] uv = UV_MAP.get(textureName);
        if (uv == null) {
            LOGGER.warning("BlockAtlas.remap(): texture not in atlas: " + textureName);
            uv = fallbackUV;
        }
        float atlasU = uv[0].x + localU * (uv[1].x - uv[0].x);
        float atlasV = uv[0].y + localV * (uv[1].y - uv[0].y);
        return new Vector2f(atlasU, atlasV);
    }

    /**
     * copies a block texture's pixels into the atlas buffer at the given tile offset.
     * scales the source image to TILE_SIZE x TILE_SIZE if needed.
     * @return true on success, false if the texture could not be loaded
     */
    private static boolean copyTexture(
        AssetManager assetManager,
        String name,
        ByteBuffer atlasBuffer,
        int pixelX,
        int pixelY,
        int atlasSize
    ) {
        try {
            //DOES: load via AWT so pixel format is always predictable ARGB regardless of source format
            java.io.InputStream is = BlockAtlas.class.getClassLoader().getResourceAsStream(
                "textures/block/" + name + ".png"
            );
            if (is == null) return false;

            java.awt.image.BufferedImage img = javax.imageio.ImageIO.read(is);
            if (img == null) return false;

            int srcWidth = img.getWidth();
            int srcHeight = img.getHeight();

            for (int row = 0; row < TILE_SIZE; row++) {
                for (int col = 0; col < TILE_SIZE; col++) {
                    //IS: source pixel scaled to TILE_SIZE if needed
                    int srcCol = (col * srcWidth) / TILE_SIZE;
                    int srcRow = (row * srcHeight) / TILE_SIZE;

                    //IS: AWT getRGB returns ARGB packed int
                    int argb = img.getRGB(srcCol, srcRow);
                    byte a = (byte) ((argb >> 24) & 0xFF);
                    byte r = (byte) ((argb >> 16) & 0xFF);
                    byte g = (byte) ((argb >> 8) & 0xFF);
                    byte b = (byte) (argb & 0xFF);

                    int dstIndex = ((pixelY + row) * atlasSize + (pixelX + col)) * 4;
                    atlasBuffer.put(dstIndex, r);
                    atlasBuffer.put(dstIndex + 1, g);
                    atlasBuffer.put(dstIndex + 2, b);
                    atlasBuffer.put(dstIndex + 3, a);
                }
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * fills a tile-sized region of the atlas buffer with solid magenta (255, 0, 255, 255).
     * used as a placeholder for missing textures so they are visually obvious.
     */
    private static void fillMagenta(ByteBuffer atlasBuffer, int pixelX, int pixelY, int atlasSize) {
        for (int row = 0; row < TILE_SIZE; row++) {
            for (int col = 0; col < TILE_SIZE; col++) {
                int dstIndex = ((pixelY + row) * atlasSize + (pixelX + col)) * 4;
                atlasBuffer.put(dstIndex, (byte) 255);
                atlasBuffer.put(dstIndex + 1, (byte) 0);
                atlasBuffer.put(dstIndex + 2, (byte) 255);
                atlasBuffer.put(dstIndex + 3, (byte) 255);
            }
        }
    }

    private BlockAtlas() {}
}
