package craftingtable;

import necesse.inventory.recipe.CanCraft;
import necesse.inventory.recipe.Ingredient;
import necesse.inventory.recipe.Recipe;

import java.util.ArrayList;

public class CanCraftCustomRecipe extends CanCraft {
    public final int[] haveIngredients;
    public int canCraft;
    public ArrayList<Ingredient> ingredients;
    public Ingredient[] customIngredients;
    private Recipe recipe;
    private int hasAnyItems;

    public CanCraftCustomRecipe(CraftingTableShapedRecipe shapedRecipe, Recipe recipe) {
        super(recipe);
        haveIngredients = new int[shapedRecipe.customIngredients.length];
        customIngredients = shapedRecipe.customIngredients;
    }

    public static CanCraftCustomRecipe allTrue(CraftingTableShapedRecipe r) {
        CanCraftCustomRecipe out = new CanCraftCustomRecipe(r, r);

        for (int i = 0; i < r.ingredients.length; ++i) {
            out.addIngredient(i, i, r.ingredients[i].getIngredientAmount());
        }

        return out;
    }

    public void addIngredient(int ingredientIndex, int slot, int amount) {
        if (ingredientIndex != slot) {
            return;
        }
        Ingredient ingredient = customIngredients[ingredientIndex];
        if (ingredient == null) {
            this.haveIngredients[ingredientIndex] = -1;
            ++this.canCraft;
            ++this.hasAnyItems;
        } else if (ingredient.getIngredientAmount() == 0) {
            if (this.haveIngredients[ingredientIndex] == 0) {
                this.haveIngredients[ingredientIndex] = -1;
                ++this.canCraft;
                ++this.hasAnyItems;
            }
        } else if (amount > 0) {
            if (this.haveIngredients[ingredientIndex] == 0) {
                ++this.hasAnyItems;
            }

            boolean haveEnoughBefore = this.haveIngredients[ingredientIndex] >= ingredient.getIngredientAmount();
            int[] var10000 = this.haveIngredients;
            var10000[ingredientIndex] += amount;
            boolean haveEnoughAfter = this.haveIngredients[ingredientIndex] >= ingredient.getIngredientAmount();
            if (!haveEnoughBefore && haveEnoughAfter) {
                ++this.canCraft;
            }
        }

    }

    public boolean hasAnyIngredients(int ingredientIndex) {
        Ingredient ingredient = customIngredients[ingredientIndex];
        if (ingredient != null)
            if (ingredient.getIngredientAmount() == 0) {
                return this.haveIngredients[ingredientIndex] == -1;
            } else {
                return this.haveIngredients[ingredientIndex] > 0;
            }
        return this.haveIngredients[ingredientIndex] == -1;
    }

    public boolean canCraft() {
        return this.canCraft >= this.haveIngredients.length;
    }

    public boolean hasAnyItems() {
        return this.hasAnyItems > 0;
    }

    public boolean hasAnyOfAllItems() {
        return this.hasAnyItems >= this.haveIngredients.length;
    }
}
