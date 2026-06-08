package com.luxof.lapisworks.mixin;

import at.petrak.hexcasting.client.gui.GuiSpellcasting;

import com.luxof.lapisworks.Lapisworks;

import static com.luxof.lapisworks.init.ModItems.AMEL_RING;
import static com.luxof.lapisworks.init.ModItems.AMEL_RING2;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GuiSpellcasting.class)
public class GuiSpellcastingMixin {
    @Inject(
        method = "tick",
        at = @At(
            value = "INVOKE",
            target = "at/petrak/hexcasting/client/gui/GuiSpellcasting.closeForReal()V",
            shift = At.Shift.BEFORE
        ),
        cancellable = true
    )
    private void lapisworks$heyWait(CallbackInfo ci) {
        PlayerEntity player = MinecraftClient.getInstance().player;

        if (Lapisworks.trinketEquipped(player, AMEL_RING) ||
            Lapisworks.trinketEquipped(player, AMEL_RING2)) {
            // PROBABLY should add a check for if the player intentionally opened the casting menu
            // but i'll let that be until it bites me in the ass
            ci.cancel();
        }
    }
}
