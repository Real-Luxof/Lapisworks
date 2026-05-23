package com.luxof.lapisworks.media;

import static com.luxof.lapisworks.Lapisworks.interactWithLinkableMediaBlocks;

import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

/** implement this in a block entity.
 * <p>don't worry about removing dead links!
 * as long as your block's onStateReplaced calls super.onStateReplaced,
 * removing the current position from any blocks it's linked to should be handled automatically. */
public interface LinkableMediaBlock extends MediaTransferInterface {
    public World getWorld();

    public void addLink(BlockPos pos);
    public void removeLink(BlockPos pos);
    public boolean isLinkedTo(BlockPos pos);
    public Set<BlockPos> getLinks();
    public int getNumberOfLinks();

    default public int getMaxNumberOfLinks() { return 5; }
    @Nullable default Vec3d getPosIfPossible() { return getThisPos().toCenterPos(); }
    public BlockPos getThisPos();

    public long getMediaHere();
    default long getMediaHereWithLinks() {
        long total = 0L;
        Stack<BlockPos> todo = new Stack<>();
        HashSet<BlockPos> seen = new HashSet<>();

        todo.add(getThisPos());
        seen.add(getThisPos());

        while (!todo.isEmpty()) {
            BlockPos currPos = todo.pop();
            LinkableMediaBlock curr = (LinkableMediaBlock)getWorld().getBlockEntity(currPos);
            total += curr.getMediaHere();
            curr.getLinks().forEach(pos -> { if (seen.add(pos)) todo.add(pos); });
        }

        return total;
    }

    public long getMaxMedia();
    default long getMaxMediaWithLinks() {
        long total = 0L;
        Stack<BlockPos> todo = new Stack<>();
        HashSet<BlockPos> seen = new HashSet<>();

        todo.add(getThisPos());
        seen.add(getThisPos());

        while (!todo.isEmpty()) {
            BlockPos currPos = todo.pop();
            LinkableMediaBlock curr = (LinkableMediaBlock)getWorld().getBlockEntity(currPos);
            total += curr.getMaxMedia();
            curr.getLinks().forEach(pos -> { if (seen.add(pos)) todo.add(pos); });
        }

        return total;
    }

    default public long depositMedia(long amount, boolean simulate) {
        return MediaTransferInterface.super.depositMedia(amount, simulate);
    }
    default long depositMediaWithLinks(long amount, boolean simulate) {
        return interactWithLinkableMediaBlocks(
            getWorld(),
            Set.of(getThisPos()),
            amount,
            true,
            simulate
        ).getLeft();
    }

    default public long withdrawMedia(long amount, boolean simulate) {
        return MediaTransferInterface.super.withdrawMedia(amount, simulate);
    }
    default long withdrawMediaWithLinks(long amount, boolean simulate) {
        return interactWithLinkableMediaBlocks(
            getWorld(),
            Set.of(getThisPos()),
            amount,
            false,
            simulate
        ).getLeft();
    }
}
