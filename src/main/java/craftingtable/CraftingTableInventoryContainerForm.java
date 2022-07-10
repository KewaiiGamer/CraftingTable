package craftingtable;

import necesse.engine.localization.message.GameMessageBuilder;
import necesse.engine.network.client.Client;
import necesse.gfx.forms.components.FormInputSize;
import necesse.gfx.forms.components.containerSlot.FormContainerSlot;
import necesse.gfx.forms.components.localComponents.FormLocalTextButton;
import necesse.gfx.forms.presets.containerComponent.ContainerFormList;
import necesse.gfx.forms.presets.containerComponent.object.FuelContainerForm;
import necesse.gfx.forms.presets.containerComponent.object.OEInventoryContainerForm;
import necesse.gfx.ui.ButtonColor;

public class CraftingTableInventoryContainerForm extends ContainerFormList<CraftingTableOEInventoryContainer> {
    protected OEInventoryContainerForm<CraftingTableOEInventoryContainer> containerForm;
    protected FuelContainerForm fuelForm;
    private FormLocalTextButton craftButton;

    public CraftingTableInventoryContainerForm(Client client, CraftingTableOEInventoryContainer container) {
        super(client, container);
        int inputSlots = container.craftingTableObjectEntity.inputSlots;
        int outputSlots = container.craftingTableObjectEntity.outputSlots;

        this.containerForm = this.addComponent(new OEInventoryContainerForm<CraftingTableOEInventoryContainer>(client, container) {
            protected void addSlots() {
                this.slots = new FormContainerSlot[this.container.INVENTORY_END - this.container.INVENTORY_START + 1];
                int inputSlots = this.container.craftingTableObjectEntity.inputSlots;
                CraftingTableProcessingHelp craftingTableProcessingHelp = this.container.craftingTableObjectEntity.getProcessingHelp();
                int centerWidth = 40;
                craftButton = this.inventoryForm.addComponent(new FormLocalTextButton(new GameMessageBuilder().append("Craft"), 20, this.inventoryForm.getHeight() / 2, 40, FormInputSize.SIZE_24, ButtonColor.BASE));
                craftButton.onClicked(formButtonFormInputEvent -> {
                    container.processCraft.runAndSend();
                });


                for (int i = 0; i < this.slots.length; ++i) {
                    int slotIndex = i + this.container.INVENTORY_START;
                    int x;
                    int y;
                    int sideWidth;
                    if (i < inputSlots) {
                        sideWidth = 3 * 40;
                        x = this.inventoryForm.getWidth() / 2 - sideWidth - centerWidth / 2 + i % 3 * 40;
                        y = i / 3 * 40;
                    } else {
                        sideWidth = i - inputSlots;
                        x = this.inventoryForm.getWidth() / 2 + centerWidth / 2 + sideWidth % 4 * 40;
                        y = 40;
                    }

                    this.slots[i] = this.inventoryForm.addComponent(new KewFormContainerCraftingTableRecipeSlot(this.client, slotIndex, x, y + 30 + 4, craftingTableProcessingHelp));
                }

            }
        });
        this.containerForm.inventoryForm.setHeight(Math.max(KewOEInventoryContainerForm.getContainerHeight(inputSlots, 3), KewOEInventoryContainerForm.getContainerHeight(outputSlots, 3)));
        this.containerForm.inventoryForm.addComponent(new KewFormProcessingProgressArrow(this.containerForm.inventoryForm.getWidth() / 2 - 16, 30 + (this.containerForm.inventoryForm.getHeight() - 30) / 2 - 16, container.craftingTableObjectEntity.getProcessingHelp()));
        int var10005 = container.INVENTORY_START;
        int var10006 = container.INVENTORY_START - 1;
    }

    public void setDefaultPos() {
        this.containerForm.setDefaultPos();
    }

    public boolean shouldOpenInventory() {
        return true;
    }
}

