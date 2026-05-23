package com.luxof.lapisworks.mixin;

import com.luxof.lapisworks.media.MediaTransferInterface;

import miyucomics.hexical.features.media_jar.MediaJarBlock;
import miyucomics.hexical.features.media_jar.MediaJarBlockEntity;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

@Mixin(value = MediaJarBlockEntity.class, remap = false)
public abstract class MediaJarBlockEntityMixin extends BlockEntity implements MediaTransferInterface {
    public MediaJarBlockEntityMixin(BlockEntityType<?> type, BlockPos pos, BlockState state) {
        super(type, pos, state);
    }

    @Shadow private long media;
    @Shadow public abstract long getMedia();
    @Shadow abstract void setMedia(long media);

    @Override @Unique public void setMediaHere(long media) { setMedia(media); }
    @Override @Unique public long getMaxMedia() { return MediaJarBlock.MAX_CAPACITY; }
    @Override @Unique public long getMediaHere() { return getMedia(); }
    @Override @Unique public Vec3d getPosIfPossible() { return this.pos.toCenterPos(); }
}
