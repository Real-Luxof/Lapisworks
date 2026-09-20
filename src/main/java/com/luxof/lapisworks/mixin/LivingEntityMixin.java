package com.luxof.lapisworks.mixin;

import com.google.common.collect.ImmutableMultimap;

import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;

import static com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes.ATTACK_RANGE;
import static com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes.REACH;
import static com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes.getAttackRange;
import static com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes.getReachDistance;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import com.luxof.lapisworks.actions.MoarReachYouBitch;
import com.luxof.lapisworks.init.EnchantCountKeeper;
import com.luxof.lapisworks.items.shit.ITotem;
import com.luxof.lapisworks.mixinsupport.DamageSupportInterface;
import com.luxof.lapisworks.mixinsupport.LapisworksInterface;

import dev.emi.trinkets.api.SlotReference;

import static com.luxof.lapisworks.Lapisworks.tryGetTotem;
import static com.luxof.lapisworks.LapisworksIDs.REACH_ENHANCEMENT_UUID;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LightningEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.DefaultAttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.tag.DamageTypeTags;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.stat.Stats;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(LivingEntity.class)
public abstract class LivingEntityMixin extends Entity implements LapisworksInterface, DamageSupportInterface {
	public LivingEntityMixin(EntityType<?> type, World world) { super(type, world); }

	@Shadow @Final
	private AttributeContainer attributes;

	@Unique
	private Map<EntityAttribute, Double> juicedUpVals = new HashMap<>(Map.of(
		EntityAttributes.GENERIC_ATTACK_DAMAGE, 0.0,
		EntityAttributes.GENERIC_MAX_HEALTH, 0.0,
		EntityAttributes.GENERIC_MOVEMENT_SPEED, 0.0
	));
	@Unique private List<Integer> enchantments = new ArrayList<>(
		List.of(EnchantCountKeeper.getTallyAsZeroes())
	);

	// specification details
	@Override
	public double getAmountOfAttrJuicedUpByAmel(EntityAttribute attribute) {
		if (attribute == ReachEntityAttributes.REACH) {
			return getReachDistance((LivingEntity)(Object)this, 0);
		} else if (attribute == ReachEntityAttributes.ATTACK_RANGE) {
			return getAttackRange((LivingEntity)(Object)this, 0);
		}
		return juicedUpVals.get(attribute);
	}

	@Override
	public void setAmountOfAttrJuicedUpByAmel(EntityAttribute attribute, double value) {
		double juiced = juicedUpVals.get(attribute);
		EntityAttributeInstance attrInst = attributes.getCustomInstance(attribute);

		if (attrInst != null) {
			attrInst.setBaseValue(attrInst.getBaseValue() - juiced + value);
		}
		juicedUpVals.put(attribute, value);
	}
	@Override @Unique
    public void setJuicedAttrSpecifically(EntityAttribute attribute, double value) {
		juicedUpVals.put(attribute, value);
	}

	@Override
	public void setAllJuicedUpAttrsToZero() {
		juicedUpVals.keySet().forEach(attr -> juicedUpVals.put(attr, 0.0));
	}

	@Override
	public Map<EntityAttribute, Double> getLapisworksAttributes() { return juicedUpVals; }
	@Override
	public void setLapisworksAttributes(AttributeContainer toAttributes) {
		setAmountOfAttrJuicedUpByAmel(
			EntityAttributes.GENERIC_ATTACK_DAMAGE,
			toAttributes.getBaseValue(EntityAttributes.GENERIC_ATTACK_DAMAGE)
		);
		setAmountOfAttrJuicedUpByAmel(
			EntityAttributes.GENERIC_MAX_HEALTH,
			toAttributes.getBaseValue(EntityAttributes.GENERIC_MAX_HEALTH)
		);
		setAmountOfAttrJuicedUpByAmel(
			EntityAttributes.GENERIC_MOVEMENT_SPEED,
			toAttributes.getBaseValue(EntityAttributes.GENERIC_MOVEMENT_SPEED)
		);
	}
	@Override
	public void setLapisworksAttributes(Map<EntityAttribute, Double> toAttributes) {
		setAmountOfAttrJuicedUpByAmel(
			EntityAttributes.GENERIC_ATTACK_DAMAGE,
			toAttributes.get(EntityAttributes.GENERIC_ATTACK_DAMAGE)
		);
		setAmountOfAttrJuicedUpByAmel(
			EntityAttributes.GENERIC_MAX_HEALTH,
			toAttributes.get(EntityAttributes.GENERIC_MAX_HEALTH)
		);
		setAmountOfAttrJuicedUpByAmel(
			EntityAttributes.GENERIC_MOVEMENT_SPEED,
			toAttributes.get(EntityAttributes.GENERIC_MOVEMENT_SPEED)
		);
	}
	/** on world load ragh */
	@Unique
	public void setJuiceOnWorldLoad(AttributeContainer toAttributes) {
		juicedUpVals.put(
			EntityAttributes.GENERIC_ATTACK_DAMAGE,
			toAttributes.getBaseValue(EntityAttributes.GENERIC_ATTACK_DAMAGE)
		);

		juicedUpVals.put(
			EntityAttributes.GENERIC_MAX_HEALTH,
			toAttributes.getBaseValue(EntityAttributes.GENERIC_MAX_HEALTH)
		);

		juicedUpVals.put(
			EntityAttributes.GENERIC_MOVEMENT_SPEED,
			toAttributes.getBaseValue(EntityAttributes.GENERIC_MOVEMENT_SPEED)
		);
	}

