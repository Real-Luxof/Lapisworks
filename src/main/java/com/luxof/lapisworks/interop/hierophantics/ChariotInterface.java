package com.luxof.lapisworks.interop.hierophantics;

import at.petrak.hexcasting.api.casting.ParticleSpray;
import at.petrak.hexcasting.api.casting.castables.SpellAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.mishaps.MishapBadBlock;
import at.petrak.hexcasting.api.misc.MediaConstants;

import com.luxof.lapisworks.init.ModBlocks;
import com.luxof.lapisworks.mixinsupport.ChariotServerPlayer;
import com.luxof.lapisworks.nocarpaltunnel.HexIotaStack;
import com.luxof.lapisworks.nocarpaltunnel.SpellActionNCT.RenderedSpellNCT;

import static com.luxof.lapisworks.MishapThrowerJava.throwIfEmpty;

import java.util.List;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import org.jetbrains.annotations.Nullable;

public class ChariotInterface {
    /** null = couldn't work. */
    @Nullable
    public static SpellAction.Result tryImbueChariotMind(
        HexIotaStack args,
        CastingEnvironment ctx
    ) {
        ServerWorld world = ctx.getWorld();
        BlockPos flayInto = args.getBlockPos(0);
        //LOGGER.info("flay into pos: " + flayInto.toString());
        //LOGGER.info("instance of flay bed? " + String.valueOf(world.getBlockEntity(flayInto) instanceof robotgiggle.hierophantics.blocks.FlayBedBlockEntity flayBed));
        if (
            !(world.getBlockEntity(flayInto) instanceof
            robotgiggle.hierophantics.blocks.FlayBedBlockEntity flayBed)
        )
            return null;
        var player = flayBed.getSleeper(world) instanceof ServerPlayerEntity plr
            ? plr
            : null;

        BlockPos flayFrom = args.getBlockPos(1);
        throwIfEmpty(
            world.getBlockEntity(flayFrom, Chariot.CHARIOT_MIND_ENTITY_TYPE),
            new MishapBadBlock(flayFrom, Chariot.CHARIOT_MIND.getName())
        );

        return new SpellAction.Result(
            new Spell(flayFrom, player),
            (long)(MediaConstants.CRYSTAL_UNIT * 5),
            List.of(ParticleSpray.burst(ctx.mishapSprayPos(), 10, 50)),
            1L
        );
    }

    public static class Spell implements RenderedSpellNCT {
        public final BlockPos flayFrom;
        public final ServerPlayerEntity flayInto;

        public Spell(BlockPos flayFrom, ServerPlayerEntity flayInto) {
            this.flayFrom = flayFrom;
            this.flayInto = flayInto;
        }

		@Override
		public void cast(CastingEnvironment ctx) {
            ServerWorld world = ctx.getWorld();

            var chariotMind = world.getBlockEntity(flayFrom, Chariot.CHARIOT_MIND_ENTITY_TYPE);
            if (flayInto != null)
                ((ChariotServerPlayer)flayInto).getFusedAmalgamations().add(
                    chariotMind.get().getAmalgamation(world)
                );
            world.setBlockState(flayFrom, ModBlocks.MIND_BLOCK.getDefaultState());
        }
    }
}
