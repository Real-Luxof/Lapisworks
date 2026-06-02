package com.luxof.lapisworks.collar;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;

import dev.emi.trinkets.api.SlotReference;

import java.util.List;
import java.util.UUID;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

public interface LapisCollarAddition {
    public Text getName();
    public Text getName(ItemStack collarStack);
    /** No <code>ItemStack</code> usage so I can *eventually* put this stuff in EMI.  */
    public boolean testItem(Item item);

    public boolean canAdd(
        ItemStack collarStack,
        List<Identifier> existingAdditions,
        Identifier yourId
    );
    /** Add your addition to the stack via <code>COLLAR.addAddition</code>. */
    public ItemStack craft(
        ItemStack collarStack,
        List<Identifier> existingAdditions,
        ItemStack yourStack,
        Identifier yourId
    );

    /** Make sure to annotate this method with <code>@Environment(EnvType.CLIENT)</code>.
     * <p>The reason you should do this is so that you can register this method in common code
     * without it erroring when registered on a dedicated server. */
    @Environment(EnvType.CLIENT)
    public void render(
        ItemStack collarStack,
        Identifier yourId,
        @Nullable LivingEntity entity,
        ModelTransformationMode mode,
        MatrixStack matrices,
        VertexConsumerProvider vertexConsumers,
        int light,
        int overlay
    );

    default boolean renderingAsTrinket(ModelTransformationMode mode) {
        return mode.equals(ModelTransformationMode.GUI);
    }



    default void inventoryTick(
        ItemStack stack, World world, LivingEntity entity, int slot, boolean selected
    ) {}
    default void trinketTick(
        ItemStack stack, SlotReference slot, LivingEntity entity
    ) {}
    default void generalTick(ItemStack stack, LivingEntity entity) {}

    
	default void onEquip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        onEquip(stack, entity);
    }
	default void onUnequip(ItemStack stack, SlotReference slot, LivingEntity entity) {
        onUnequip(stack, entity);
	}
    default void onEquip(ItemStack stack, LivingEntity entity) {}
    default void onUnequip(ItemStack stack, LivingEntity entity) {}


    default Multimap<EntityAttribute, EntityAttributeModifier> getModifiers(
        ItemStack stack, SlotReference slot, LivingEntity entity, UUID uuid
    ) {
        return ImmutableMultimap.of();
    }
}
