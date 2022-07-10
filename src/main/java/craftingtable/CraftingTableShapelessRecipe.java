package craftingtable;

import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.util.HashMapSet;
import necesse.engine.util.MapIterator;
import necesse.engine.util.ObjectValue;
import necesse.entity.mobs.PlayerMob;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryRange;
import necesse.inventory.recipe.CanCraft;
import necesse.inventory.recipe.Ingredient;
import necesse.inventory.recipe.Recipe;
import necesse.inventory.recipe.Tech;
import necesse.level.maps.Level;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

public class CraftingTableShapelessRecipe extends Recipe {
    public CraftingTableShapelessRecipe(String resultID, int resultAmount, Tech tech, Ingredient[] ingredients) {
        super(resultID, resultAmount, tech, ingredients);
    }

    public CraftingTableShapelessRecipe(String resultID, int resultAmount, Tech tech, Ingredient[] ingredients, boolean isHidden) {
        super(resultID, resultAmount, tech, ingredients, isHidden);
    }

    public CraftingTableShapelessRecipe(String resultID, int resultAmount, Tech tech, Ingredient[] ingredients, boolean isHidden, GNDItemMap gndData) {
        super(resultID, resultAmount, tech, ingredients, isHidden, gndData);
    }

    public CraftingTableShapelessRecipe(String resultID, Tech tech, Ingredient[] ingredients) {
        super(resultID, tech, ingredients);
    }

    public CanCraft canCraft(Level level, PlayerMob player, Inventory inv) {
        return this.canCraft(level, player, Collections.singletonList(inv));
    }

    public CanCraft canCraft(Level level, PlayerMob player, Iterable<Inventory> invList) {
        return this.canCraftRange(level, player, () -> {
            return new MapIterator<>(invList.iterator(), InventoryRange::new);
        });
    }

    public CanCraft canCraftRange(Level level, PlayerMob player, InventoryRange inv) {
        return this.canCraftRange(level, player, Collections.singletonList(inv));
    }

    public CanCraft canCraftRange(Level level, PlayerMob player, Iterable<InventoryRange> invList) {
        CanCraft out = new CanCraft(this);
        HashMapSet<Inventory, Integer> usedSlots = new HashMapSet<>();
        LinkedList<ObjectValue<Integer, Ingredient>> sortedIngredients = new LinkedList<>();
        LinkedList<ObjectValue<Integer, Ingredient>> mustHaveIngredients = new LinkedList<>();

        for (int i = this.ingredients.length - 1; i >= 0; --i) {
            Ingredient ingredient = this.ingredients[i];
            if (ingredient.getIngredientAmount() == 0) {
                mustHaveIngredients.add(new ObjectValue<>(i, ingredient));
            } else if (ingredient.isGlobalIngredient()) {
                sortedIngredients.addLast(new ObjectValue<>(i, ingredient));
            } else {
                sortedIngredients.addFirst(new ObjectValue<>(i, ingredient));
            }
        }

        Iterator var10 = mustHaveIngredients.iterator();

        while (var10.hasNext()) {
            ObjectValue<Integer, Ingredient> e = (ObjectValue) var10.next();
            sortedIngredients.addFirst(e);
        }

        var10 = invList.iterator();

        while (var10.hasNext()) {
            InventoryRange range = (InventoryRange) var10.next();
            if (range.inventory.canBeUsedForCrafting()) {
                range.inventory.countIngredientAmount(level, player, range.startSlot, range.endSlot, (inventory, slot, item) -> {
                    if (inventory.canBeUsedForCrafting()) {
                        System.out.println(item.getItemDisplayName());
                        if (!usedSlots.get(inventory).contains(slot)) {
                            int usedItems = 0;
                            Iterator<ObjectValue<Integer, Ingredient>> var7 = sortedIngredients.iterator();

                            while (var7.hasNext()) {
                                ObjectValue<Integer, Ingredient> e = var7.next();
                                int index = e.object;
                                Ingredient ingredient = e.value;
                                if (ingredient.matchesItem(item.item)) {
                                    int itemsRemaining = item.getAmount() - usedItems;
                                    int foundItems = Math.min(itemsRemaining, ingredient.getIngredientAmount());
                                    out.addIngredient(index, foundItems);
                                    usedItems += foundItems;
                                    if (usedItems >= item.getAmount()) {
                                        break;
                                    }
                                }
                            }

                            usedSlots.add(inventory, slot);
                        }
                    }
                });
            }
        }

        return out;
    }

    public void craft(Level level, PlayerMob player, Inventory inv) {
        this.craft(level, player, Collections.singletonList(inv));
    }

    public void craft(Level level, PlayerMob player, Iterable<Inventory> invList) {
        this.craftRange(level, player, () -> {
            return new MapIterator<>(invList.iterator(), InventoryRange::new);
        });
    }

    public void craftRange(Level level, PlayerMob player, InventoryRange inv) {
        this.craftRange(level, player, Collections.singletonList(inv));
    }

    public void craftRange(Level level, PlayerMob player, Iterable<InventoryRange> invList) {
        Ingredient[] var4 = this.ingredients;
        int var5 = var4.length;

        for (int var6 = 0; var6 < var5; ++var6) {
            Ingredient in = var4[var6];
            if (in.getIngredientAmount() > 0) {
                int amountRemaining = in.getIngredientAmount();
                Iterator<InventoryRange> var9 = invList.iterator();

                while (var9.hasNext()) {
                    InventoryRange invRange = var9.next();
                    if (invRange.inventory.canBeUsedForCrafting()) {
                        if (amountRemaining <= 0) {
                            break;
                        }

                        int removed = invRange.inventory.removeItems(level, player, in, amountRemaining, invRange.startSlot, invRange.endSlot);
                        amountRemaining -= removed;
                    }
                }
            }
        }

    }
}
