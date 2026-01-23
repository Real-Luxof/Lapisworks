package com.luxof.lapisworks.items.shit;

import java.util.function.Predicate;

import net.minecraft.item.ItemStack;

/** Makes this item accessible to the VAULT. */
public interface InventoryItem {
    /** Returns how much was drained.
     * <p>negative amount = take everything. */
    public int drain(ItemStack stack, Predicate<ItemStack> predicate, int amount, boolean simulate);
    /** Returns how much was given.
     * <p>negative amount = give as much as possible. */
    public int give(ItemStack stack, Predicate<ItemStack> predicate, int amount, boolean simulate);
}
