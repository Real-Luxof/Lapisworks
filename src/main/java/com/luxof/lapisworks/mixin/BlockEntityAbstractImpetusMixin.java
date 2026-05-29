package com.luxof.lapisworks.mixin;

import at.petrak.hexcasting.api.block.HexBlockEntity;
import at.petrak.hexcasting.api.casting.circles.BlockEntityAbstractImpetus;

import com.luxof.lapisworks.media.LinkableMediaBlock;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.inventory.SidedInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = BlockEntityAbstractImpetus.class, remap = false)
public abstract class BlockEntityAbstractImpetusMixin extends HexBlockEntity implements SidedInventory, LinkableMediaBlock {
    public BlockEntityAbstractImpetusMixin(BlockEntityType<?> pType, BlockPos pWorldPosition, BlockState pBlockState) { super(pType, pWorldPosition, pBlockState); }

    @Unique
    private HashSet<BlockPos> linked = new HashSet<>();
    @Shadow
    protected long media;
    @Shadow
    @Final
    private static long MAX_CAPACITY;

    @Override public World getWorldThisIsIn() { return getWorld(); }
    @Override public void addLink(BlockPos pos) { linked.add(pos); }
    @Override public void removeLink(BlockPos pos) { linked.remove(pos); }
    @Override public boolean isLinkedTo(BlockPos pos) { return linked.contains(pos); }
    @Override public Set<BlockPos> getLinks() { return linked; }
    @Override public int getNumberOfLinks() { return linked.size(); }
    @Override public int getMaxNumberOfLinks() { return 1; }
    @Override public BlockPos getThisPos() { return getPos(); }

    @Override public long getMediaHere() { return media; }
    @Override public long getMaxMedia() { return Math.max(getMediaHere(), 10000_0000L); }
    @Override public void setMediaHere(long to) { media = to; sync(); }


    @Unique
    private List<Integer> posToInts(HashSet<BlockPos> posList) {
        return posList.stream().flatMap(
            pos -> Stream.of(pos.getX(), pos.getY(), pos.getZ())
        ).toList();
    }
    @Inject(at = @At("HEAD"), method = "saveModData")
    protected void saveModData(NbtCompound nbt, CallbackInfo ci) {
        nbt.putIntArray(
            "links",
            posToInts(linked)
        );
    }

    @Unique
    private HashSet<BlockPos> intsToPos(int[] intArray) {
        HashSet<BlockPos> posList = new HashSet<>();
        int x = 0;
        int y = 0;
        int part = 0;
        for (int integer : intArray) {
            switch (part) {
                case 0 -> x = integer;
                case 1 -> y = integer;
                case 2 -> posList.add(new BlockPos(x, y, integer));
                default -> {}
            };
            part = (part + 1) % 3;
        }
        return posList;
    }
    @Inject(at = @At("HEAD"), method = "loadModData")
    protected void loadModData(NbtCompound nbt, CallbackInfo ci) {
        linked = intsToPos(nbt.getIntArray("links"));
    }
}
