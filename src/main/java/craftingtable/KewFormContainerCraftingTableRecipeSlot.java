package craftingtable;

import necesse.engine.network.client.Client;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.forms.components.containerSlot.FormContainerSlot;
import necesse.gfx.gameTexture.GameSprite;
import necesse.inventory.InventoryItem;

import java.awt.*;

public class KewFormContainerCraftingTableRecipeSlot extends FormContainerSlot {
    protected CraftingTableProcessingHelp help;

    public KewFormContainerCraftingTableRecipeSlot(Client client, int containerSlotIndex, int x, int y, CraftingTableProcessingHelp help) {
        super(client, containerSlotIndex, x, y);
        this.help = help;
    }

    public void drawDecal(PlayerMob perspective) {
        super.drawDecal(perspective);
        if (this.help != null) {
            InventoryItem item = this.getContainerSlot().getItem();
            if (item == null) {
                InventoryItem ghostItem = this.help.getGhostItem(this.getContainerSlot().getInventorySlot());
                if (ghostItem != null) {
                    GameSprite sprite = ghostItem.item.getItemSprite(ghostItem, perspective);
                    Color drawColor = ghostItem.item.getDrawColor(ghostItem, perspective);
                    sprite.initDraw().size(32).color(drawColor).alpha(0.25F).draw(this.getX() + 4, this.getY() + 4);
                }
            }
        }

    }
}
