package com.minecraftclone.gui;

import com.jme3.asset.AssetManager;
import com.jme3.font.BitmapFont;
import com.jme3.font.BitmapText;
import com.jme3.scene.Node;
import com.jme3.texture.Texture2D;
import com.jme3.ui.Picture;
import com.minecraftclone.Main;
import com.minecraftclone.util.UIHelper;
import java.util.ArrayList;
import java.util.List;

class HUD {

    private AssetManager asset;
    private Picture hotbar, experienceBarEmpty, hotbarSelector, crosshair;
    private Node hotbarNode, containerNode, hungerNode, heartNode;

    private int scale, halfWidth, halfHeight;

    private List<Picture> hearts = new ArrayList<>(),
        hungerBars = new ArrayList<>();
    private List<Slot> hotbarSlots = new ArrayList<>();

    private Texture2D fullHeartTexture, halfHeartTexture, fullHungerTexture, halfHungerTexture, blankTexture;

    private int selectedSlot;

    HUD(Main main, int scale) {
        Node guiNode = main.getGuiNode();
        BitmapFont font = main.getguiFont();
        this.asset = main.getAssetManager();
        this.scale = scale;

        halfWidth = main.getCamera().getWidth() / 2;
        halfHeight = main.getCamera().getHeight() / 2;

        UIHelper uiHelper = new UIHelper(asset, scale, font);

        //DOES: Create Nodes for layering and attach them
        hotbarNode = new Node("hotbarNode");
        containerNode = new Node("containerNode");
        hungerNode = new Node("hungerNode");
        heartNode = new Node("heartNode");

        guiNode.attachChild(hotbarNode);
        hotbarNode.attachChild(containerNode);
        hotbarNode.attachChild(hungerNode);
        hotbarNode.attachChild(heartNode);

        //DOES: Create Texture variables
        Texture2D hotbarTexture = uiHelper.loadGUITexture2d("sprites/hud/hotbar"); //182x22
        Texture2D hotbarSelectorTexture = uiHelper.loadGUITexture2d("sprites/hud/hotbar_selection"); //24x23
        Texture2D crosshairTexture = uiHelper.loadGUITexture2d("sprites/hud/crosshair"); //15x15
        Texture2D experienceBarEmptyTexture = uiHelper.loadGUITexture2d("sprites/hud/experience_bar_background"); //182x5
        Texture2D heartContainerTexture = uiHelper.loadGUITexture2d("sprites/hud/heart/container"); //9x9
        Texture2D hungerContainerTexture = uiHelper.loadGUITexture2d("sprites/hud/food_empty"); //9x9

        fullHeartTexture = uiHelper.loadGUITexture2d("sprites/hud/heart/full"); //9x9
        halfHeartTexture = uiHelper.loadGUITexture2d("sprites/hud/heart/half"); //9x9
        fullHungerTexture = uiHelper.loadGUITexture2d("sprites/hud/food_full"); //9x9
        halfHungerTexture = uiHelper.loadGUITexture2d("sprites/hud/food_half"); //9x9
        blankTexture = uiHelper.loadGUITexture2d("blank"); //1x1

        //DOES: Create Pictures to display in the GUI, positions them and attaches them to nodes
        hotbar = uiHelper.createPicture(hotbarTexture, "hotbar");
        hotbarSelector = uiHelper.createPicture(hotbarSelectorTexture, "hotbarSelector");
        experienceBarEmpty = uiHelper.createPicture(experienceBarEmptyTexture, "experienceBarEmpty");
        crosshair = uiHelper.createPicture(crosshairTexture, "crosshair");

        hotbar.setPosition(halfWidth - (hotbar.getWidth() / 2), 0);
        hotbarSelector.setPosition(halfWidth - ((hotbarSelector.getWidth() / 2)), 0);
        experienceBarEmpty.setPosition(halfWidth - ((experienceBarEmpty.getWidth() / 2)), hotbar.getHeight() + scale * 2);
        crosshair.setPosition(halfWidth - ((crosshair.getWidth() / 2)), halfHeight - ((crosshair.getHeight() / 2)));

        hotbarNode.attachChild(hotbar);
        hotbarNode.attachChild(hotbarSelector);
        hotbarNode.attachChild(experienceBarEmpty);
        hotbarNode.attachChild(crosshair);

        //DOES: Creates Heart containers and empty textures on top of them to be replaced by heart textures to display the players life
        for (int i = 0; i < 10; i++) {
            Picture heartContainer = uiHelper.createPicture(heartContainerTexture, "heartContainer");
            heartContainer.setPosition(
                halfWidth - ((hotbar.getWidth() / 2)) + 8 * scale * i,
                experienceBarEmpty.getHeight() + scale * 4 + hotbar.getHeight()
            );
            containerNode.attachChild(heartContainer);

            Picture hungerContainer = uiHelper.createPicture(hungerContainerTexture, "hungerContainer");
            hungerContainer.setPosition(
                halfWidth + 10 * scale + 8 * scale * i,
                experienceBarEmpty.getHeight() + scale * 4 + hotbar.getHeight()
            );
            containerNode.attachChild(hungerContainer);

            Picture heart = uiHelper.createPicture(fullHeartTexture, "fullHeart");
            heart.setPosition(
                halfWidth - ((hotbar.getWidth() / 2)) + 8 * scale * i,
                experienceBarEmpty.getHeight() + scale * 4 + hotbar.getHeight()
            );
            hearts.add(heart);
            heartNode.attachChild(heart);

            Picture hunger = uiHelper.createPicture(fullHungerTexture, "hunger");
            hunger.setPosition(
                halfWidth + hotbar.getWidth() / 2 - 8 * scale * i - 9 * scale,
                experienceBarEmpty.getHeight() + scale * 4 + hotbar.getHeight()
            );
            hungerBars.add(hunger);
            hungerNode.attachChild(hunger);
        }

        //DOES: Creates empty textures and text on top of the Hotbar to display items placed there
        for (int i = 0; i < 9; i++) {
            /*
            Picture slot = textureManager.createPicture(blankTexture, "blank", 16 * scale); //Usage: Customscale needs to be multiplied by scale otherwise it breaks scalability
            slot.setPosition((halfWidth - (hotbar.getWidth()) / 2) + scale * (3 + 20 * i), 3 * scale);
            hotbarNode.attachChild(slot);
            hotbarList.add(slot);

            BitmapText text = new BitmapText(font);
            text.setLocalScale(fontScale);
            text.setLocalTranslation((halfWidth - (hotbar.getWidth()) / 2) + scale * (20 + 20 * i), 2 * scale, 0);

            hotbarNode.attachChild(text);
            //hotbarTextList.add(text);
            //hotbarTextAnchorList.add(text.getLocalTranslation().clone());
            */
            Slot slot = new Slot(uiHelper, (halfWidth - (hotbar.getWidth()) / 2) + scale * (3 + 20 * i), 3 * scale);
            slot.attach(hotbarNode);
            hotbarSlots.add(slot);
        }
    }

