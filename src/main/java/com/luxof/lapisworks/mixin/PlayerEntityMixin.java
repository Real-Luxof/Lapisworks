package com.luxof.lapisworks.mixin;

import at.petrak.hexcasting.common.lib.HexAttributes;

import com.luxof.lapisworks.interop.valkyrienskies.ValkyrienUtils;
import com.luxof.lapisworks.mixinsupport.EnchSentInterface;
import com.luxof.lapisworks.mixinsupport.LapisworksInterface;

import static com.luxof.lapisworks.Lapisworks.VALKYRIEN_SKIES_INTEROP;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

// "you know you can just use Cardinal Components for this-"
// Metal Gear Rising: Revengeance OST It Has To Be This Way Extended
@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity implements EnchSentInterface {
    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) { super(entityType, world); }

    @Unique private Vec3d enchSentPos = null;
    @Unique private Double sentRange = null;

    @Override @Nullable public Vec3d getEnchantedSentinel() { return this.enchSentPos; }
    @Override @Nullable public Double getEnchantedSentinelAmbit() { return this.sentRange; }
    @Override
    public void setEnchantedSentinel(Vec3d pos, Double ambit) {
        this.enchSentPos = pos;

        EntityAttributeInstance casterAmbit = getAttributeInstance(HexAttributes.AMBIT_RADIUS);
        if (sentRange != null)
            casterAmbit.setBaseValue(casterAmbit.getBaseValue() + sentRange / 2);

        this.sentRange = ambit;

        if (sentRange != null)
            casterAmbit.setBaseValue(casterAmbit.getBaseValue() - sentRange / 2);
    }
    @Override
    public boolean shouldBreakSent() {
        if (getEnchantedSentinel() == null) return false;
        double casterAmbit = getAttributeValue(HexAttributes.AMBIT_RADIUS);

        return this.getEnchantedSentinel() == null
            ? false
            : (VALKYRIEN_SKIES_INTEROP
                ? ValkyrienUtils.distance(getWorld(), this.getPos(), this.getEnchantedSentinel())
                : this.getPos().distanceTo(this.getEnchantedSentinel()))
                    > casterAmbit;
    }
    @Override
    public void breakSent() {
        this.setEnchantedSentinel(null, null);
    }

    @Inject(at = @At("HEAD"), method = "tick")
    public void tick(CallbackInfo ci) {
        if (this.shouldBreakSent()) { this.breakSent(); }
    }

    // mojang............
    @Inject(
        at = @At("TAIL"),
        method = "readCustomDataFromNbt"
    )
    public void lapisworks$JUSTFUCKINGCARRYTHESPEEDYOUTWAT(NbtCompound nbt, CallbackInfo ci) {
        var speed = this.getAttributeInstance(EntityAttributes.GENERIC_MOVEMENT_SPEED);
        speed.setBaseValue(
            speed.getBaseValue() +
            ((LapisworksInterface)this)
                .getAmountOfAttrJuicedUpByAmel(EntityAttributes.GENERIC_MOVEMENT_SPEED)
        );
    }
}
