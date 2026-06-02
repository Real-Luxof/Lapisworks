package com.luxof.lapisworks.recipes;

import static com.luxof.lapisworks.init.ModItems.COLLAR;

import java.util.List;

import com.luxof.lapisworks.collar.LapisCollarAddition;
import com.luxof.lapisworks.collar.LapisCollarAdditions;

import net.minecraft.inventory.RecipeInputInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.recipe.SpecialCraftingRecipe;
import net.minecraft.recipe.book.CraftingRecipeCategory;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class CollarCombinationRecipe extends SpecialCraftingRecipe {

    public CollarCombinationRecipe(Identifier id) {
        super(id, CraftingRecipeCategory.MISC);
    }

    public static class Type implements RecipeType<CollarCombinationRecipe> {
        private Type() {}
        public static final Type INSTANCE = new Type();
    }

    @Override
    public ItemStack craft(RecipeInputInventory inventory, DynamicRegistryManager registryManager) {
        ItemStack base = null;

        for (ItemStack stack : inventory.getInputStacks()) {
            if (!stack.isOf(COLLAR)) continue;
            if (base == null)
                base = stack;
            else
                return ItemStack.EMPTY;
        }

        if (base == null) return ItemStack.EMPTY;

        List<Identifier> existingAdditions = COLLAR.getAdditions(base);

        for (ItemStack stack : inventory.getInputStacks()) {

            if (stack.isEmpty() || stack.isOf(COLLAR)) continue;
            Identifier additionId = null;
            LapisCollarAddition addition = null;

            for (var entry : LapisCollarAdditions.getAll().entrySet()) {
                Identifier id = entry.getKey();
                LapisCollarAddition testAddy = entry.getValue();
                if (
                    !testAddy.testItem(stack.getItem()) || !testAddy.canAdd(base, existingAdditions, id)
                ) continue;

                additionId = entry.getKey();
                addition = entry.getValue();
                break;
            }

            if (additionId == null || addition == null || existingAdditions.contains(additionId))
                return ItemStack.EMPTY;

            base = addition.craft(base, existingAdditions, stack, additionId);
            existingAdditions = COLLAR.getAdditions(base);
        }

        return base;
    }

    @Override
    public boolean matches(RecipeInputInventory inventory, World world) {
        ItemStack base = null;
        List<Identifier> existingAdditions = null;

        for (ItemStack stack : inventory.getInputStacks()) {
            if (!stack.isOf(COLLAR)) continue;
            if (base == null) {
                base = stack;
                existingAdditions = COLLAR.getAdditions(stack);
            } else
                return false;
        }
        if (base == null) return false;

        boolean additionFound = true;
        for (ItemStack stack : inventory.getInputStacks()) {
            for (var entry : LapisCollarAdditions.getAll().entrySet()) {
                Identifier id = entry.getKey();
                LapisCollarAddition addition = entry.getValue();

                additionFound = additionFound
                    && addition.testItem(stack.getItem())
                    && addition.canAdd(base, existingAdditions, id);

                if (additionFound) break;
            }
        }

        return additionFound;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return CollarCombinationRecipeSerializer.INSTANCE;
    }
    @Override
    public boolean fits(int width, int height) {
        return true;
    }
}
