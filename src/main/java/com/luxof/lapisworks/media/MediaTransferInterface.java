package com.luxof.lapisworks.media;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;

import net.minecraft.util.math.Vec3d;

import org.jetbrains.annotations.Nullable;

/** classes that extend this support Deposit Media, Withdraw Media, and Inner Media Prfn. */
public interface MediaTransferInterface {
    /** for e.g. item entities and wisps */
    default public boolean isMTIAtThisTime(
        CastingEnvironment ctx
    ) {
        return true;
    }
    @Nullable
    public Vec3d getPosIfPossible();
    /** this function does not take into account the maximum media capacity or negatives.
     * use depositMedia and withdrawMedia for that. */
    public void setMediaHere(long media);
    public long getMaxMedia();
    public long getMediaHere();

    /** returns the amount that was deposited. */
    default public long depositMedia(long amount, boolean simulate) {
        long mediaHere = getMediaHere();
        long spaceLeft = getMaxMedia() - mediaHere;
        long toDeposit = Math.min(spaceLeft, amount);
        if (!simulate) {
            setMediaHere(mediaHere + toDeposit);
        }
        return toDeposit;
    }
    /** returns the amount that was withdrawn. */
    default public long withdrawMedia(long amount, boolean simulate) {
        long mediaHere = getMediaHere();
        long toWithdraw = Math.min(amount, mediaHere);
        if (!simulate) {
            setMediaHere(mediaHere - toWithdraw);
        }
        return toWithdraw;
    }
}
