package com.luxof.lapisworks.actions.interact;

import at.petrak.hexcasting.api.casting.ParticleSpray;
import at.petrak.hexcasting.api.casting.castables.SpellAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.mishaps.MishapBadOffhandItem;
import at.petrak.hexcasting.api.misc.MediaConstants;
import at.petrak.hexcasting.common.items.magic.ItemMediaBattery;
import at.petrak.hexcasting.common.lib.HexItems;

import com.google.common.collect.ImmutableSet;
import com.luxof.lapisworks.nocarpaltunnel.HexIotaStack;
import com.luxof.lapisworks.nocarpaltunnel.SpellActionNCT;

import static com.luxof.lapisworks.Lapisworks.fullLinkableMediaBlocksInteraction;
import static com.luxof.lapisworks.Lapisworks.interactWithLinkableMediaBlocks;
import static com.luxof.lapisworks.MishapThrowerJava.assertLinkableThere;
import static com.luxof.lapisworks.MishapThrowerJava.throwIfNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;

public class Withdraw extends SpellActionNCT {
    public int argc = 2;
    public int getArgc() {
        return 2;
    }

    @Override
    public Result execute(HexIotaStack hexStack, CastingEnvironment ctx) {
        BlockPos pos = hexStack.getBlockPosInRange(0);
        long amount = (long)(hexStack.getPositiveDouble(1) * MediaConstants.DUST_UNIT);

        assertLinkableThere(pos, ctx);

        ItemStack phialStack = throwIfNull(
            ctx.getHeldItemToOperateOn(stack -> stack.isOf(HexItems.BATTERY)),
            new MishapBadOffhandItem(
                ItemStack.EMPTY.copy(),
                Text.translatable("mishaps.lapisworks.descs.phial")
            )
        ).stack();
        ItemMediaBattery phial = (ItemMediaBattery)phialStack.getItem();

        Pair<Long, Set<BlockPos>> interactSimResult = fullLinkableMediaBlocksInteraction(
            ctx.getWorld(),
            Set.of(pos),
            amount,
            false,
            true
        );
        long realAmount = Math.min(
            interactSimResult.getLeft(),
            phial.getMaxMedia(phialStack) - phial.getMedia(phialStack)
        );

        List<ParticleSpray> particles = new ArrayList<>(List.of(
            ParticleSpray.cloud(ctx.mishapSprayPos(), 3, 20)
        ));
        particles.addAll(interactSimResult.getRight().stream().map(
            position -> ParticleSpray.cloud(position.toCenterPos(), 3, 10)
        ).toList());

        return new SpellAction.Result(
            new Spell(pos, phialStack, realAmount),
            (long)(realAmount * 0.1),
            particles,
            1
        );
    }

    public class Spell implements RenderedSpellNCT {
        public final BlockPos pos;
        public final ItemStack phialStack;
        public final long amount;

        public Spell(BlockPos pos, ItemStack phialStack, long amount) {
            this.pos = pos;
            this.phialStack = phialStack;
            this.amount = amount;
        }

		@Override
		public void cast(CastingEnvironment ctx) {
            long used = ((ItemMediaBattery)phialStack.getItem()).insertMedia(
                phialStack, amount, true
            );
            long withdrawing = interactWithLinkableMediaBlocks(
                ctx.getWorld(),
                ImmutableSet.of(pos),
                used,
                false
            );
            ((ItemMediaBattery)phialStack.getItem()).insertMedia(
                phialStack,
                withdrawing,
                false
            );
		}
    }
}