	@Override
	public int getEnchant(int whatEnchant) {
		return this.enchantments.get(whatEnchant);
	}

	@Override
	public void setEnchantmentLevel(int whatEnchant, int level) {
		this.enchantments.set(whatEnchant, level);
	}

	// still not DRYer than your dms
	@Override
	public void incrementEnchant(int whatEnchant) { this.incrementEnchant(whatEnchant, 1); }
	@Override
	public void incrementEnchant(int whatEnchant, int amount) {
		this.setEnchantmentLevel(
			whatEnchant,
			this.getEnchant(whatEnchant) + amount
		);
	}
	@Override
	public void decrementEnchant(int whatEnchant) { this.incrementEnchant(whatEnchant, -1); }
	@Override
	public void decrementEnchant(int whatEnchant, int amount) { this.incrementEnchant(whatEnchant, -amount); }

	@Override
	public List<Integer> getEnchantments() {
		return List.copyOf(this.enchantments);
	}

	@Override
	public int[] getEnchantmentsArray() {
		return this.enchantments.stream().mapToInt(Integer::intValue).toArray();
	}

	@Override
	public void setEnchantments(int[] levels) {
		for (int i = 0; i < levels.length && i < this.enchantments.size(); i++) {
			this.enchantments.set(i, levels[i]);
		}
	}

	@Override
	public void setAllEnchantsToZero() {
		for (int i = 0; i < this.enchantments.size(); i++) { this.enchantments.set(i, 0); }
	}

	// may be changed in the future, idk.
	@Override
	public void copyCrossDeath(ServerPlayerEntity oldplr) {}

	@Override
	public void copyCrossDimensional(ServerPlayerEntity oldplr) {
		LapisworksInterface old = (LapisworksInterface)oldplr;
		this.setLapisworksAttributes(old.getLapisworksAttributes());
		this.setEnchantments(old.getEnchantmentsArray());
	}


