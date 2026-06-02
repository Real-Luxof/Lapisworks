package com.luxof.lapisworks.mixin;

import com.luxof.lapisworks.collar.LapisCollarAdditions;
import com.luxof.lapisworks.mixinsupport.CollarControllable;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.data.DataTracker;
import net.minecraft.entity.data.TrackedData;
import net.minecraft.entity.data.TrackedDataHandlerRegistry;
import net.minecraft.entity.passive.CatEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.entity.passive.WolfEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = {
    CatEntity.class,
    WolfEntity.class
})
public abstract class CollarControllableEntityMixin extends TameableEntity implements CollarControllable {

    protected CollarControllableEntityMixin(EntityType<? extends TameableEntity> entityType, World world) {
        super(entityType, world);
    }

    private static final TrackedData<ItemStack> COLLAR_CAT = DataTracker.registerData(
        CatEntity.class,
        TrackedDataHandlerRegistry.ITEM_STACK
    );
    private static final TrackedData<ItemStack> COLLAR_WOLF = DataTracker.registerData(
        WolfEntity.class,
        TrackedDataHandlerRegistry.ITEM_STACK
    );
    private TrackedData<ItemStack> getCOLLAR() {
        return (Object)this instanceof CatEntity
            ? COLLAR_CAT
            : COLLAR_WOLF;
    }

    @Override
    public ItemStack getCollar() {
        if (!getDataTracker().containsKey(getCOLLAR())) trackCollarInTracker();
        return getDataTracker().get(getCOLLAR());
    }

    @Override
    public ItemStack setCollar(ItemStack collar) {
        if (!getDataTracker().containsKey(getCOLLAR())) trackCollarInTracker();
        ItemStack previously = getDataTracker().get(getCOLLAR());
        getDataTracker().set(getCOLLAR(), collar);
        return previously;
    }


    @Unique
    private void trackCollarInTracker() { getDataTracker().startTracking(getCOLLAR(), ItemStack.EMPTY); }

    @Inject(
        at = @At("HEAD"),
        method = "initDataTracker"
    )
    protected void lapisworks$addCollar(CallbackInfo ci) {
        this.dataTracker.startTracking(getCOLLAR(), ItemStack.EMPTY);
    }

    @Inject(
        at = @At("HEAD"),
        method = "writeCustomDataToNbt"
    )
    public void lapisworks$writeCollar(NbtCompound nbt, CallbackInfo ci) {
        getCollar().writeNbt(nbt);
    }
    @Inject(
        at = @At("HEAD"),
        method = "readCustomDataFromNbt"
    )
    public void lapisworks$readCollar(NbtCompound nbt, CallbackInfo ci) {
        setCollar(ItemStack.fromNbt(nbt));
    }


    @Inject(
        at = @At("HEAD"),
        method = "tick"
    )
    public void lapisworks$collarTick(CallbackInfo ci) {
        ItemStack collar = getCollar();
        if (collar.isEmpty()) return;
        LapisCollarAdditions.toAllAdditions(
            collar,
            (addition, id) -> addition.generalTick(collar, this)
        );
    }


    @Inject(
        at = @At("TAIL"),
        method = "initGoals"
    )
    protected void lapisworks$beCollarMindControlled(CallbackInfo ci) {
        this.goalSelector.add(0, new CollarMindControlGoal(this));
    }
}
