package com.luxof.lapisworks.mixin;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;

import com.luxof.lapisworks.media.MediaTransferInterface;
import com.luxof.lapisworks.mixinsupport.WispCanIntoItem;

import java.util.UUID;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import ram.talia.hexal.common.entities.BaseCastingWisp;

// can't extend BaseWisp because some BS with an interface default method and class method having
// the same erasure but diff return types
@Mixin(value = BaseCastingWisp.class, remap = false)
public abstract class BaseCastingWispMixin extends Entity implements MediaTransferInterface, WispCanIntoItem {

    public BaseCastingWispMixin(EntityType<?> type, World world) {
        super(type, world);
    }


    @Shadow private UUID casterUUID;

    @Unique private BaseCastingWisp self = (BaseCastingWisp)(Object)this;

    @Override public boolean isMTIAtThisTime(
        CastingEnvironment ctx
    ) {
        LivingEntity thatCaster = ctx.getCastingEntity();
        return thatCaster != null && casterUUID != null && thatCaster.getUuid().equals(casterUUID);
    }

    @Override public Vec3d getPosIfPossible() {
        return getPos();
    }
    @Override public void setMediaHere(long media) {
        self.setMedia(media);
    }
    @Override public long getMaxMedia() {
        return 9_000_000_000L;
    }
    @Override public long getMediaHere() {
        return self.getMedia();
    }
    @Override public long withdrawMedia(long amount, boolean simulate) {
        long ret = MediaTransferInterface.super.withdrawMedia(amount, simulate);
        if (getMediaHere() <= 0L) {
            discard();
        }
        return ret;
    }


    @Unique
    private static final TrackedData<ItemStack> heldStack = DataTracker.registerData(
        BaseCastingWisp.class,
        TrackedDataHandlerRegistry.ITEM_STACK
    );
    @Unique
    private void trackHeldItemStack() {
        getDataTracker().startTracking(
            heldStack,
            ItemStack.EMPTY
        );
    }

    @Override public ItemStack getStack() {
        // mfw no initDataTracker for me to inject into
        try {
            return getDataTracker().get(heldStack);
        } catch (NullPointerException e) {
            trackHeldItemStack();
            return getDataTracker().get(heldStack);
        }
    }

    @Override public ItemStack setStack(ItemStack stack) {
        ItemStack old = getStack();
        getDataTracker().set(heldStack, stack);
        return old;
    }

    @Inject(
        at = @At("HEAD"),
        method = "readCustomDataFromNbt"
    )
    protected void lapisworks$readMyStack(NbtCompound nbt, CallbackInfo ci) {
        NbtCompound stack = nbt.getCompound("lapisworks_heldStack");
        getDataTracker().set(heldStack, ItemStack.fromNbt(stack));
    }
    
    @Inject(
        at = @At("HEAD"),
        method = "writeCustomDataToNbt"
    )
    protected void lapisworks$writeMyStack(NbtCompound nbt, CallbackInfo ci) {
        // thanks for the chainability, Mojang
        nbt.put("lapisworks_heldStack", getStack().writeNbt(new NbtCompound()));
    }
}
