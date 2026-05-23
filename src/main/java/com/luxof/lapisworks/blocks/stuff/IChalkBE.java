package com.luxof.lapisworks.blocks.stuff;

import at.petrak.hexcasting.api.HexAPI;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;

import com.luxof.lapisworks.chalk.OneTimeRitualExecutionState;
import com.luxof.lapisworks.init.PersistentStateRituals;
import com.luxof.lapisworks.media.MediaTransferInterface;

import static com.luxof.lapisworks.Lapisworks.getFacingWithRespectToDown;

import java.util.List;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

/** makes this block entity cast when deposited into by Deposit Media. */
public interface IChalkBE extends MediaTransferInterface {
    default public boolean isMTIAtThisTime(
        CastingEnvironment ctx
    ) {
        return true;
    }

    /** happens on media deposited by the Deposit Media spell. */
    default public void startCast(long amount, CastingEnvironment ctx) {
        ServerPlayerEntity player = null;
        if (ctx.getCastingEntity() instanceof ServerPlayerEntity p) player = p;

        PersistentStateRituals.getState(ctx.getWorld()).addRitual(new OneTimeRitualExecutionState(
            getPos(),
            getFacingWithRespectToDown(
                player != null
                    ? player.getRotationVector()
                    : Vec3d.of(Direction.NORTH.getVector()),
                getAttachedTo()
            ),
            new CastingImage(),
            player != null ? player.getUuid() : null,
            player != null ? HexAPI.instance().getColorizer(player) : null,
            amount,
            List.of()
        ));
    }

    public Direction getAttachedTo();
    public BlockPos getPos();
    default public Vec3d getPosIfPossible() { return getPos().toCenterPos(); }
    default public long depositMedia(long amount, boolean simulate) {
        return amount;
    }
    default public long withdrawMedia(long amount, boolean simulate) { return 0L; }
    default public void setMediaHere(long media) {}
    default public long getMediaHere() { return 0L; }
    default public long getMaxMedia() { return Long.MAX_VALUE; }
}
