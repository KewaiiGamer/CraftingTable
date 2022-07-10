package craftingtable;

import necesse.engine.localization.Localization;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.gfx.camera.GameCamera;
import necesse.gfx.gameTexture.GameTexture;
import necesse.inventory.InventoryItem;
import necesse.inventory.InventoryRange;
import necesse.inventory.container.object.CraftingStationContainer;
import necesse.inventory.item.Item;
import necesse.inventory.item.toolItem.ToolType;
import necesse.inventory.recipe.Recipe;
import necesse.inventory.recipe.Recipes;
import necesse.level.gameObject.CraftingStationObject;
import necesse.level.gameObject.ObjectHoverHitbox;
import necesse.level.maps.Level;

import java.awt.*;
import java.util.ArrayList;
import java.util.stream.Stream;

public class CraftingTableObject extends CraftingStationObject {
    public GameTexture texture;

    public CraftingTableObject() {
        super(new Rectangle(32, 32));
        this.mapColor = new Color(150, 50, 50);
        this.rarity = Item.Rarity.COMMON;
        this.drawDmg = false;
        this.toolType = ToolType.ALL;
    }

    @Override
    public void loadTextures() {
        super.loadTextures();
        this.texture = GameTexture.fromFile("craftingtable");
    }

    @Override

    public Rectangle getCollision(Level level, int x, int y, int rotation) {
        return rotation % 2 == 0 ? new Rectangle(x * 32 + 2, y * 32 + 6, 28, 20) : new Rectangle(x * 32 + 6, y * 32 + 2, 20, 28);
    }

    @Override
    public java.util.List<ObjectHoverHitbox> getHoverHitboxes(Level level, int tileX, int tileY) {
        java.util.List<ObjectHoverHitbox> list = super.getHoverHitboxes(level, tileX, tileY);
        list.add(new ObjectHoverHitbox(tileX, tileY, 0, -16, 32, 16));
        return list;
    }

    @Override
    public void drawPreview(Level level, int tileX, int tileY, int rotation, float alpha, PlayerMob player, GameCamera camera) {
        int drawX = camera.getTileDrawX(tileX);
        int drawY = camera.getTileDrawY(tileY);
        int spriteHeight = this.texture.getHeight() - 32;
        this.texture.initDraw().sprite(rotation % 4, 0, 32, spriteHeight).alpha(alpha).draw(drawX, drawY - (spriteHeight - 32));
    }

    @Override
    public ObjectEntity getNewObjectEntity(Level level, int x, int y) {
        return new CraftingTableObjectEntity(level, x, y);
    }

    @Override
    public String getInteractTip(Level level, int x, int y, PlayerMob perspective, boolean debug) {
        return Localization.translate("controls", "opentip");
    }

    @Override

    public boolean canInteract(Level level, int x, int y, PlayerMob player) {
        return true;
    }

    @Override
    public void interact(Level level, int x, int y, PlayerMob player) {
        if (level.isServerLevel()) {
            CraftingStationContainer.openAndSendContainer(Main.CRAFTING_TABLE_STATION_CONTAINER, player.getServerClient(), level, x, y);
        }

    }

    public CraftingTableObjectEntity getForgeObjectEntity(Level level, int tileX, int tileY) {
        ObjectEntity objectEntity = level.entityManager.getObjectEntity(tileX, tileY);
        return objectEntity instanceof CraftingTableObjectEntity ? (CraftingTableObjectEntity) objectEntity : null;
    }

    @Override
    public Stream<Recipe> streamSettlementRecipes(Level level, int tileX, int tileY) {
        CraftingTableObjectEntity forgeObjectEntity = this.getForgeObjectEntity(level, tileX, tileY);
        return forgeObjectEntity != null ? Recipes.streamRecipes(forgeObjectEntity.techs) : Stream.empty();
    }

    @Override
    public boolean canCurrentlyCraft(Level level, int tileX, int tileY, Recipe recipe) {
        CraftingTableObjectEntity forgeObjectEntity = this.getForgeObjectEntity(level, tileX, tileY);
        if (forgeObjectEntity == null) {
            return false;
        } else {
            return forgeObjectEntity.getExpectedResults().crafts < 10;
        }
    }

    @Override

    public InventoryRange getProcessingInputRange(Level level, int tileX, int tileY) {
        CraftingTableObjectEntity forgeObjectEntity = this.getForgeObjectEntity(level, tileX, tileY);
        return forgeObjectEntity != null ? forgeObjectEntity.getInputInventoryRange() : null;
    }

    @Override
    public InventoryRange getProcessingOutputRange(Level level, int tileX, int tileY) {
        CraftingTableObjectEntity forgeObjectEntity = this.getForgeObjectEntity(level, tileX, tileY);
        return forgeObjectEntity != null ? forgeObjectEntity.getOutputInventoryRange() : null;
    }

    @Override
    public ArrayList<InventoryItem> getCurrentAndFutureProcessingOutputs(Level level, int tileX, int tileY) {
        CraftingTableObjectEntity forgeObjectEntity = this.getForgeObjectEntity(level, tileX, tileY);
        return forgeObjectEntity != null ? forgeObjectEntity.getCurrentAndExpectedResults().items : new ArrayList();
    }

}