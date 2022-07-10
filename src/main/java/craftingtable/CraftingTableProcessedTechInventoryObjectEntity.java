package craftingtable;

import necesse.engine.Screen;
import necesse.entity.mobs.PlayerMob;
import necesse.gfx.gameTooltips.GameTooltips;
import necesse.gfx.gameTooltips.StringTooltips;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.InventoryRange;
import necesse.inventory.item.Item;
import necesse.inventory.recipe.Ingredient;
import necesse.inventory.recipe.Recipes;
import necesse.inventory.recipe.Tech;
import necesse.level.maps.Level;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public abstract class CraftingTableProcessedTechInventoryObjectEntity extends CraftingTableInventoryObjectEntity {
    public Tech[] techs;
    private int expectedCrafts;
    private ArrayList<InventoryItem> expectedResultItems;
    private final TechProcessingHelp help = new TechProcessingHelp();

    public CraftingTableProcessedTechInventoryObjectEntity(Level level, String type, int x, int y, int inputSlots, int outputSlots, Tech... techs) {
        super(level, type, x, y, inputSlots, outputSlots);
        this.techs = techs;
    }

    public boolean isValidInputItem(InventoryItem item) {
        return item != null;
    }

    protected void onSlotUpdate(int slot) {
        super.onSlotUpdate(slot);
        this.help.update = true;
        this.expectedResultItems = null;
    }

    public FutureCrafts getExpectedResults() {
        if (this.expectedResultItems == null) {
            this.expectedCrafts = 0;
            this.expectedResultItems = new ArrayList();
            Inventory copy = this.inventory.copy();

            boolean success = false;

            for (int i = this.inputSlots - 1; i >= 0; --i) {
                InventoryRange useRange = new InventoryRange(copy, i, this.inputSlots - 1);
                Iterator var6 = Recipes.getRecipes(this.techs).iterator();

                while (var6.hasNext()) {
                    CraftingTableShapedRecipe recipe = (CraftingTableShapedRecipe) var6.next();
                    if (recipe.canCraftRange(this.getLevel(), null, useRange).canCraft()) {
                        InventoryItem resultItem = recipe.resultItem.copy(recipe.resultAmount);
                        resultItem.stackToList(this.expectedResultItems);
                        recipe.craftRange(this.getLevel(), null, useRange);
                        ++this.expectedCrafts;
                        success = true;
                        break;
                    }
                }

                if (success) {
                    break;
                }
            }
        }

        ArrayList<InventoryItem> out = new ArrayList(this.expectedResultItems.size());
        Iterator var10 = this.expectedResultItems.iterator();

        while (var10.hasNext()) {
            InventoryItem expectedResult = (InventoryItem) var10.next();
            out.add(expectedResult.copy());
        }

        return new FutureCrafts(this.expectedCrafts, out);
    }

    public FutureCrafts getCurrentAndExpectedResults() {
        FutureCrafts combined = this.getExpectedResults();

        for (int i = this.inputSlots; i < this.inventory.getSize(); ++i) {
            InventoryItem item = this.inventory.getItem(i);
            if (item != null) {
                item.stackToList(combined.items);
            }
        }

        return combined;
    }


    public boolean processInput() {
        for (int i = this.inputSlots - 1; i >= 0; --i) {
            InventoryRange invRange = new InventoryRange(this.inventory, i, this.inputSlots - 1);
            Iterator var3 = Recipes.getRecipes(this.techs).iterator();

            while (var3.hasNext()) {
                CraftingTableShapedRecipe recipe = (CraftingTableShapedRecipe) var3.next();
                CanCraftCustomRecipe canCraft = (CanCraftCustomRecipe) recipe.canCraftRange(this.getLevel(), null, invRange);
                System.out.println("haveingredients" + canCraft.haveIngredients.length);
                System.out.println("cancraft" + canCraft.canCraft);
                if (canCraft.canCraft()) {
                    System.out.println(recipe.resultItem);
                    InventoryItem resultItem = recipe.resultItem.copy(recipe.resultAmount);
                    System.out.println(resultItem.getItemDisplayName());
                    if (this.canAddOutput(resultItem)) {
                        recipe.craftRange(this.getLevel(), null, invRange);

                        this.addOutput(resultItem);
                            return true;
                    }
                }
            }
        }

        return false;
    }

    public CraftingTableProcessingHelp getProcessingHelp() {
        return this.help;
    }

    public void onMouseHover(PlayerMob perspective, boolean debug) {
        super.onMouseHover(perspective, debug);
        if (debug) {
            FutureCrafts results = this.getCurrentAndExpectedResults();
            if (results.crafts > 0 || !results.items.isEmpty()) {
                StringTooltips tooltips = new StringTooltips("Expected results from " + results.crafts + " crafts:");
                Iterator var5 = results.items.iterator();

                while (var5.hasNext()) {
                    InventoryItem result = (InventoryItem) var5.next();
                    tooltips.add("  " + result.getAmount() + "x " + result.getItemDisplayName());
                }

                Screen.addTooltip(tooltips);
            }
        }

    }

    public static class FutureCrafts {
        public final int crafts;
        public final ArrayList<InventoryItem> items;

        public FutureCrafts(int crafts, ArrayList<InventoryItem> items) {
            this.crafts = crafts;
            this.items = items;
        }
    }

    public class TechProcessingHelp extends CraftingTableProcessingHelp {
        public boolean update;
        public CraftingTableShapedRecipe currentRecipe;
        public CanCraftCustomRecipe currentRecipeCanCraft;
        public HashSet<Integer> showRecipeTooltip;
        public HashMap<Integer, Ingredient> inputGhostItems;
        public HashMap<Integer, InventoryItem> outputGhostItems;

        private TechProcessingHelp() {
            this.update = true;
            this.showRecipeTooltip = new HashSet();
            this.inputGhostItems = new HashMap();
            this.outputGhostItems = new HashMap();
        }

        public void update() {
            this.update = false;
            this.currentRecipe = null;
            this.currentRecipeCanCraft = null;
            this.showRecipeTooltip.clear();
            this.inputGhostItems.clear();
            this.outputGhostItems.clear();
            InventoryRange inputRange = CraftingTableProcessedTechInventoryObjectEntity.this.getInputInventoryRange();

            int i;
            InventoryRange outputRange;
            for (i = inputRange.endSlot; i >= inputRange.startSlot; --i) {
                outputRange = new InventoryRange(CraftingTableProcessedTechInventoryObjectEntity.this.inventory, i, inputRange.endSlot);
                Iterator var4 = Recipes.getRecipes(CraftingTableProcessedTechInventoryObjectEntity.this.techs).iterator();

                while (var4.hasNext()) {
                    CraftingTableShapedRecipe recipe = (CraftingTableShapedRecipe) var4.next();
                    CanCraftCustomRecipe canCraftx = recipe.canCraftRange(CraftingTableProcessedTechInventoryObjectEntity.this.getLevel(), null, outputRange);
                    if (canCraftx.canCraft()) {
                        this.currentRecipe = recipe;
                        this.currentRecipeCanCraft = canCraftx;
                        break;
                    }
                }

                if (this.currentRecipe != null) {
                    break;
                }
            }

            if (this.currentRecipe == null) {
                Iterator var7 = Recipes.getRecipes(CraftingTableProcessedTechInventoryObjectEntity.this.techs).iterator();

                while (var7.hasNext()) {
                    CraftingTableShapedRecipe recipex = (CraftingTableShapedRecipe) var7.next();
                    CanCraftCustomRecipe canCraft = recipex.canCraftRange(CraftingTableProcessedTechInventoryObjectEntity.this.getLevel(), null, inputRange);
                    if (canCraft.hasAnyItems()) {
                        this.currentRecipe = recipex;
                        this.currentRecipeCanCraft = canCraft;
                        break;
                    }
                }
            }

            if (this.currentRecipe != null) {
                int j;
                InventoryItem item;
                if (!this.currentRecipeCanCraft.hasAnyOfAllItems()) {
                    for (i = 0; i < this.currentRecipe.ingredients.length; ++i) {
                        if (!this.currentRecipeCanCraft.hasAnyIngredients(i)) {
                            Ingredient ingredient = this.currentRecipe.ingredients[i];
                            boolean found = false;

                            for (j = inputRange.startSlot; j <= inputRange.endSlot; ++j) {
                                item = inputRange.inventory.getItem(j);
                                if (item != null && ingredient.matchesItem(item.item)) {
                                    this.inputGhostItems.put(j, ingredient);
                                    found = true;
                                    break;
                                }
                            }

                            if (!found) {
                                for (j = inputRange.startSlot; j <= inputRange.endSlot; ++j) {
                                    if (inputRange.inventory.isSlotClear(j)) {
                                        this.inputGhostItems.put(j, ingredient);
                                        this.showRecipeTooltip.add(j);
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }

                if (this.currentRecipe != null) {
                    InventoryItem resultItem = this.currentRecipe.resultItem.copy(this.currentRecipe.resultAmount);
                    outputRange = CraftingTableProcessedTechInventoryObjectEntity.this.getOutputInventoryRange();
                    Inventory inv = CraftingTableProcessedTechInventoryObjectEntity.this.inventory.copy();
                    inv.addItem(CraftingTableProcessedTechInventoryObjectEntity.this.getLevel(), null, resultItem, outputRange.startSlot, outputRange.endSlot, "add", true, false);

                    for (j = outputRange.startSlot; j <= outputRange.endSlot; ++j) {
                        item = inv.getItem(j);
                        if (item != null && item.equals(this.currentRecipe.resultItem.copy(this.currentRecipe.resultAmount), true)) {
                            this.outputGhostItems.put(j, this.currentRecipe.resultItem);
                            this.showRecipeTooltip.add(j);
                        }
                    }
                }
            }

        }

        public InventoryItem getGhostItem(int slot) {
            if (this.update) {
                this.update();
            }

            InventoryItem outputGhost = this.outputGhostItems.get(slot);
            if (outputGhost != null) {
                return outputGhost;
            } else {
                Ingredient ingredient = this.inputGhostItems.get(slot);
                if (ingredient != null) {
                    Item displayItem = ingredient.getDisplayItem();
                    if (displayItem != null) {
                        return displayItem.getDefaultItem(null, 1);
                    }
                }

                return null;
            }
        }

        public GameTooltips getTooltip(int slot) {
            if (this.update) {
                this.update();
            }

            return this.showRecipeTooltip.contains(slot) && this.currentRecipe != null ? this.currentRecipe.getTooltip(this.currentRecipeCanCraft.haveIngredients, null) : null;
        }

        public GameTooltips getCurrentRecipeTooltip() {
            if (this.update) {
                this.update();
            }

            return this.currentRecipe != null ? this.currentRecipe.getTooltip(this.currentRecipeCanCraft.haveIngredients, null) : null;
        }
    }
}
