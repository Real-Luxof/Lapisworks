package com.luxof.lapisworks.BeegInfusions;

import at.petrak.hexcasting.api.casting.OperatorUtils;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment.HeldItemInfo;
import at.petrak.hexcasting.api.misc.MediaConstants;

import com.luxof.lapisworks.VAULT.Flags;
import com.luxof.lapisworks.init.Mutables.BeegInfusion;
import com.luxof.lapisworks.init.Mutables.Mutables;
import com.luxof.lapisworks.mishaps.MishapBadHandItem;
import com.luxof.lapisworks.mishaps.MishapNotEnoughItems;

import static com.luxof.lapisworks.LapisworksIDs.AMEL;
import static com.luxof.lapisworks.LapisworksIDs.ENCHBOOK_WITH_NOTONE_ENCH;
import static com.luxof.lapisworks.LapisworksIDs.ENCHBOOK_WITH_ONE_ENCH;
import static com.luxof.lapisworks.MishapThrowerJava.assertItemAmount;

import java.util.Map;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;

public class EnhanceEnchantedBook extends BeegInfusion {
    private int requiredAmel = 0;
    private ItemStack stack = null;
    private Hand hand = null;
    private int infusing = 0;

    @Override
    public boolean test() {
        boolean ret = false;
        for (HeldItemInfo heldInfo : this.heldInfos) {
            stack = heldInfo.stack();
            hand = heldInfo.hand();
            if (stack.isOf(Items.ENCHANTED_BOOK)) {
                ret = true;
                break;
            }
        }
        if (!ret) return false;
        requiredAmel = 20 * EnchantmentHelper.get(stack).values().iterator().next();
        infusing = Math.min(
            OperatorUtils.getPositiveInt(this.hexStack, 0, this.hexStack.size()),
            requiredAmel
        );
        return ret;
    }

    @Override
    public void mishapIfNeeded() {
        // this seems a bit problematic for any other enchanted book handlers..
        // open an issue or something if you don't want this first mishap here
        if (EnchantmentHelper.get(stack).values().size() != 1) {
            throw new MishapBadHandItem(
                stack,
                ENCHBOOK_WITH_ONE_ENCH,
                ENCHBOOK_WITH_NOTONE_ENCH,
                hand
            );
        } else if (infusing < requiredAmel)
            throw new MishapNotEnoughItems(AMEL, infusing, requiredAmel);
        assertItemAmount(ctx, Mutables::isAmel, AMEL, requiredAmel);
    }

    @Override
    public Long getCost() {
        return MediaConstants.CRYSTAL_UNIT * 5;
    }

    @Override
    public void accept() {
        vault.drain(Mutables::isAmel, requiredAmel, false, Flags.PRESET_UpToHotbar);

        Map<Enchantment, Integer> enchants = EnchantmentHelper.get(stack);
        Enchantment enchant = enchants.keySet().iterator().next();
        enchants.put(enchant, enchants.get(enchant) + 1);
        EnchantmentHelper.set(enchants, stack);
        ctx.replaceItem(
            stack -> true,
            stack,
            hand
        );
    }
}
