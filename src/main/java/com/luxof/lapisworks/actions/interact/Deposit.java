package com.luxof.lapisworks.actions.interact;

import at.petrak.hexcasting.api.casting.ParticleSpray;
import at.petrak.hexcasting.api.casting.castables.SpellAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.misc.MediaConstants;

import com.google.common.collect.ImmutableSet;
import com.luxof.lapisworks.nocarpaltunnel.HexIotaStack;
import com.luxof.lapisworks.nocarpaltunnel.SpellActionNCT;

import static com.luxof.lapisworks.Lapisworks.fullLinkableMediaBlocksInteraction;
import static com.luxof.lapisworks.Lapisworks.interactWithLinkableMediaBlocks;
import static com.luxof.lapisworks.MishapThrowerJava.assertLinkableThere;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;

public class Deposit extends SpellActionNCT {
    public int getArgc() {
        return 2;
    }

    @Override
    public Result execute(HexIotaStack stack, CastingEnvironment ctx) {
        BlockPos pos = stack.getBlockPosInRange(0);
        long amount = (long)(stack.getPositiveDouble(1) * MediaConstants.DUST_UNIT);

        assertLinkableThere(pos, ctx);

        Pair<Long, Set<BlockPos>> interactSimResult = fullLinkableMediaBlocksInteraction(
            ctx.getWorld(),
            Set.of(pos),
            amount,
            true,
            true
        );
        long realAmount = interactSimResult.getLeft();

        List<ParticleSpray> particles = new ArrayList<>(List.of(
            ParticleSpray.cloud(ctx.mishapSprayPos(), 3, 20)
        ));
        particles.addAll(interactSimResult.getRight().stream().map(
            position -> ParticleSpray.cloud(position.toCenterPos(), 3, 10)
        ).toList());

        return new SpellAction.Result(
            new Spell(pos, realAmount),
            realAmount + (long)(realAmount * 0.1),
            particles,
            1
        );
    }

    public class Spell implements RenderedSpellNCT {
        public final BlockPos pos;
        public final long amount;

        public Spell(BlockPos pos, long amount) {
            this.pos = pos;
            this.amount = amount;
        }

		@Override
		public void cast(CastingEnvironment ctx) {
            interactWithLinkableMediaBlocks(
                ctx.getWorld(),
                ImmutableSet.of(pos),
                amount,
                true
            );
		}
    }
}
