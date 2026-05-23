package com.luxof.lapisworks.mixin;

import com.luxof.lapisworks.media.LinkableMediaBlock;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractBlock.class)
public class AbstractBlockMixin {
    @Inject(method = "onStateReplaced", at = @At("HEAD"))
    public void lapisworks$removeDeadLinks(
        BlockState state, World world, BlockPos pos, BlockState newState, boolean moved, CallbackInfo ci
    ) {
        if (
            !state.hasBlockEntity() ||
            state.isOf(newState.getBlock()) ||
            !(world.getBlockEntity(pos) instanceof LinkableMediaBlock lmb)
        )
            return;

        lmb.getLinks().forEach(
            linkedPos -> ((LinkableMediaBlock)world.getBlockEntity(linkedPos)).removeLink(pos)
        );
    }
}