	@Inject(at = @At("TAIL"), method = "readCustomDataFromNbt")
	public void readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
		if (attributes != null && this.getWorld() != null && !this.getWorld().isClient) {

			setJuiceOnWorldLoad(
				new AttributeContainer(DefaultAttributeContainer.builder()
					.add(EntityAttributes.GENERIC_ATTACK_DAMAGE, nbt.getDouble("LAPISWORKS_JUICED_FISTS"))
					.add(EntityAttributes.GENERIC_MAX_HEALTH, nbt.getDouble("LAPISWORKS_JUICED_SKIN"))
					.add(EntityAttributes.GENERIC_MOVEMENT_SPEED, nbt.getDouble("LAPISWORKS_JUICED_FEET"))
					.build())
			);

			if (nbt.getBoolean("LAPISWORKS_JUICED_REACH"))
				attributes.addTemporaryModifiers(
					ImmutableMultimap.of(
						REACH, MoarReachYouBitch.REACH_MODIFIER,
						ATTACK_RANGE, MoarReachYouBitch.ATTACK_REACH_MODIFIER
					)
				);

		}
		setEnchantments(nbt.getIntArray("LAPISWORKS_ENCHANTMENTS"));
	}

	@Inject(at = @At("TAIL"), method = "writeCustomDataToNbt")
	public void writeCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
		nbt.putDouble("LAPISWORKS_JUICED_FISTS", juicedUpVals.get(EntityAttributes.GENERIC_ATTACK_DAMAGE));
		nbt.putDouble("LAPISWORKS_JUICED_SKIN", juicedUpVals.get(EntityAttributes.GENERIC_MAX_HEALTH));
		nbt.putDouble("LAPISWORKS_JUICED_FEET", juicedUpVals.get(EntityAttributes.GENERIC_MOVEMENT_SPEED));
		nbt.putIntArray("LAPISWORKS_ENCHANTMENTS", getEnchantments());
		nbt.putBoolean(
			"LAPISWORKS_JUICED_REACH",
			attributes.hasModifierForAttribute(REACH, REACH_ENHANCEMENT_UUID)
		);
	}

	// not sure i even need this
	@Inject(at = @At("HEAD"), method = "onDeath")
	public void onDeath(DamageSource damageSource, CallbackInfo ci) {
		this.setAllJuicedUpAttrsToZero();
		this.setAllEnchantsToZero();
	}

	@Inject(at = @At("HEAD"), method = "onAttacking")
	public void onAttacking(Entity target, CallbackInfo ci) {

		if (!(target instanceof LivingEntity) || target.getWorld().isClient)
			return;
		if (this.getEnchant(AllEnchantments.fireyFists) == 1)
			((LivingEntity)target).setOnFireFor(3);

		int lightningbendingLevel = this.getEnchant(AllEnchantments.lightningBending);
		ServerWorld world = (ServerWorld)target.getWorld();
		Vec3d targetPos = target.getPos();

		if (
			(lightningbendingLevel == 1 && world.isThundering()) ||
			(lightningbendingLevel == 2 && (world.isRaining() || world.isRaining())) ||
			lightningbendingLevel == 3
		) {
			LightningEntity lightning = new LightningEntity(EntityType.LIGHTNING_BOLT, world);
			lightning.setPos(targetPos.x, targetPos.y, targetPos.z);
			world.tryLoadEntity(lightning);
		}
	}

	@WrapMethod(method = {"computeFallDamage"})
	public int computeFallDamage(float fallDistance, float damageMultiplier, Operation<Integer> og) {
		return og.call(
			Math.max(
				fallDistance - 20 * this.getEnchant(AllEnchantments.fallDmgRes),
				0
			),
			damageMultiplier
		);
	}

	@ModifyVariable(
		method = "getNextAirUnderwater",
		at = @At(
			value = "INVOKE_ASSIGN",
			target = "net/minecraft/enchantment/EnchantmentHelper.getRespiration(Lnet/minecraft/entity/LivingEntity;)I",
			shift = At.Shift.AFTER
		),
		index = 2 // why is this supposed to be 2? there are only 2 variables in the entire method.
	)
	private int getLongBreathEffectUnderwater(int original) {
		// TODO: according to logging, getEnchant sometimes returns 0 here. investigate, maybe?????
		// ^ this message looks like i was on the 'caine but i think i meant when longBreath was >0
		//   it still said 0. probably some unimportant networking bullshit tbh
		return original + this.getEnchant(AllEnchantments.longBreath) * 2;
	}

	@ModifyConstant(method = "getNextAirOnLand", constant = @Constant(intValue = 4))
	private int getLongBreathEffectOnLand(int original) {
		return original + this.getEnchant(AllEnchantments.longBreath) * 2;
	}

	@Inject(at = @At("HEAD"), method = "damage", cancellable = true)
	public void damage(DamageSource source, float amount, CallbackInfoReturnable<Boolean> cir) {
		if (!this.damageHelper(source, amount, (LivingEntity)(Object)this, this.getEnchantments())) {
			cir.setReturnValue(false);
		}
	}


	@Inject(at = @At("HEAD"), method = "tryUseTotem", cancellable = true)
	public void lapisworks$useMyTotemIfYouDontHaveOne(
		DamageSource source,
		CallbackInfoReturnable<Boolean> cir
	) {
		if (source.isIn(DamageTypeTags.BYPASSES_INVULNERABILITY)) return;

		LivingEntity thisLE = (LivingEntity)(Object)this;

		var totemAndSlot = tryGetTotem(thisLE);
		if (totemAndSlot == null) return;

		ItemStack totem = totemAndSlot.getLeft();
		SlotReference slot = totemAndSlot.getRight();

		if ((Object)this instanceof ServerPlayerEntity sp) {
			sp.incrementStat(Stats.USED.getOrCreateStat(totem.getItem()));
			Criteria.USED_TOTEM.trigger(sp, totem);
		}

		if (totem.getItem() instanceof ITotem iTotem)
			iTotem.revive(thisLE, totem, slot);
		else {
			thisLE.setHealth(1.0f);
			thisLE.clearStatusEffects();
			thisLE.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 900, 1));
			thisLE.addStatusEffect(new StatusEffectInstance(StatusEffects.ABSORPTION, 100, 1));
			thisLE.addStatusEffect(new StatusEffectInstance(StatusEffects.FIRE_RESISTANCE, 800, 0));
			this.getWorld().sendEntityStatus(this, (byte)35);
			totem.decrement(1);
		}

		cir.setReturnValue(true);
	}
}
