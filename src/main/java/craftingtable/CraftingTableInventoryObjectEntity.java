package craftingtable;

import necesse.engine.localization.message.GameMessage;
import necesse.engine.network.PacketReader;
import necesse.engine.network.PacketWriter;
import necesse.engine.save.LoadData;
import necesse.engine.save.SaveData;
import necesse.engine.save.levelData.InventorySave;
import necesse.entity.mobs.PlayerMob;
import necesse.entity.objectEntity.ObjectEntity;
import necesse.entity.objectEntity.interfaces.OEInventory;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.InventoryRange;
import necesse.level.maps.Level;

import java.util.ArrayList;

public abstract class CraftingTableInventoryObjectEntity extends ObjectEntity implements OEInventory {
    public final Inventory inventory;
    public final int inputSlots;
    public final int outputSlots;
    public boolean forceUpdate = true;
    protected boolean updateCanCraft;


    public CraftingTableInventoryObjectEntity(Level level, String type, int x, int y, int inputSlots, int outputSlots) {
        super(level, type, x, y);
        this.inputSlots = inputSlots;
        this.outputSlots = outputSlots;
        this.inventory = new Inventory(inputSlots + outputSlots) {
            public void updateSlot(int slot) {
                super.updateSlot(slot);
                CraftingTableInventoryObjectEntity.this.onSlotUpdate(slot);
            }
        };
        this.inventory.filter = (slot, item) -> {
            if (item == null) {
                return true;
            } else {
                return slot < inputSlots && this.isValidInputItem(item);
            }
        };
    }

    public void serverTick() {
        super.serverTick();
    }

    public void processCraft() {
        this.inventory.tickItems(this);
        for (int i = 0; i <= this.inventory.getSize() - 2; i++) {
            if (this.inventory.getItem(i) != null && this.inventory.getItem(i).equals(new InventoryItem("emptyitem", 1))) {
                this.inventory.setItem(i, null);
            }
        }
        if (this.processInput()) {
            this.forceUpdate = true;
        }
        this.serverTickInventorySync(this.getLevel().getServer(), this);
    }

    protected void onSlotUpdate(int slot) {
        this.forceUpdate = true;
    }

    public void addSaveData(SaveData save) {
        super.addSaveData(save);
        save.addSaveData(InventorySave.getSave(this.inventory));
    }

    public void applyLoadData(LoadData save) {
        super.applyLoadData(save);
        this.inventory.override(InventorySave.loadSave(save.getFirstLoadDataByName("INVENTORY")));
    }

    public void setupContentPacket(PacketWriter writer) {
        super.setupContentPacket(writer);
        this.inventory.writeContent(writer);
    }

    public void applyContentPacket(PacketReader reader) {
        super.applyContentPacket(reader);
        this.inventory.override(Inventory.getInventory(reader));
    }

    public ArrayList<InventoryItem> getDroppedItems() {
        ArrayList<InventoryItem> list = new ArrayList();

        for (int i = 0; i < this.inventory.getSize(); ++i) {
            if (!this.inventory.isSlotClear(i)) {
                list.add(this.inventory.getItem(i));
            }
        }

        return list;
    }

    public void markClean() {
        super.markClean();
        this.inventory.clean();
    }

    public abstract boolean isValidInputItem(InventoryItem var1);

    public abstract boolean processInput();

    public abstract CraftingTableProcessingHelp getProcessingHelp();

    public boolean canAddOutput(InventoryItem... outputItems) {
        Inventory copy = this.inventory.copy();
        InventoryItem[] var3 = outputItems;
        int var4 = outputItems.length;

        for (int var5 = 0; var5 < var4; ++var5) {
            InventoryItem outputItem = var3[var5];
            InventoryItem outputItemCopy = outputItem.copy();
            if (!copy.addItem(this.getLevel(), null, outputItemCopy, this.inputSlots, this.inventory.getSize() - 1, "add", true, false)) {
                return false;
            }

            if (outputItemCopy.getAmount() > 0) {
                return false;
            }
        }

        return true;
    }

    public boolean addOutput(InventoryItem... outputItems) {
        if (!this.canAddOutput(outputItems)) {
            return false;
        } else {
            InventoryItem[] var2 = outputItems;
            int var3 = outputItems.length;

            for (int var4 = 0; var4 < var3; ++var4) {
                InventoryItem outputItem = var2[var4];
                this.inventory.addItem(this.getLevel(), null, outputItem, this.inputSlots, this.inventory.getSize() - 1, "add", true, false);
            }

            return true;
        }
    }

    public Inventory getInventory() {
        return this.inventory;
    }

    public GameMessage getInventoryName() {
        return this.getObject().getLocalization();
    }

    public boolean canQuickStackInventory() {
        return false;
    }

    public boolean canRestockInventory() {
        return false;
    }

    public boolean canSortInventory() {
        return false;
    }

    public boolean canUseForNearbyCrafting() {
        return false;
    }

    public InventoryRange getSettlementStorage() {
        return null;
    }

    public InventoryRange getInputInventoryRange() {
        return new InventoryRange(this.inventory, 0, this.inputSlots - 1);
    }

    public InventoryRange getOutputInventoryRange() {
        return new InventoryRange(this.inventory, this.inputSlots, this.inventory.getSize() - 1);
    }

    public void onMouseHover(PlayerMob perspective, boolean debug) {
        super.onMouseHover(perspective, debug);
    }

}
