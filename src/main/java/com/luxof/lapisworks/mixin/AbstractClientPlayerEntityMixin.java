package com.luxof.lapisworks.mixin;

import at.petrak.hexcasting.xplat.IClientXplatAbstractions;

import com.luxof.lapisworks.mixinsupport.SpiralPatternsClearable;

import com.mojang.authlib.GameProfile;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;

// i mixin to this so it's convenient to turn spiral patterns on and off
// (given i know a player is on the client)
// but also so i don't have to deal with networking bullshit
@Mixin(AbstractClientPlayerEntity.class)
public abstract class AbstractClientPlayerEntityMixin extends PlayerEntity implements SpiralPatternsClearable {
    public AbstractClientPlayerEntityMixin(
        World world, BlockPos pos, float yaw, GameProfile gameProfile
    ) { super(world, pos, yaw, gameProfile); }

    @Override
    public void setSpiralPatternsClearing(boolean yesOrNo) {
        ((SpiralPatternsClearable)(Object)IClientXplatAbstractions.INSTANCE
            .getClientCastingStack(this))
            .setSpiralPatternsClearing(yesOrNo);
    }

    @Override
    public boolean getSpiralPatternsClearing() {
        return ((SpiralPatternsClearable)(Object)IClientXplatAbstractions.INSTANCE
            .getClientCastingStack(this))
            .getSpiralPatternsClearing();
    }
}
