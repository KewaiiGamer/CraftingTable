package craftingtable;

import necesse.level.maps.Level;

public class CraftingTableObjectEntity extends CraftingTableProcessedTechInventoryObjectEntity {

    public CraftingTableObjectEntity(Level level, int x, int y) {
        super(level, "craftingtable", x, y, 9, 1, Main.CRAFTING_TABLE_SHAPED);
    }
}