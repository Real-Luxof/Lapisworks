package com.luxof.lapisworks.mixin;

import at.petrak.hexcasting.api.block.circle.BlockCircleComponent;

import com.luxof.lapisworks.init.PersistentStateCircleBlockCache;
import com.luxof.lapisworks.media.LinkableMediaBlock;

import net.minecraft.block.BlockState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerWorld.class)
public class ServerWorldMixin {
    @Unique private ServerWorld world = (ServerWorld)(Object)this;

    @Inject(method = "onBlockChanged", at = @At("HEAD"))
    public void lapisworks$updateMyPersistentState(
        BlockPos pos, BlockState oldState, BlockState newState, CallbackInfo ci
    ) {
        if (oldState.isOf(newState.getBlock())) return;

        var persistentState = PersistentStateCircleBlockCache.getState(world);
        if (oldState.getBlock() instanceof BlockCircleComponent &&
            !(newState.getBlock() instanceof BlockCircleComponent)) {

            persistentState.stopCachingBlockPos(pos);
            persistentState.markDirty();

        } else if (!(oldState.getBlock() instanceof BlockCircleComponent) &&
            newState.getBlock() instanceof BlockCircleComponent) {

            persistentState.cacheBlockPos(pos);
            persistentState.markDirty();

        }

        if (
            !oldState.hasBlockEntity() ||
            !(world.getBlockEntity(pos) instanceof LinkableMediaBlock lmb)
        )
            return;
        
        lmb.getLinks().forEach(
            linkedPos -> ((LinkableMediaBlock)world.getBlockEntity(linkedPos)).removeLink(pos)
        );
    }
}
