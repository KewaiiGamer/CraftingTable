package craftingtable;

import necesse.engine.network.NetworkClient;
import necesse.engine.network.PacketReader;
import necesse.inventory.container.customAction.EmptyCustomAction;
import necesse.inventory.container.object.OEInventoryContainer;

public class CraftingTableOEInventoryContainer extends OEInventoryContainer {
    public final CraftingTableInventoryObjectEntity craftingTableObjectEntity;
    public EmptyCustomAction processCraft;

    public CraftingTableOEInventoryContainer(NetworkClient client, int uniqueSeed, CraftingTableInventoryObjectEntity objectEntity, PacketReader reader) {
        super(client, uniqueSeed, objectEntity, reader);
        this.craftingTableObjectEntity = objectEntity;
        processCraft = this.registerAction(new EmptyCustomAction() {
            protected void run() {
                ((CraftingTableInventoryObjectEntity) CraftingTableOEInventoryContainer.this.objectEntity).processCraft();
            }
        });
    }
}