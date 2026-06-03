package com.luxof.lapisworks.mixin;

import com.luxof.lapisworks.Lapisworks;
import com.luxof.lapisworks.init.PersistentStateCircleBlockCache;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.ChunkSerializer;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.ProtoChunk;
import net.minecraft.world.poi.PointOfInterestStorage;

import static com.luxof.lapisworks.Lapisworks.nbtListOf;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ChunkSerializer.class)
public class ChunkSerializerMixin {
    @ModifyReturnValue(
        method = "serialize",
        at = @At("RETURN")
    )
    private static NbtCompound lapisworks$putInMyCachedCircleBlocks(
        NbtCompound ogNbt,
        ServerWorld world,
        Chunk chunk
    ) {
        ChunkPos cp = chunk.getPos();
        PersistentStateCircleBlockCache state = PersistentStateCircleBlockCache.getState(world);
        ogNbt.put(
            "cachedCircleBlocks",
            nbtListOf(
                state.getCachedBlocksInChunk(cp).stream().map(Lapisworks::serializeBlockPos).toList()
            )
        );
        return ogNbt;
    }

    @Inject(
        method = "deserialize",
        at = @At("HEAD")
    )
    private static void lapisworks$takeMyCachedCircleBlocksOut(
        ServerWorld world,
        PointOfInterestStorage poiStorage,
        ChunkPos cp,
        NbtCompound nbt,
        CallbackInfoReturnable<ProtoChunk> cir
    ) {
        PersistentStateCircleBlockCache state = PersistentStateCircleBlockCache.getState(world);
        state.cacheBlockPosInBulk(
            cp,
            nbt.getList("cachedCircleBlocks", NbtElement.COMPOUND_TYPE).stream()
                .map(Lapisworks::deserializeBlockPos)
                .toList()
        );
    }
}
