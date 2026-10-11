package com.luxof.lapisworks.actions;

import at.petrak.hexcasting.api.casting.ParticleSpray;
import at.petrak.hexcasting.api.casting.castables.SpellAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.mishaps.MishapBadOffhandItem;

import com.luxof.lapisworks.inv.HandsInv;
import com.luxof.lapisworks.mixinsupport.GetStacks;
import com.luxof.lapisworks.nocarpaltunnel.HexIotaStack;
import com.luxof.lapisworks.nocarpaltunnel.SpellActionNCT;
import com.luxof.lapisworks.recipes.mold.IMoldRec;

import static com.luxof.lapisworks.MishapThrowerJava.throwIfEmpty;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public class SwapAmel extends SpellActionNCT {
    public int argc = 0;

    @Override
    public SpellAction.Result execute(HexIotaStack args, CastingEnvironment ctx) {
        HandsInv inv = new HandsInv(((GetStacks)ctx).getHeldItemStacks());
        IMoldRec recipe = throwIfEmpty(

            ctx.getWorld().getRecipeManager().getFirstMatch(
                IMoldRec.Type.INSTANCE,
                inv,
                ctx.getWorld()
            ),

            new MishapBadOffhandItem(
                ItemStack.EMPTY.copy(),
                Text.translatable("mishaps.lapisworks.descs.moldable")
            )

        );

        ItemStack swapWith = recipe.craft(inv);
        ItemStack swapFrom = ((GetStacks)ctx).getHeldItemStacksOtherFirst().stream()
            .filter(recipe::matches)
            .findFirst()
            .get();

        return new SpellAction.Result(
            new Spell(swapWith, swapFrom),
            0,
            List.of(ParticleSpray.burst(
                ctx.mishapSprayPos(),
                1,
                10 + (swapWith.getCount() + swapFrom.getCount()) / 2
            )),
            1
        );
    }

    public class Spell implements RenderedSpellNCT {
        public final ItemStack swapTo;
        public final ItemStack swapFrom;

        public Spell(ItemStack swapTo, ItemStack swapFrom) {
            this.swapTo = swapTo;
            this.swapFrom = swapFrom;
        }

		@Override
		public void cast(CastingEnvironment ctx) {
            ctx.replaceItem(
                stack -> stack == swapFrom,
                swapTo,
                null
            );
		}
    }
}
