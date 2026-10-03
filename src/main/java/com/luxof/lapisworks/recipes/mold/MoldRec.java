package com.luxof.lapisworks.recipes.mold;

import com.google.gson.JsonObject;

import com.luxof.lapisworks.inv.HandsInv;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class MoldRec implements IMoldRec {
    private final Identifier id;
    private final Ingredient input;
    private final Item output;

    public MoldRec(
        Identifier id,
        Ingredient input,
        Item output
    ) {
        this.id = id;
        this.input = input;
        this.output = output;
    }

    protected class MoldRecJsonFormat {
        JsonObject input;
        String output;
    }

    @Override
    public Identifier getId() { return this.id; }
    public Ingredient getInput() { return this.input; }
    public Item getOutput() { return this.output; }
    @Override
    public ItemStack getOutput(DynamicRegistryManager registryManager) {
        return new ItemStack(this.getOutput());
    }

    @Override
    public ItemStack craft(HandsInv inventory) {
        ItemStack oldStack = inventory.getHands().stream()
            .filter(stack -> this.input.test(stack))
            .findFirst()
            .get();
        ItemStack newStack = new ItemStack(
            this.getOutput(),
            oldStack.getCount()
        );
        newStack.setNbt(oldStack.getNbt());

        return newStack;
    }

    @Override
    public boolean matches(HandsInv inventory, World world) {
        return inventory.getHands().stream()
            .filter(stack -> this.input.test(stack))
            .findFirst()
            .isPresent();
    }

    @Override public boolean fits(int width, int height) { return true; }
    @Override public RecipeSerializer<?> getSerializer() { return MoldRecSerializer.INSTANCE; }

}
