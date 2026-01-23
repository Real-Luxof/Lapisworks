package com.luxof.lapisworks.VAULT;

import com.luxof.lapisworks.items.shit.InventoryItem;

import java.util.List;
import java.util.function.Predicate;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Identifier;

/** Very Ass and Unnecessary but not use-Less Terminal (for items)
 * <p>Fetches, drains, gives items.
 * <p>Dynamic. Does not need to save into NBT data or anything.
 * <p>Adding your own type support if you want (why would you?) is easy.
 * <p>Right now, only god and I know how this code works.
 * Soon, only god will know.
 */
public abstract class VAULT {

    public abstract Identifier getKindOfVault();

    protected abstract List<ItemStack> getTrinkets();
    protected abstract List<ItemStack> getHands();
    protected abstract List<ItemStack> getHotbar();
    protected abstract List<ItemStack> getInventory();

    private int handleDrain(
        ItemStack stack,
        Predicate<ItemStack> pred,
        int amount,
        boolean sim,
        boolean handleItemStacks,
        boolean handleInvItems
    ) {
        if (stack.getItem() instanceof InventoryItem invItem)
            return invItem.drain(stack, pred, amount, sim);

        if (pred.test(stack)) {
            int taken = Math.min(stack.getCount(), amount);
            if (!sim) {
                stack.decrement(taken);
            }
            return taken;
        }
        
        return 0;
    }
    private int handleGive(
        ItemStack stack,
        Predicate<ItemStack> pred,
        int amount,
        boolean sim,
        boolean handleItemStacks,
        boolean handleInvItems
    ) {
        if (handleInvItems && stack.getItem() instanceof InventoryItem invItem)
            return invItem.give(stack, pred, amount, sim);

        if (handleItemStacks && pred.test(stack)) {
            int canTake = stack.getMaxCount() - stack.getCount();
            int given = Math.min(canTake, amount);
            if (!sim) {
                stack.increment(given);
            }
            return given;
        }

        return 0;
    }


    /** Returns the amount that couldn't be drained. */
    public int drain(Predicate<ItemStack> itemPred, int amount, boolean sim, Flags flags) {
        int left = amount;
        for (ItemStack stack : getTrinkets()) {
            left -= handleDrain(
                stack,
                itemPred,
                amount,
                sim,
                flags.has(Flags.EQ_TRINKETS),
                flags.has(Flags.EQ_TRINKETS_INVITEM)
            );
            if (left == 0) return amount;
        }

        for (ItemStack stack : getHands()) {
            left -= handleDrain(
                stack,
                itemPred,
                amount,
                sim,
                flags.has(Flags.HANDS),
                flags.has(Flags.HANDS_INVITEM)
            );
            if (left == 0) return amount;
        }

        for (ItemStack stack : getHotbar()) {
            left -= handleDrain(
                stack,
                itemPred,
                amount,
                sim,
                flags.has(Flags.HOTBAR),
                flags.has(Flags.HOTBAR_INVITEM)
            );
            if (left == 0) return amount;
        }

        for (ItemStack stack : getInventory()) {
            left -= handleDrain(
                stack,
                itemPred,
                amount,
                sim,
                flags.has(Flags.HOTBAR),
                flags.has(Flags.HOTBAR_INVITEM)
            );
            if (left == 0) return amount;
        }

        return amount - left;
    }
    /** Returns the amount that couldn't be drained. */
    public int drain(Item item, int amount, boolean sim, Flags flags) {
        return this.drain(it -> it.getItem() == item, amount, sim, flags);
    }


    /** Returns the amount that couldn't be given. */
    public int give(Predicate<ItemStack> itemPred, int amount, boolean sim, Flags flags) {
        int left = amount;
        for (ItemStack stack : getTrinkets()) {
            left -= handleGive(
                stack,
                itemPred,
                amount,
                sim,
                flags.has(Flags.EQ_TRINKETS),
                flags.has(Flags.EQ_TRINKETS_INVITEM)
            );
            if (left == 0) return amount;
        }

        for (ItemStack stack : getHands()) {
            left -= handleGive(
                stack,
                itemPred,
                amount,
                sim,
                flags.has(Flags.HANDS),
                flags.has(Flags.HANDS_INVITEM)
            );
            if (left == 0) return amount;
        }

        for (ItemStack stack : getHotbar()) {
            left -= handleGive(
                stack,
                itemPred,
                amount,
                sim,
                flags.has(Flags.HOTBAR),
                flags.has(Flags.HOTBAR_INVITEM)
            );
            if (left == 0) return amount;
        }

        for (ItemStack stack : getInventory()) {
            left -= handleGive(
                stack,
                itemPred,
                amount,
                sim,
                flags.has(Flags.HOTBAR),
                flags.has(Flags.HOTBAR_INVITEM)
            );
            if (left == 0) return amount;
        }

        return amount - left;
    }
    /** Returns the amount that couldn't be given. */
    public int give(Item item, int amount, boolean sim, Flags flags) {
        return this.give(it -> it.getItem() == item, amount, sim, flags);
    }
}
