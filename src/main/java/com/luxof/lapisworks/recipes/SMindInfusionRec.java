package com.luxof.lapisworks.recipes;

import com.luxof.lapisworks.VAULT.Flags;
import com.luxof.lapisworks.init.Mutables.SMindInfusion;
import com.luxof.lapisworks.inv.SMindInfusionSetupInv;

import static com.luxof.lapisworks.MishapThrowerJava.assertItemAmount;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.Recipe;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

public class SMindInfusionRec implements Recipe<SMindInfusionSetupInv> {
    private final Identifier id;
    private final Block from;
    private final Ingredient displayFromItem;
    private final Block to;
    private final Item displayToItem;
    private final List<IngredientWithCount> additionalCosts;
    public final boolean needsEnlightenment;
    /** make sure to call <code>.setup(...)</code>. */
    public final SMindInfusion innerInfusion;

    public static class InnerSMindInfusionClass extends SMindInfusion {
        public final SMindInfusionRec outer;
        public InnerSMindInfusionClass(SMindInfusionRec outer) {
            this.outer = outer;
        }

        @Override
        public boolean testBlock() {
            return outer.needsEnlightenment ? ctx.isEnlightened() : true
                && ctx.getWorld().getBlockState(this.blockPos).isOf(outer.getInput());
        }
        @Override
        public void mishapIfNeeded() {
            for (IngredientWithCount cost : outer.getAdditionalCosts()) {
                assertItemAmount(ctx, cost::test, cost.getName(), cost.getCount());
            }
        }
        @Override
        public void accept() {
            for (IngredientWithCount cost : outer.getAdditionalCosts()) {
                vault.drain(
                    cost::test,
                    cost.getCount(),
                    false,
                    Flags.PRESET_UpToHotbar
                );
            }
            ctx.getWorld().setBlockState(blockPos, outer.getOutput().getDefaultState());
        }
    }

    public SMindInfusionRec(
        Identifier id,
        Block from,
        Ingredient displayFromItem,
        Block to,
        Item displayToItem,
        List<IngredientWithCount> additionalCosts,
        boolean needsEnlightenment
    ) {
        this.id = id;
        this.from = from;
        this.displayFromItem = displayFromItem;
        this.to = to;
        this.displayToItem = displayToItem;
        this.additionalCosts = additionalCosts;
        this.innerInfusion = new InnerSMindInfusionClass(this);
        this.needsEnlightenment = needsEnlightenment;
    }
    public SMindInfusionRec(
        Identifier id,
        Block from,
        Ingredient displayFromItem,
        Block to,
        Item displayToItem,
        boolean needsEnlightenment
    ) {
        this(id, from, displayFromItem, to, displayToItem, List.of(), needsEnlightenment);
    }
    public SMindInfusionRec(
        Identifier id,
        Block from,
        Ingredient displayFromItem,
        Block to,
        Item displayToItem,
        List<IngredientWithCount> additionalCosts
    ) {
        this(
            id, from, displayFromItem, to, displayToItem, additionalCosts, false
        );
    }
    public SMindInfusionRec(
        Identifier id,
        Block from,
        Ingredient displayFromItem,
        Block to,
        Item displayToItem
    ) {
        this(id, from, displayFromItem, to, displayToItem, List.of(), false);
    }

    @Override public Identifier getId() { return id; }
    public Block getInput() { return from; }
    /** returns the input item that's displayed in the EMI window. */
    public Ingredient getDisplayInput() { return displayFromItem; }
    public Block getOutput() { return to; }
    /** returns the output item that's displayed in the EMI window. */
    public Item getDisplayOutput() { return displayToItem; }
    public List<IngredientWithCount> getAdditionalCosts() { return additionalCosts; }

    @Override
    public boolean matches(SMindInfusionSetupInv inventory, World world) {
        return inventory.isBlockInfusion && inventory.setUp(innerInfusion).testBlock();
    }

    public static class Type implements RecipeType<SMindInfusionRec> {
        private Type() {}
        public static Type INSTANCE = new Type();
    }
    /** Do not call. Returns empty stack. */
    @Override
    public ItemStack craft(SMindInfusionSetupInv inventory, DynamicRegistryManager registryManager) {
        return ItemStack.EMPTY.copy();
    }
    /** Do not call. Returns empty stack. */
    @Override
    public ItemStack getOutput(DynamicRegistryManager registryManager) {
        return ItemStack.EMPTY.copy();
    }
    @Override
    public boolean fits(int width, int height) {
        return true;
    }
    @Override
    public RecipeType<?> getType() {
        return Type.INSTANCE;
    }
    @Override
    public RecipeSerializer<?> getSerializer() {
        return SMindInfusionRecSerializer.INSTANCE;
    }
}
