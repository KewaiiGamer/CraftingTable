package craftingtable;

import necesse.engine.network.client.Client;
import necesse.gfx.forms.presets.containerComponent.object.OEInventoryContainerForm;
import necesse.inventory.container.object.OEInventoryContainer;

public class KewOEInventoryContainerForm extends OEInventoryContainerForm {

    protected KewOEInventoryContainerForm(Client client, OEInventoryContainer container, int height) {
        super(client, container, height);
    }


    public static int getContainerHeight(int inventorySize, int columns) {
        return (inventorySize + columns - 1) / columns * 40 + 30 + 8;
    }

}
