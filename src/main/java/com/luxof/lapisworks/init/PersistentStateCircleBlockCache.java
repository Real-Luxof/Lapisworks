package com.luxof.lapisworks.init;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.PersistentState;

public class PersistentStateCircleBlockCache extends PersistentState {

    private HashMap<ChunkPos, HashSet<BlockPos>> cache = new HashMap<>();
    private static HashSet<Predicate<BlockState>> cachingPredicates = new HashSet<>();

    public static boolean registerCachingPredicate(Predicate<BlockState> predicate) {
        return cachingPredicates.add(predicate);
    }

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        for (var entry : cache.entrySet()) {
            if (entry.getValue().isEmpty()) continue;

            ChunkPos cp = entry.getKey();
            byte[] array = new byte[entry.getValue().size() * 4];
            int idx = 0;
            for (BlockPos pos : entry.getValue()) {
                array[idx] = (byte)(pos.getX() - cp.getStartX());
                array[idx + 1] = (byte)(pos.getY() << 24 >> 24);
                array[idx + 2] = (byte)(pos.getY() >> 8);
                array[idx + 3] = (byte)(pos.getZ() - cp.getStartZ());
                idx += 4;
            }

            nbt.putByteArray(String.valueOf(cp.toLong()), array);
        }

        return nbt;
    }
    
    public static PersistentStateCircleBlockCache readNbt(NbtCompound nbt) {
        PersistentStateCircleBlockCache state = new PersistentStateCircleBlockCache();

        for (String key : nbt.getKeys()) {
            ChunkPos cp = new ChunkPos(Long.valueOf(key));

            HashSet<BlockPos> posSet = new HashSet<>();
            byte[] array = nbt.getByteArray(key);
            for (int idx = 0; idx < array.length; idx += 4) {
                posSet.add(cp.getBlockPos(
                    array[idx], array[idx + 1] + array[idx + 2] << 8, array[idx + 3]
                ));
            }

            state.cache.put(
                cp,
                posSet
            );
        }

        return state;
    }

    public static PersistentStateCircleBlockCache getState(ServerWorld world) {
        return world.getPersistentStateManager().getOrCreate(
            PersistentStateCircleBlockCache::readNbt,
            () -> new PersistentStateCircleBlockCache(),
            "lapisworks_circleblockcache"
        );
    }

    public Set<BlockPos> getCachedBlocksInChunk(ChunkPos cp) {
        return cache.getOrDefault(cp, new HashSet<>());
    }

    public Set<BlockPos> getCachedBlocksInChunks(List<ChunkPos> cps) {
        HashSet<BlockPos> blocks = new HashSet<>();
        cps.forEach(cp -> { if (cache.containsKey(cp)) blocks.addAll(cache.get(cp)); });
        return blocks;
    }

    public boolean isBlockPosCached(BlockPos pos) {
        return cache.get(new ChunkPos(pos)).contains(pos);
    }

    public boolean cacheBlockPos(BlockPos pos) {
        return cache.computeIfAbsent(new ChunkPos(pos), cp -> new HashSet<>()).add(pos);
    }

    public boolean stopCachingBlockPos(BlockPos pos) {
        ChunkPos key = new ChunkPos(pos);
        if (!cache.containsKey(key)) return false;
        return cache.get(key).remove(pos);
    }
}
