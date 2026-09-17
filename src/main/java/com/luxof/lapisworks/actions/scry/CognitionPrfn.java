package com.luxof.lapisworks.actions.scry;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.DoubleIota;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.mishaps.MishapBadBlock;

import com.luxof.lapisworks.blocks.entities.MindEntity;
import com.luxof.lapisworks.init.ModBlocks;
import com.luxof.lapisworks.nocarpaltunnel.ConstMediaActionNCT;
import com.luxof.lapisworks.nocarpaltunnel.HexIotaStack;

import static com.luxof.lapisworks.Lapisworks.prettifyDouble;
import static com.luxof.lapisworks.LapisworksIDs.MIND_BLOCK;
import static com.luxof.lapisworks.MishapThrowerJava.throwIfEmpty;

import java.util.List;

import net.minecraft.util.math.BlockPos;

public class CognitionPrfn extends ConstMediaActionNCT {
    public int argc = 1;
    public long mediaCost = 0L;
    @Override
    public List<Iota> execute(HexIotaStack stack, CastingEnvironment ctx) {
        BlockPos mindPos = stack.getBlockPos(0);
        ctx.assertPosInRange(mindPos);

        MindEntity blockEntity = throwIfEmpty(
            ctx.getWorld().getBlockEntity(mindPos, ModBlocks.MIND_ENTITY_TYPE),
            new MishapBadBlock(mindPos, MIND_BLOCK)
        );

        return List.of(new DoubleIota(prettifyDouble((double)blockEntity.mindCompletion)));
    }
}
