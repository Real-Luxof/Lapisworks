package com.luxof.lapisworks.blocks.entities;

import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.IotaType;
import at.petrak.hexcasting.api.casting.iota.NullIota;
import at.petrak.hexcasting.api.misc.MediaConstants;

import com.luxof.lapisworks.blocks.TuneableAmethyst;
import com.luxof.lapisworks.init.ModBlocks;
import com.luxof.lapisworks.init.PersistentStateRituals;
import com.luxof.lapisworks.media.MediaTransferInterface;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import org.jetbrains.annotations.Nullable;

public class TuneableAmethystEntity extends BlockEntity implements MediaTransferInterface {
    public TuneableAmethystEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.TUNEABLE_AMETHYST_ENTITY_TYPE, pos, state);
    }

    public long media = 0L;
    @Nullable private Iota tunedFrequency = null;
    @Nullable private NbtCompound tunedNbt = null;

    public final double minAmbit = 2.0;
    public final double ambitCap = 16;
    private final double ambitCapSqr = ambitCap*ambitCap;
    private final double minAmbitSqr = minAmbit*minAmbit;

    public final long mediaCap = (long)(MediaConstants.DUST_UNIT * ambitCapSqr);

    public double getMediaInDust() { return (double)media / (double)MediaConstants.DUST_UNIT; }
    public double getAmbit() { return Math.max(minAmbit, Math.sqrt(getMediaInDust())); }
    public double getAmbitSqr() { return Math.max(minAmbitSqr, getMediaInDust()); }

    /** to clear, you can also pass in a NullIota.
     * <p>Server-only method. Throws if on client. */
    public void tune(@Nullable Iota frequency) {
        if (world.isClient) throw new IllegalStateException("Server-only method.");
        PersistentStateRituals state = PersistentStateRituals.getState((ServerWorld)world);

        if (tunedFrequency != null) {
            if (frequency != null && Iota.tolerates(tunedFrequency, frequency)) return;

            state.removeTuneable(tunedFrequency, pos);
        }

        tunedFrequency = frequency instanceof NullIota ? null : frequency;
        tunedNbt = frequency instanceof NullIota ? null : IotaType.serialize(frequency);
        if (tunedFrequency != null)
            state.addTuneable(tunedFrequency, pos);
        save();
    }
    public Iota getTunedFrequency() { return tunedFrequency; }

    /** Used usually on client where there is no way to deserialize an iota. */
    @Nullable
    public Text getTunedFrequencyDisplay() {
        return tunedNbt != null ? IotaType.getDisplay(tunedNbt) : null;
    }

    @SuppressWarnings("deprecation")
    public void updateState() {
        BlockState state = world.getBlockState(pos);
        int filled = Math.min(3, (int)Math.floor(media / (mediaCap / 3)));

        if (filled == state.get(TuneableAmethyst.STAGE)) return;
        BlockState newState = state.with(TuneableAmethyst.STAGE, filled);
        world.setBlockState(pos, newState);
        setCachedState(newState);
    }

    public void save() {
        markDirty();
        world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_LISTENERS);
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putLong("media", media);
        if (tunedFrequency != null) {
            tunedNbt = IotaType.serialize(tunedFrequency);
            nbt.put("frequency", tunedNbt);
        } else if (tunedNbt != null)
            nbt.put("frequency", tunedNbt);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        media = nbt.getLong("media");
        if (nbt.contains("frequency")) {
            tunedNbt = nbt.getCompound("frequency");
            if (world instanceof ServerWorld sw)
                tunedFrequency = IotaType.deserialize(nbt, sw);
        } else {
            tunedFrequency = null;
            tunedNbt = null;
        }
    }

    @Override @Nullable public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this); }
    @Override public NbtCompound toInitialChunkDataNbt() { return createNbt(); }

    @Override @Nullable public Vec3d getPosIfPossible() { return pos.toCenterPos(); }
    @Override public long getMediaHere() { return media; }
    @Override public void setMediaHere(long media) { this.media = media; updateState(); save(); }
    @Override public long getMaxMedia() { return mediaCap; }
}
