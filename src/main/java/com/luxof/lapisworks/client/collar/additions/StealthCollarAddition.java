package com.luxof.lapisworks.client.collar.additions;

import at.petrak.hexcasting.api.casting.iota.IotaType;
import at.petrak.hexcasting.api.casting.iota.NullIota;
import at.petrak.hexcasting.api.utils.NBTHelper;
import at.petrak.hexcasting.common.lib.HexItems;

import com.luxof.lapisworks.client.collar.LapisCollarAddition;
import com.luxof.lapisworks.mixinsupport.SpiralPatternsClearable;
import com.luxof.lapisworks.mixinsupport.StatusEffectParticleControl;

import static com.luxof.lapisworks.Lapisworks.id;
import static com.luxof.lapisworks.init.ModItems.COLLAR;

import dev.emi.trinkets.api.SlotReference;

import java.util.List;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import org.jetbrains.annotations.Nullable;

public class StealthCollarAddition implements LapisCollarAddition {
    public static final Identifier ID = id("collar/stealth");

    @Override
    public Text getName() {
        return Text.translatable(
            "tooltips.lapisworks.collar.added_items.stealth"
        ).formatted(Formatting.BLUE);
    }

    @Override
    public Text getName(ItemStack collarStack) {
        return getName();
    }

    @Override
    public boolean testItem(Item item) {
        return item == HexItems.CHARGED_AMETHYST;
    }

    @Override
    public boolean canAdd(ItemStack collarStack, List<Identifier> existingAdditions, Identifier yourId) {
        return true;
    }

    @Override
    public ItemStack craft(ItemStack collarStack, List<Identifier> existingAdditions, ItemStack yourStack,
            Identifier yourId) {
        ItemStack newCollar = collarStack.copy();
        COLLAR.addAddition(newCollar, yourId);
        NBTHelper.putCompound(newCollar, "stored_iota", IotaType.serialize(new NullIota()));
        return newCollar;
    }

    @Override
    public void render(ItemStack collarStack, Identifier yourId, @Nullable LivingEntity entity,
            ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light,
            int overlay) {
        return;
    }

    public void onEquip(ItemStack stack, LivingEntity entity) {
        if (!entity.getWorld().isClient || !(entity instanceof PlayerEntity player)) return;
        ((SpiralPatternsClearable)(player)).setSpiralPatternsClearing(true);
    }

    public void onUnequip(ItemStack stack, LivingEntity entity) {

        StatusEffectInstance invisiblity = entity.getStatusEffect(StatusEffects.INVISIBILITY);
        if (invisiblity != null)
            ((StatusEffectParticleControl)invisiblity).revertToWhatShowParticlesWasBefore();

        if (!entity.getWorld().isClient || !(entity instanceof PlayerEntity player)) return;
        ((SpiralPatternsClearable)(player)).setSpiralPatternsClearing(false);
    }

	@Override
	public void trinketTick(
        ItemStack stack, SlotReference slot, LivingEntity entity
    ) {
		generalTick(stack, entity);
	}

    @Override
    public void generalTick(ItemStack stack, LivingEntity entity) {
        StatusEffectInstance invisiblity = entity.getStatusEffect(StatusEffects.INVISIBILITY);
        if (invisiblity != null)
            ((StatusEffectParticleControl)invisiblity).setShowsParticles(false);

        if (!entity.getWorld().isClient || !(entity instanceof PlayerEntity player))
            return;
        ((SpiralPatternsClearable)player).setSpiralPatternsClearing(true);
    }
}
