package com.luxof.lapisworks.mixin;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.env.PlayerBasedCastEnv;

import com.luxof.lapisworks.mixinsupport.LapisworksInterface;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.server.world.ServerWorld;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = PlayerBasedCastEnv.class, remap = false)
public abstract class PlayerBasedCastEnvMixin extends CastingEnvironment {
	protected PlayerBasedCastEnvMixin(ServerWorld world) { super(world); }

	@Shadow
	public abstract LivingEntity getCastingEntity();

	@Inject(at = @At("HEAD"), method = "canOvercast", cancellable = true)
	protected void canOvercast(CallbackInfoReturnable<Boolean> cir) {
		double amountOfMaxHPJuicedUp = ((LapisworksInterface)getCastingEntity())
			.getAmountOfAttrJuicedUpByAmel(EntityAttributes.GENERIC_MAX_HEALTH);

		if (amountOfMaxHPJuicedUp > 0) cir.setReturnValue(false);
	}
}
