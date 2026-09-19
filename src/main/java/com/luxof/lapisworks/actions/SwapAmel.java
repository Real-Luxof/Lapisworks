package com.luxof.lapisworks.actions;

import at.petrak.hexcasting.api.casting.ParticleSpray;
import at.petrak.hexcasting.api.casting.castables.SpellAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment.HeldItemInfo;
import at.petrak.hexcasting.api.casting.mishaps.MishapBadOffhandItem;

import com.luxof.lapisworks.inv.HandsInv;
import com.luxof.lapisworks.mixinsupport.GetStacks;
import com.luxof.lapisworks.nocarpaltunnel.HexIotaStack;
import com.luxof.lapisworks.nocarpaltunnel.SpellActionNCT;
import com.luxof.lapisworks.recipes.MoldRec;

import static com.luxof.lapisworks.MishapThrowerJava.throwIfEmpty;

import java.util.List;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;

public class SwapAmel extends SpellActionNCT {
    public int argc = 0;

    @Override
    public SpellAction.Result execute(HexIotaStack args, CastingEnvironment ctx) {
        MoldRec recipe = throwIfEmpty(

            ctx.getWorld().getRecipeManager().getFirstMatch(
                MoldRec.Type.INSTANCE,
                new HandsInv(((GetStacks)ctx).getHeldItemStacks()),
                ctx.getWorld()
            ),

            new MishapBadOffhandItem(
                ItemStack.EMPTY.copy(),
                Text.translatable("mishaps.lapisworks.descs.moldable")
            )

        );

        Item swapWith = recipe.getOutput();
        HeldItemInfo inputItems = ctx.getHeldItemToOperateOn(stack -> recipe.getInput().test(stack));
        int count = inputItems.stack().getCount();
        Hand hand = inputItems.hand();

        return new SpellAction.Result(
            new Spell(swapWith, count, hand),
            0,
            List.of(ParticleSpray.burst(ctx.mishapSprayPos(), 1, 10 + count)),
            1
        );
    }

    public class Spell implements RenderedSpellNCT {
        public final Item item;
        public final int count;
        public final Hand hand;

        public Spell(Item item, int count, Hand hand) {
            this.item = item;
            this.count = count;
            this.hand = hand;
        }

		@Override
		public void cast(CastingEnvironment ctx) {
            ctx.replaceItem(
                stack -> true,
                new ItemStack(this.item, this.count),
                this.hand
            );
		}
    }
}
