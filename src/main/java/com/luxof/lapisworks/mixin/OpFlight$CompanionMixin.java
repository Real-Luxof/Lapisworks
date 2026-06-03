package com.luxof.lapisworks.mixin;

import at.petrak.hexcasting.common.casting.actions.spells.OpFlight.Companion;

import com.luxof.lapisworks.collar.additions.StealthCollarAddition;

import static com.luxof.lapisworks.Lapisworks.getFirstTrinketIfEquipped;
import static com.luxof.lapisworks.init.ModItems.COLLAR;

import net.minecraft.server.network.ServerPlayerEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Companion.class)
public class OpFlight$CompanionMixin {
    @Inject(
        method = "tickDownFlight",
        at = @At(
            value = "INVOKE",
            target = "net/minecraft/server/network/ServerPlayerEntity.getWorld()Lnet/minecraft/world/World;",
            shift = At.Shift.BEFORE
        ),
        cancellable = true
    )
    public void lapisworks$beStealthyIfWearingACollarOnFlightBreak(
        ServerPlayerEntity sp,
        CallbackInfo ci
    ) {
        var trinket = getFirstTrinketIfEquipped(sp, COLLAR);
        if (trinket != null && COLLAR.hasAddition(trinket.getRight(), StealthCollarAddition.ID))
            ci.cancel();
    }


    @Inject(
        method = "tickDownFlight",
        at = @At(
            value = "INVOKE",
            target = "at/petrak/hexcasting/xplat/IXplatAbstractions.setFlight(Lnet/minecraft/server/network/ServerPlayerEntity;Lat/petrak/hexcasting/api/player/FlightAbility;)V",
            ordinal = 1,
            shift = At.Shift.AFTER
        ),
        cancellable = true
    )
    public void lapisworks$beStealthyIfWearingACollar(
        ServerPlayerEntity sp,
        CallbackInfo ci
    ) {
        var trinket = getFirstTrinketIfEquipped(sp, COLLAR);
        if (trinket != null && COLLAR.hasAddition(trinket.getRight(), StealthCollarAddition.ID))
            ci.cancel();
    }
}
