package com.luxof.lapisworks.mixin;

import at.petrak.hexcasting.api.addldata.ADMediaHolder;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.xplat.IXplatAbstractions;

import com.luxof.lapisworks.media.MediaTransferInterface;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(ItemEntity.class)
public abstract class ItemEntityMixin extends Entity implements MediaTransferInterface {

    public ItemEntityMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Shadow
    public abstract ItemStack getStack();

    @Unique @Nullable
    private ADMediaHolder getMediaHolder() {
        return IXplatAbstractions.INSTANCE.findMediaHolder(getStack());
    }

    @Override @Unique
    public boolean isMTIAtThisTime(CastingEnvironment ctx) { return getMediaHolder() != null; }

    @Override @Unique @Nullable
    public Vec3d getPosIfPossible() {
        return getPos();
    }

    @Override @Unique
    public void setMediaHere(long media) {
        getMediaHolder().setMedia(media);
        if (getMediaHere() <= 0L) {
            this.discard();
        }
    }

    @Override @Unique
    public long getMaxMedia() {
        return getMediaHolder().getMaxMedia();
    }

    @Override @Unique
    public long getMediaHere() {
        return getMediaHolder().getMedia();
    }
}
