package com.luxof.lapisworks.actions;

import at.petrak.hexcasting.api.casting.ParticleSpray;
import at.petrak.hexcasting.api.casting.castables.SpellAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.mishaps.MishapBadBlock;
import at.petrak.hexcasting.api.casting.mishaps.MishapBadEntity;
import at.petrak.hexcasting.api.misc.MediaConstants;

import com.luxof.lapisworks.VAULT.VAULT;
import com.luxof.lapisworks.blocks.entities.MindEntity;
import com.luxof.lapisworks.init.ModBlocks;
import com.luxof.lapisworks.init.Mutables.Mutables.SMindInfusions;
import com.luxof.lapisworks.interop.hierophantics.ChariotInterface;
import com.luxof.lapisworks.init.Mutables.SMindInfusion;
import com.luxof.lapisworks.mixinsupport.GetVAULT;
import com.luxof.lapisworks.nocarpaltunnel.HexIotaStack;
import com.luxof.lapisworks.nocarpaltunnel.SpellActionNCT;

import static com.luxof.lapisworks.Lapisworks.HIEROPHANTICS_INTEROP;
import static com.luxof.lapisworks.LapisworksIDs.ENTITY_INFUSEABLE_WITH_SMIND;
import static com.luxof.lapisworks.LapisworksIDs.FULL_SIMPLE_MIND;
import static com.luxof.lapisworks.LapisworksIDs.INFUSEABLE_WITH_SMIND;
import static com.luxof.lapisworks.LapisworksIDs.MIND_BLOCK;
import static com.luxof.lapisworks.MishapThrowerJava.throwIfEmpty;

import com.mojang.datafixers.util.Either;

import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;

import org.jetbrains.annotations.Nullable;

public class FlayArtMind extends SpellActionNCT {
    public int argc = 2;

    @Override
    public SpellAction.Result execute(HexIotaStack args, CastingEnvironment ctx) {
        Either<BlockPos, Entity> flayInto = args.getBlockPosOrEntity(0);
        BlockPos flayIntoPos = flayInto.left().orElse(null);
        Entity flayIntoEntity = flayInto.right().orElse(null);

        VAULT vault = ((GetVAULT)ctx).grabVAULT();
        // stop complaing about maybe not init fuckahh :pray:
        SMindInfusion infusionRecipe = new SMindInfusion();

        if (flayIntoPos != null) {

            if (HIEROPHANTICS_INTEROP) {
                Result result = ChariotInterface.tryImbueChariotMind(args, ctx);
                if (result != null) return result;
            }

            infusionRecipe = SMindInfusions
                .filterAll(flayIntoPos, ctx, args.stack, vault)
                .values().stream().findFirst()
                .orElseThrow(() -> new MishapBadBlock(flayIntoPos, INFUSEABLE_WITH_SMIND));
        }
        else if (flayIntoEntity != null) {
            infusionRecipe = SMindInfusions.filterAll(flayIntoEntity, ctx, args.stack, vault)
                .values().stream().findFirst()
                .orElseThrow(() -> new MishapBadEntity(flayIntoEntity, ENTITY_INFUSEABLE_WITH_SMIND));
        }

        infusionRecipe.mishapIfNeeded();

        // be funny. come on. try it.
        BlockPos mindPos = args.getBlockPos(1);
        MindEntity blockEntity = throwIfEmpty(
            ctx.getWorld().getBlockEntity(mindPos, ModBlocks.MIND_ENTITY_TYPE),
            new MishapBadBlock(mindPos, MIND_BLOCK)
        );
        if (blockEntity.mindCompletion < 100f) {
            throw new MishapBadBlock(mindPos, FULL_SIMPLE_MIND);
        }

        return new SpellAction.Result(
            new Spell(flayIntoPos, infusionRecipe, blockEntity),
            MediaConstants.CRYSTAL_UNIT,
            List.of(ParticleSpray.burst(ctx.mishapSprayPos(), 2, 15)),
            1
        );
    }

    public class Spell implements RenderedSpellNCT {
        public final BlockPos flayIntoPos;
        public final SMindInfusion flayer;
        public final MindEntity mind;

        public Spell(BlockPos flayIntoPos, SMindInfusion flayer, MindEntity mind) {
            this.flayIntoPos = flayIntoPos;
            this.flayer = flayer;
            this.mind = mind;
        }

		@Override
		public void cast(CastingEnvironment ctx) {
            mind.mindCompletion = 0f;
            mind.markDirty();
            ctx.getWorld().updateListeners(
                mind.getPos(),
                mind.getCachedState(),
                mind.getCachedState(),
                Block.NOTIFY_ALL
            );
            this.flayer.accept();
		}
    }

    @Nullable
    public static ServerPlayerEntity getPlayerOrNull(CastingEnvironment ctx) {
        return ctx.getCastingEntity() != null ? (ServerPlayerEntity)ctx.getCastingEntity() : null;
    }
}
