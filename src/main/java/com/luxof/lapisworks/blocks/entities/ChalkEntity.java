package com.luxof.lapisworks.blocks.entities;

import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;

import com.luxof.lapisworks.blocks.stuff.AttachedBE;
import com.luxof.lapisworks.chalk.RitualCastEnv;
import com.luxof.lapisworks.chalk.RitualComponent;
import com.luxof.lapisworks.init.ModBlocks;

import java.util.List;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import org.jetbrains.annotations.Nullable;

public class ChalkEntity extends BlockEntity implements AttachedBE, RitualComponent {
    public ChalkEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.CHALK_ENTITY_TYPE, pos, state);
    }

    /** no null. configuration when attachedTo =
     * <p>DOWN & UP: north, west, south, east.
     * <p>NORTH: up, west, down, east.
     * <p>SOUTH: down, west, up, east.
     * <p>WEST: north, up, south, down.
     * <p>EAST: north, down, south, up. */
    public boolean[] sidesAreChalk = { false, false, false, false };
    //public List<Boolean> sidesAreChalk = List.of(true, true, true, false);
    public Direction attachedTo = Direction.DOWN;
    public boolean powered = false;

    /** returns success. marks dirty. */
    public boolean setSideIsChalk(Direction dir, boolean state) {
        int idx = -1;
        if (attachedTo == Direction.DOWN) {
            switch (dir) {
                case NORTH -> idx = 0;
                case WEST -> idx = 1;
                case SOUTH -> idx = 2;
                case EAST -> idx = 3;
                default -> idx = -1;
            }
        } else if (attachedTo == Direction.UP) {
            switch (dir) {
                case SOUTH -> idx = 0;
                case WEST -> idx = 1;
                case NORTH -> idx = 2;
                case EAST -> idx = 3;
                default -> idx = -1;
            }
        } else if (attachedTo == Direction.NORTH) {
            switch (dir) {
                case UP -> idx = 0;
                case WEST -> idx = 1;
                case DOWN -> idx = 2;
                case EAST -> idx = 3;
                default -> idx = -1;
            }
        } else if (attachedTo == Direction.SOUTH) {
            switch (dir) {
                case DOWN -> idx = 0;
                case WEST -> idx = 1;
                case UP -> idx = 2;
                case EAST -> idx = 3;
                default -> idx = -1;
            }
        } else if (attachedTo == Direction.WEST) {
            switch (dir) {
                case NORTH -> idx = 0;
                case UP -> idx = 1;
                case SOUTH -> idx = 2;
                case DOWN -> idx = 3;
                default -> idx = -1;
            }
        } else if (attachedTo == Direction.EAST) {
            switch (dir) {
                case NORTH -> idx = 0;
                case DOWN -> idx = 1;
                case SOUTH -> idx = 2;
                case UP -> idx = 3;
                default -> idx = -1;
            }
        }
        if (idx == -1 || sidesAreChalk[idx] == state) return false;
        sidesAreChalk[idx] = state;
        this.markDirty();
        return true;
    }
    public boolean allSidesArentChalk() {
        for (boolean isSideChalk : sidesAreChalk) { if (isSideChalk) return true; }
        return false;
    }
    public boolean allSidesAreChalk() {
        for (boolean isSideChalk : sidesAreChalk) { if (!isSideChalk) return false; }
        return true;
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        int[] sides = nbt.getIntArray("sidesAreChalk");
        for (int i = 0; i < sidesAreChalk.length; i++) {
            sidesAreChalk[i] = sides[i] == 1;
        }

        powered = nbt.getBoolean("powered");
        attachedTo = Direction.byName(nbt.getString("attachedTo"));
    }
    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);

        int[] sides = {0, 0, 0, 0};
        for (int i = 0; i < sidesAreChalk.length; i++) {
            sides[i] = sidesAreChalk[i] ? 1 : 0;
        }

        nbt.putIntArray("sidesAreChalk", sides);

        nbt.putBoolean("powered", powered);
        nbt.putString("attachedTo", attachedTo.toString());
    }

    @Override @Nullable
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    public void save() {
        markDirty();
        BlockState state = world.getBlockState(pos);
        world.updateListeners(pos, state, state, 0);
    }


    @Override
    public Direction getAttachedTo() { return attachedTo; }
    @Override
    public Direction getParticleSprayDir() { return getAttachedTo().getOpposite(); }


    @Override
    public List<BlockPos> getPossibleNextBlocks(ServerWorld world, @Nullable Direction forward) {
        return getPossibleNextBlocksGeneric(world, forward, pos);
    }
    @Override
    public Pair<BlockPos, CastingImage> execute(RitualCastEnv env) {
        powered = true;
        save();

        return new Pair<>(
            getNextBlockDuringExecutionHelper(env),
            env.ritual().currentImage
        );
    }
    @Override
    public void unpower() {
        powered = false;
        save();
    }
    @Override
    public boolean executionCanFlowTo(ServerWorld world, BlockPos pos) {
        if (!(world.getBlockEntity(pos) instanceof AttachedBE chalk)) return true;
        return attachedTo == chalk.getAttachedTo();
    }
}
