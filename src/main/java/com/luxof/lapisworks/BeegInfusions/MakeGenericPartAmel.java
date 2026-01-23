package com.luxof.lapisworks.BeegInfusions;

import at.petrak.hexcasting.api.casting.OperatorUtils;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment.HeldItemInfo;
import at.petrak.hexcasting.api.misc.MediaConstants;
import at.petrak.hexcasting.common.items.ItemStaff;

import com.luxof.lapisworks.VAULT.Flags;
import com.luxof.lapisworks.init.Mutables.BeegInfusion;
import com.luxof.lapisworks.init.Mutables.Mutables;
import com.luxof.lapisworks.interop.hextended.LapixtendedInterface;
import com.luxof.lapisworks.items.AmelStaff;
import com.luxof.lapisworks.items.shit.DurabilityPartAmel;

import static com.luxof.lapisworks.LapisworksIDs.AMEL;
import static com.luxof.lapisworks.MishapThrowerJava.assertItemAmount;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public class MakeGenericPartAmel extends BeegInfusion {
    private ItemStack stack = null;
    private Item item = null;
    private Hand hand = null;
    private int fullInfusionCost = 0;
    private int infusing = 0;

    @Override
    public boolean test() {
        boolean ret = false;

        for (HeldItemInfo heldInfo : this.heldInfos) {
            stack = heldInfo.stack();
            item = stack.getItem();
            hand = heldInfo.hand();
            if (item instanceof ItemStaff && !(item instanceof AmelStaff)) {
                ret = true;
                break;
            }
        }
        if (!ret) return false;


        fullInfusionCost = item instanceof DurabilityPartAmel durab ?
            stack.getMaxDamage() / durab.getAmelWorthInDurability()
            : Mutables.getCostForFullInfusionOfStaff((ItemStaff)item, ctx.getWorld());
        infusing = Math.min(
            OperatorUtils.getPositiveInt(hexStack, 0, hexStack.size()),
            fullInfusionCost
        );
        return ret;
    }

    @Override
    public void mishapIfNeeded() {
        assertItemAmount(ctx, Mutables::isAmel, AMEL, infusing);
    }

    @Override
    public Long getCost() { return MediaConstants.SHARD_UNIT * 2 * infusing; }

    @Override
    public void accept() {
        vault.drain(Mutables::isAmel, infusing, false, Flags.PRESET_UpToHotbar);

        DurabilityPartAmel partAmel = (DurabilityPartAmel)LapixtendedInterface.getAppropriatePartAmelGeneric(item);
        Item fullAmel = LapixtendedInterface.getAppropriateFullAmel(item);
        ItemStack newStaff;
        if (infusing == fullInfusionCost) { newStaff = new ItemStack(fullAmel); }
        else if (!(item instanceof DurabilityPartAmel)) {
            newStaff = new ItemStack((Item)partAmel);
            newStaff.setDamage(
                newStaff.getMaxDamage() - infusing * partAmel.getAmelWorthInDurability()
            );
        } else {
            newStaff = stack.copy();
            newStaff.setDamage(
                stack.getDamage() - infusing * partAmel.getAmelWorthInDurability()
            );
        }
        ctx.replaceItem(
            stack -> stack.getItem() instanceof ItemStaff, 
            newStaff,
            hand
        );
    }
}
