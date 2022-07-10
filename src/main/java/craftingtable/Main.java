package craftingtable;

import necesse.engine.modLoader.annotations.ModEntry;
import necesse.engine.network.PacketReader;
import necesse.engine.registries.ContainerRegistry;
import necesse.engine.registries.ItemRegistry;
import necesse.engine.registries.ObjectRegistry;
import necesse.engine.registries.RecipeTechRegistry;
import necesse.inventory.item.Item;
import necesse.inventory.recipe.Ingredient;
import necesse.inventory.recipe.Recipes;
import necesse.inventory.recipe.Tech;

@ModEntry
public class Main {
    public static int CRAFTING_TABLE_STATION_CONTAINER;

    public static Tech CRAFTING_TABLE_SHAPELESS;
    public static Tech CRAFTING_TABLE_SHAPED;

    public void init() {
        CRAFTING_TABLE_SHAPELESS = RecipeTechRegistry.registerTech("craftingtable_shapeless");
        CRAFTING_TABLE_SHAPED = RecipeTechRegistry.registerTech("craftingtable_shaped");
        ObjectRegistry.registerObject("kew_craftingtable", new CraftingTableObject(), 2.0F, true);

        CRAFTING_TABLE_STATION_CONTAINER = ContainerRegistry.registerOEContainer((client, uniqueSeed, oe, content) -> {
            return new CraftingTableInventoryContainerForm(client, new CraftingTableOEInventoryContainer(client.getClient(), uniqueSeed, (CraftingTableInventoryObjectEntity) oe, new PacketReader(content)));
        }, (client, uniqueSeed, oe, content, serverObject) -> {
            return new CraftingTableOEInventoryContainer(client, uniqueSeed, (CraftingTableInventoryObjectEntity) oe, new PacketReader(content));
        });
        ItemRegistry.registerItem("emptyitem", new Item(1), 0.0F, false);
        /*Recipes.registerModRecipe(new CraftingTableShapelessRecipe(
                "ironbar",
                1,
                Main.CRAFTING_TABLE_SHAPELESS,
                new Ingredient[]{
                        new Ingredient("ironore", 1),
                        new Ingredient("copperore", 1)
                }
        ));*/
    }

    public void initResources() {
    }

    public void postInit() {
        Recipes.registerModRecipe(new CraftingTableShapedRecipe(
                "ironbar",
                1,
                Main.CRAFTING_TABLE_SHAPED,
                new Ingredient[]{
                        null, new Ingredient("copperore", 1), new Ingredient("ironore", 1),
                        null, new Ingredient("copperore", 1), new Ingredient("ironore", 1),
                        null, new Ingredient("copperore", 1), new Ingredient("ironore", 1),
                }
        ));
    }

}
