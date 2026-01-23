package com.luxof.lapisworks.actions;

import at.petrak.hexcasting.api.casting.ParticleSpray;
import at.petrak.hexcasting.api.casting.castables.SpellAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment.HeldItemInfo;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.mishaps.MishapBadOffhandItem;
import at.petrak.hexcasting.api.misc.MediaConstants;

import com.luxof.lapisworks.VAULT.Flags;
import com.luxof.lapisworks.init.ModItems;
import com.luxof.lapisworks.nocarpaltunnel.SpellActionNCT;

import static com.luxof.lapisworks.MishapThrowerJava.assertItemAmount;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;

public class ImbueLap extends SpellActionNCT {
    public int argc = 0;

    private static boolean isLapis(ItemStack stack) {
        return stack.isOf(Items.LAPIS_LAZULI);
    }

    @Override
    public SpellAction.Result execute(List<? extends Iota> args, CastingEnvironment ctx) {
        HeldItemInfo heldStackInfo = ctx.getHeldItemToOperateOn(ImbueLap::isLapis);
        if (heldStackInfo == null)
            throw MishapBadOffhandItem.of(ItemStack.EMPTY.copy(), "lapis_lazuli");

        int count = heldStackInfo.stack().getCount();

        assertItemAmount(ctx, Items.AMETHYST_SHARD, count);

        return new SpellAction.Result(
            new Spell(count, heldStackInfo.hand()),
            MediaConstants.SHARD_UNIT * count,
            List.of(ParticleSpray.burst(ctx.mishapSprayPos(), 1, 10 + count)),
            1
        );
    }

    public class Spell implements RenderedSpellNCT {
        public final int count;
        public final Hand hand;

        public Spell(int count, Hand hand) { this.count = count; this.hand = hand; }

		@Override
		public void cast(CastingEnvironment ctx) {
            vault.drain(Items.AMETHYST_SHARD, this.count, false, Flags.PRESET_UpToHotbar);
            ctx.replaceItem(ImbueLap::isLapis, new ItemStack(ModItems.AMEL_ITEM, this.count), hand);
		}
    }
}
