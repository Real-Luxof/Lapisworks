package com.luxof.lapisworks.recipes.mold;

import com.luxof.lapisworks.inv.HandsInv;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.DynamicRegistryManager;

public interface IMoldRec extends Recipe<HandsInv> {
    public static class Type implements RecipeType<MoldRec> {
        private Type() {}
        public static final Type INSTANCE = new Type();
    }

    default RecipeType<?> getType() { return Type.INSTANCE; }
    default ItemStack craft(HandsInv inventory, DynamicRegistryManager registryManager) {
        return craft(inventory);
    }
    /** will not modify the inventory given or the item stacks within. */
    public ItemStack craft(HandsInv inventory);
}
