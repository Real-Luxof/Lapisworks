package com.luxof.lapisworks.actions.interact;

import at.petrak.hexcasting.api.casting.ParticleSpray;
import at.petrak.hexcasting.api.casting.castables.SpellAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.math.HexPattern;
import at.petrak.hexcasting.api.casting.mishaps.MishapBadBlock;
import at.petrak.hexcasting.api.misc.MediaConstants;

import com.luxof.lapisworks.blocks.entities.SimpleImpetusEntity;
import com.luxof.lapisworks.init.ModBlocks;
import com.luxof.lapisworks.nocarpaltunnel.HexIotaStack;
import com.luxof.lapisworks.nocarpaltunnel.SpellActionNCT;

import static com.luxof.lapisworks.LapisworksIDs.SIMP_IMP_BLOCK;
import static com.luxof.lapisworks.MishapThrowerJava.throwIfEmpty;

import java.util.List;

import net.minecraft.util.math.BlockPos;

public class TeachSImp extends SpellActionNCT {
    public int argc = 2;

    @Override
    public SpellAction.Result execute(HexIotaStack stack, CastingEnvironment ctx) {
        BlockPos pos = stack.getBlockPos(0);
        SimpleImpetusEntity blockEntity = throwIfEmpty(
            ctx.getWorld().getBlockEntity(pos, ModBlocks.SIMPLE_IMPETUS_ENTITY_TYPE),
            new MishapBadBlock(pos, SIMP_IMP_BLOCK)
        );

        return new SpellAction.Result(
            new Spell(blockEntity, stack.getPattern(1)),
            MediaConstants.DUST_UNIT * 2,
            List.of(ParticleSpray.burst(ctx.mishapSprayPos(), 2, 15)),
            1
        );
    }

    public class Spell implements RenderedSpellNCT {
        public final SimpleImpetusEntity blockEntity;
        public final HexPattern pattern;

        public Spell(SimpleImpetusEntity blockEntity, HexPattern pattern) {
            this.blockEntity = blockEntity;
            this.pattern = pattern;
        }

		@Override
		public void cast(CastingEnvironment ctx) {
            this.blockEntity.tune(pattern, true);
		}
    }
}
