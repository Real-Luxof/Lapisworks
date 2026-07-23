package com.luxof.lapisworks.actions.misc;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.mishaps.MishapBadBlock;
import at.petrak.hexcasting.api.mod.HexTags;

import com.luxof.lapisworks.nocarpaltunnel.HexIotaStack;
import com.luxof.lapisworks.nocarpaltunnel.SpellActionNCT;

import java.util.List;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;

public class EraseColor extends SpellActionNCT {
    public int argc = 1;

    public Result execute(HexIotaStack stack, CastingEnvironment ctx) {
        BlockPos pos = stack.getBlockPosInRange(0);
        BlockState state = world.getBlockState(pos);

        if (
            !state.isIn(HexTags.Blocks.CHEAP_TO_BREAK_BLOCK) &&
            !state.isOf(Blocks.LIGHT)
        )
            throw new MishapBadBlock(
                pos,
                Text.translatable("mishaps.lapisworks.descs.cheap_to_break_block")
            );

        return new Result(
            new Spell(pos),
            dust(0.01),
            List.of(),
            1L
        );
    }

    public static class Spell implements RenderedSpellNCT {
        final BlockPos pos;
        public Spell(BlockPos pos) { this.pos = pos; }

        public void cast(CastingEnvironment ctx) {
            ctx.getWorld().setBlockState(pos, Blocks.AIR.getDefaultState());
        }
    }
}