    /**
     * Changes the heart textures in order to display the players life. odd numbers make half hearts
     * @param life hearts that should be displayed in the HUD
     */
    void setLife(int life) {
        int fullHearts = life / 2;
        boolean hasHalfHeart = (life % 2 == 1);

        for (int i = 0; i < hearts.size(); i++) {
            Picture heart = hearts.get(i);

            if (i < fullHearts) {
                heart.setTexture(asset, fullHeartTexture, true);
            } else if (i == fullHearts && hasHalfHeart) {
                heart.setTexture(asset, halfHeartTexture, true);
            } else {
                heart.setTexture(asset, blankTexture, true);
            }
        }
    }

    /**
     * Changes the hunger bars textures in order to display the players hunger. odd numbers make half hearts
     * @param huger hunger bars that should be displayed in the HUD
     */
    void setHunger(int hunger) {
        //Does: Changes the hunger textures in order to display the players hunger odd numbers make half hunger bars
        int fullHunger = hunger / 2;
        boolean hasHalfHunger = (hunger % 2 == 1);

        for (int i = 0; i < hungerBars.size(); i++) {
            Picture hungerBar = hungerBars.get(i);

            if (i < fullHunger) {
                hungerBar.setTexture(asset, fullHungerTexture, true);
            } else if (i == fullHunger && hasHalfHunger) {
                hungerBar.setTexture(asset, halfHungerTexture, true);
            } else {
                hungerBar.setTexture(asset, blankTexture, true);
            }
        }
    }

    /**
     * Changes the displayed selected slot
     * @param slot Specifies the slot that should appear selected
     */
    void changeHotbarSelectedSlot(int slot) {
        //Does: Change the Hotbarslot based of the given int slot
        if (slot <= 9 && slot >= 1) {
            selectedSlot = slot;
            hotbarSelector.setPosition(
                halfWidth -
                    ((hotbar.getWidth() / 2) + 1 * scale) -
                    (hotbar.getWidth() - 2 * scale) / 9 +
                    (((hotbar.getWidth() - 2 * scale) / 9) * slot),
                0
            );
        }
    }

    /**
     * Updates the Hotbar items with the items displayed in the inventory
     * @param invPic List of all Item Pictures in the inventory
     * @param invText List of all Item Texts in the inventory
     */
    void updateHotbarDisplayItem(List<Picture> invPic, List<BitmapText> invText) {
        //Info: The items displayed in the Hotbar are copied from those in the inventoryList, because the inventory also has a Hotbar
        //Does: Checks for differences between the inventory hotbar and real hotbar and if they are not the same displays the item in the inventory hotbar in the hotbar
        for (int i = 0; i < 9; i++) {
            Slot slot = hotbarSlots.get(i);

            //slot.setMaterial(invPic.get(i).getMaterial());
            //slot.setTexture(asset, TextureManager.getItemTexture("golden_apple"));

            slot.setText(invText.get(i).getText());
            //text.setLocalTranslation(anchor.x - text.getLineWidth() * fontScale, anchor.y + text.getHeight() * fontScale, anchor.z);
        }
    }

    public int getSelectedSlot() {
        return selectedSlot;
    }
}
