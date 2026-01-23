package com.luxof.lapisworks.blocks.stuff;

import java.util.Set;

import net.minecraft.util.math.BlockPos;

public interface LinkableMediaBlock {
    public void addLink(BlockPos pos);
    public void removeLink(BlockPos pos);
    public boolean isLinkedTo(BlockPos pos);
    public Set<BlockPos> getLinks();
    public int getNumberOfLinks();
    default public int getMaxNumberOfLinks() { return 5; }
    public BlockPos getThisPos();
    /** this function does not take into account the maximum media capacity or negatives.
     * use depositMedia and withdrawMedia for that. */
    public void setMedia(long media);
    public long getMaxMedia();
    /** returns the amount that was deposited. */
    default public long depositMedia(long amount, boolean simulate) {
        long mediaHere = getMediaHere();
        long spaceLeft = getMaxMedia() - mediaHere;
        long toDeposit = Math.min(spaceLeft, amount);
        if (!simulate) {
            setMedia(mediaHere + toDeposit);
        }
        return toDeposit;
    }
    /** returns the amount that was withdrawn. */
    default public long withdrawMedia(long amount, boolean simulate) {
        long mediaHere = getMediaHere();
        long toWithdraw = Math.min(amount, mediaHere);
        if (!simulate) {
            setMedia(mediaHere - toWithdraw);
        }
        return toWithdraw;
    }
    public long getMediaHere();


    // don't override these :pray:
    /** does not simulate. */
    default public long depositMedia(long amount) { return depositMedia(amount, false); }
    /** does not simulate. */
    default public long withdrawMedia(long amount) { return withdrawMedia(amount, false); }
}
