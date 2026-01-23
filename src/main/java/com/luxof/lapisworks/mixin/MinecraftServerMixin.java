package com.luxof.lapisworks.mixin;

import com.luxof.lapisworks.LapisConfig;

import java.util.function.BooleanSupplier;

import net.minecraft.server.MinecraftServer;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MinecraftServer.class)
public class MinecraftServerMixin {

    @Unique private int tickCountdown = 100;
    @Unique private void handleConfigUpdates() {
        if (tickCountdown > 0) {
            tickCountdown--;
            return;
        } else {
            tickCountdown = 100;
        }
        LapisConfig.renewCurrentConfig();
    }

    @Inject(at = @At("HEAD"), method = "tick")
    public void tick(BooleanSupplier shouldKeepTicking, CallbackInfo ci) {
        handleConfigUpdates();
    }
}
