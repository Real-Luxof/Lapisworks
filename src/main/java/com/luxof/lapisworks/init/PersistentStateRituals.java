package com.luxof.lapisworks.init;

import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.IotaType;

import com.luxof.lapisworks.Lapisworks;
import com.luxof.lapisworks.chalk.OneTimeRitualExecutionState;

import static com.luxof.lapisworks.Lapisworks.nbtListOf;

import java.util.ArrayList;
import java.util.HashMap;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.world.PersistentState;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkSectionPos;

public class PersistentStateRituals extends PersistentState {
    public ArrayList<OneTimeRitualExecutionState> rituals = new ArrayList<>();
    public HashMap<IotaKey, ArrayList<BlockPos>> tuneables = new HashMap<>();

    public static final record IotaKey(Iota iota) {
        @Override
        public int hashCode() {
            return iota.hashCode();
        }

        @Override
        public boolean equals(Object other) {
            return other != null
                && other instanceof IotaKey otherIotaKey
                && Iota.tolerates(iota, otherIotaKey.iota);
        }
    }


    public ArrayList<OneTimeRitualExecutionState> getRituals() {
        return rituals;
    }

    public void addRitual(OneTimeRitualExecutionState ritual) {
        rituals.add(ritual);
    }


    public HashMap<IotaKey, ArrayList<BlockPos>> getTuneables() {
        return tuneables;
    }

    public ArrayList<BlockPos> getTuneables(Iota key) {
        return tuneables.get(new IotaKey(key));
    }

    public void addTuneable(Iota key, BlockPos pos) {
        tuneables.computeIfAbsent(new IotaKey(key), any -> new ArrayList<>())
            .add(pos);
    }

    public void addTuneables(Iota key, ArrayList<BlockPos> positions) {
        tuneables.computeIfAbsent(new IotaKey(key), any -> new ArrayList<>())
            .addAll(positions);
    }

    public void removeTuneable(Iota key, BlockPos pos) {
        IotaKey iotaKey = new IotaKey(key);
        if (!tuneables.containsKey(iotaKey)) return;

        ArrayList<BlockPos> positions = tuneables.get(iotaKey);
        positions.remove(pos);

        if (positions.size() == 0)
            tuneables.remove(iotaKey);
    }


    public void tick(ServerWorld world) {
        for (int i = rituals.size() - 1; i >= 0; i--) {
            OneTimeRitualExecutionState ritual = rituals.get(i);
            if (!ritual.tick(world)) {
                rituals.remove(i);
                markDirty();
            }
        }

        for (var entry : tuneables.entrySet()) {
            ArrayList<BlockPos> poses = entry.getValue();

            for (int i = poses.size() - 1; i >= 0; i--) {
                BlockPos pos = poses.get(i);

                if (
                    !world.isChunkLoaded(ChunkSectionPos.from(pos).asLong()) ||
                    world.getBlockState(pos).isOf(ModBlocks.TUNEABLE_AMETHYST)
                ) continue;

                poses.remove(i);
            }
        }
    }


    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {

        NbtCompound ritualsNbt = new NbtCompound();
        ritualsNbt.put("rituals", nbtListOf(rituals.stream().map(ritual -> ritual.save()).toList()));

        NbtCompound tuneablesNbt = new NbtCompound();
        tuneablesNbt.put(
            "tuneables",
            nbtListOf(tuneables.entrySet().stream()
                .filter(entry -> entry.getValue().size() > 0)
                .map(entry -> {
                    NbtCompound iota = IotaType.serialize(entry.getKey().iota);
                    NbtList poses = nbtListOf(
                        entry.getValue().stream().map(Lapisworks::serializeBlockPos).toList()
                    );

                    NbtCompound entryNbt = new NbtCompound();

                    entryNbt.put("iota", iota);
                    entryNbt.put("poses", poses);
                    return entryNbt;
                })
                .toList()
            )
        );

        nbt.put("rituals", ritualsNbt);
        nbt.put("tuneables", tuneablesNbt);

        return nbt;
    }

    public static PersistentStateRituals readNbt(NbtCompound nbt, ServerWorld world) {
        PersistentStateRituals state = new PersistentStateRituals();

        NbtList ritualsNbt = nbt.getList("rituals", NbtElement.COMPOUND_TYPE);

        state.rituals = new ArrayList<>(
            ritualsNbt.stream().map(
                ritual -> OneTimeRitualExecutionState.load((NbtCompound)ritual, world)
            ).toList()
        );

        NbtList tuneablesNbt = nbt.getList("tuneables", NbtElement.COMPOUND_TYPE);
        for (NbtElement _e : tuneablesNbt) {
            NbtCompound entry = (NbtCompound)_e;

            state.tuneables.put(
                new IotaKey(IotaType.deserialize(entry.getCompound("iota"), world)),
                new ArrayList<>(
                    entry.getList("poses", NbtElement.COMPOUND_TYPE).stream()
                        .map(Lapisworks::deserializeBlockPos)
                        .toList()
                )
            );
        }

        return state;
    }

    public static PersistentStateRituals getState(ServerWorld world) {
        return world.getPersistentStateManager().getOrCreate(
            nbt -> PersistentStateRituals.readNbt(nbt, world),
            PersistentStateRituals::new,
            "lapisworks_rituals"
        );
    }
}
