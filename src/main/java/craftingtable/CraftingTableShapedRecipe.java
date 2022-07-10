package craftingtable;

import necesse.engine.network.gameNetworkData.GNDItemMap;
import necesse.engine.util.HashMapSet;
import necesse.engine.util.MapIterator;
import necesse.engine.util.ObjectValue;
import necesse.entity.mobs.PlayerMob;
import necesse.inventory.Inventory;
import necesse.inventory.InventoryItem;
import necesse.inventory.InventoryRange;
import necesse.inventory.item.Item;
import necesse.inventory.recipe.Ingredient;
import necesse.inventory.recipe.IngredientCounter;
import necesse.inventory.recipe.Recipe;
import necesse.inventory.recipe.Tech;
import necesse.level.maps.Level;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;

public class CraftingTableShapedRecipe extends Recipe {
    public Ingredient[] customIngredients;

    public CraftingTableShapedRecipe(String resultID, Tech tech, Ingredient[] ingredients) {
        this(resultID, 1, tech, ingredients);
    }

    public CraftingTableShapedRecipe(String resultID, int resultAmount, Tech tech, Ingredient[] ingredients) {
        this(resultID, resultAmount, tech, ingredients, false);
    }

    public CraftingTableShapedRecipe(String resultID, int resultAmount, Tech tech, Ingredient[] ingredients, boolean isHidden) {
        this(resultID, resultAmount, tech, ingredients, isHidden, null);
    }

    public CraftingTableShapedRecipe(String resultID, int resultAmount, Tech tech, Ingredient[] ingredients, boolean isHidden, GNDItemMap gndData) {
        super(resultID, resultAmount, tech, new Ingredient[0], isHidden, gndData);
        this.customIngredients = ingredients;
    }

    public CanCraftCustomRecipe canCraft(Level level, PlayerMob player, Inventory inv) {
        return this.canCraft(level, player, Collections.singletonList(inv));
    }

    public CanCraftCustomRecipe canCraft(Level level, PlayerMob player, Iterable<Inventory> invList) {
        return this.canCraftRange(level, player, () -> {
            return new MapIterator<>(invList.iterator(), InventoryRange::new);
        });
    }

    public CanCraftCustomRecipe canCraftRange(Level level, PlayerMob player, InventoryRange inv) {
        return this.canCraftRange(level, player, Collections.singletonList(inv));
    }

    public void countIngredientAmount(Inventory inventory, Level level, PlayerMob player, int startSlot, int endSlot, IngredientCounter handler) {
        for (int i = startSlot; i <= endSlot; ++i) {
            handler.handle(inventory, i, inventory.getItem(i));
        }

    }

    @Override
    public CanCraftCustomRecipe canCraftRange(Level level, PlayerMob player, Iterable<InventoryRange> invList) {
        CanCraftCustomRecipe out = new CanCraftCustomRecipe(this, this);
        HashMapSet<Inventory, Integer> usedSlots = new HashMapSet<>();
        LinkedList<ObjectValue<Integer, Ingredient>> sortedIngredients = new LinkedList<>();
        LinkedList<ObjectValue<Integer, Ingredient>> mustHaveIngredients = new LinkedList<>();

        for (int i = this.customIngredients.length - 1; i >= 0; --i) {
            Ingredient ingredient = this.customIngredients[i];
            if (ingredient != null) {
                if (ingredient.getIngredientAmount() == 0) {
                    mustHaveIngredients.add(new ObjectValue<>(i, ingredient));
                } else if (ingredient.isGlobalIngredient()) {
                    sortedIngredients.addLast(new ObjectValue<>(i, ingredient));
                } else {
                    sortedIngredients.addFirst(new ObjectValue<>(i, ingredient));
                }
            }
        }

        Iterator var10 = mustHaveIngredients.iterator();

        while (var10.hasNext()) {
            ObjectValue<Integer, Ingredient> e = (ObjectValue) var10.next();
            sortedIngredients.add(e);
        }

        var10 = invList.iterator();
        while (var10.hasNext()) {
            InventoryRange range = (InventoryRange) var10.next();
            if (range.inventory.canBeUsedForCrafting()) {
                countIngredientAmount(range.inventory, level, player, range.startSlot, range.endSlot, (inventory, slot, item) -> {
                    if (inventory.canBeUsedForCrafting()) {
                        if (!usedSlots.get(inventory).contains(slot)) {
                            int usedItems = 0;
                            Iterator<ObjectValue<Integer, Ingredient>> var7 = sortedIngredients.iterator();
                            for (int i = this.customIngredients.length - 1; i >= 0; --i) {
                                if (inventory.getItem(slot) == null || this.customIngredients[i] == null) {
                                    int itemsRemaining = usedItems;
                                    int foundItems = Math.min(itemsRemaining, 0);
                                    out.addIngredient(i, slot, foundItems);

                                    usedItems += foundItems;
                                } else if (this.customIngredients[i].matchesItem(inventory.getItem(slot).item)) {
                                    int itemsRemaining = item.getAmount() - usedItems;
                                    int foundItems = Math.min(itemsRemaining, this.customIngredients[i].getIngredientAmount());
                                    out.addIngredient(i, slot, foundItems);

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
        Ingredient[] var4 = this.customIngredients;
        int var5 = var4.length;

        Iterator<InventoryRange> var9 = invList.iterator();
        while (var9.hasNext()) {
            InventoryRange invRange = var9.next();
            if (invRange.inventory.canBeUsedForCrafting()) {
                for (int i = invRange.startSlot; i <= invRange.endSlot; i++) {
                    for (int var6 = 0; var6 < var5; ++var6) {
                        Ingredient in = var4[var6];
                        if (in != null && in.getIngredientAmount() > 0) {
                            int amountRemaining = in.getIngredientAmount();
                            Item itemSlot = invRange.inventory.getItemSlot(i);
                            InventoryItem item = invRange.inventory.getItem(i);
                            if (item != null && itemSlot != null) {
                                if (item.getAmount() == amountRemaining) {
                                    item.setAmount(0);
                                } else {
                                    item.setAmount(item.getAmount() - amountRemaining);
                                }
                                break;
                            }

                        }
                    }
                }
            }
        }

    }
}
