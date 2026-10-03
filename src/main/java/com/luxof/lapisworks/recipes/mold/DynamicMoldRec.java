package com.luxof.lapisworks.recipes.mold;

import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;

public abstract class DynamicMoldRec implements IMoldRec {
    public final Identifier id;

    public DynamicMoldRec(Identifier id) {
        this.id = id;
    }

    @Override 
    public Identifier getId() { return this.id; }

    @Override
    public boolean isIgnoredInRecipeBook() { return true; }

    @Override
    public ItemStack getOutput(DynamicRegistryManager registryManager) { return ItemStack.EMPTY; }

    @Override
    public RecipeType<?> getType() { return MoldRec.Type.INSTANCE; }

    @Override
    public boolean fits(int width, int height) { return true; }
}
