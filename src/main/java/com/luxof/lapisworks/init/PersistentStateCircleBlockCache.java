package com.luxof.lapisworks.init;

import com.luxof.lapisworks.Lapisworks;

import static com.luxof.lapisworks.Lapisworks.nbtListOf;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;

import net.minecraft.block.BlockState;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
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
        cache.entrySet().forEach(entry -> nbt.put(
            String.valueOf(entry.getKey().toLong()),
            nbtListOf(entry.getValue().stream().map(Lapisworks::serializeBlockPos).toList())
        ));

        return nbt;
    }
    
    public static PersistentStateCircleBlockCache readNbt(NbtCompound nbt) {
        PersistentStateCircleBlockCache state = new PersistentStateCircleBlockCache();

        nbt.getKeys().forEach(key -> state.cache.put(
            new ChunkPos(Long.valueOf(key)),
            new HashSet<>(
                nbt.getList(key, NbtElement.COMPOUND_TYPE).stream()
                    .map(Lapisworks::deserializeBlockPos)
                    .toList()
            )
        ));

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

    public boolean stopCachingChunk(ChunkPos cp) {
        return cache.remove(cp) != null;
    }

    public boolean cacheBlockPosInBulk(ChunkPos cp, List<BlockPos> poses) {
        return cache.computeIfAbsent(cp, any -> new HashSet<>()).addAll(poses);
    }
}
