package com.luxof.lapisworks.mixin;

import com.luxof.lapisworks.mixinsupport.StatusEffectParticleControl;

import net.minecraft.entity.effect.StatusEffectInstance;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(StatusEffectInstance.class)
public abstract class StatusEffectInstanceMixin implements StatusEffectParticleControl {
    @Shadow
    private boolean showParticles;
    @Unique
    private Boolean showParticlesOverride = null;

    @Inject(
        at = @At("HEAD"),
        method = "shouldShowParticles",
        cancellable = true
    )
    public void shouldShowParticles(CallbackInfoReturnable<Boolean> cir) {
        if (showParticlesOverride != null)
            cir.setReturnValue(showParticlesOverride);
    }

    @Override
    public void setShowsParticles(boolean should) { showParticlesOverride = should; }
    @Override
    public void revertToWhatShowParticlesWasBefore() { showParticlesOverride = null; }
}
