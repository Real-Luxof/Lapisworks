package com.luxof.lapisworks.client.trinkets;

import static com.luxof.lapisworks.init.ModItems.COLLAR;

import com.luxof.lapisworks.collar.additions.StealthCollarAddition;

import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.client.TrinketRenderer;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.model.EntityModel;
import net.minecraft.client.render.entity.model.PlayerEntityModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.RotationAxis;

public class CollarTrinketRenderer implements TrinketRenderer {

    @Override
    @SuppressWarnings("unchecked")
    public void render(ItemStack stack, SlotReference slotReference, EntityModel<? extends LivingEntity> contextModel,
            MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light, LivingEntity entity,
            float limbAngle, float limbDistance, float tickDelta, float animationProgress, float headYaw,
            float headPitch) {

        if (!(contextModel instanceof PlayerEntityModel playerModel &&
                entity instanceof AbstractClientPlayerEntity player)) return;
        if (player.isInvisible() && COLLAR.hasAddition(stack, StealthCollarAddition.ID)) return;

        matrices.push();
        TrinketRenderer.followBodyRotations(entity, playerModel);
        TrinketRenderer.translateToFace(matrices, playerModel, player, 0, 0);

        matrices.translate(0.0, 0.185/16.0, 0.31);
        matrices.scale(0.6f, 0.6f, 0.6f);
        matrices.multiply(RotationAxis.POSITIVE_Z.rotationDegrees(180.0f));

        MinecraftClient instance = MinecraftClient.getInstance();
        instance.getItemRenderer().renderItem(
            entity,
            stack,
            ModelTransformationMode.HEAD,
            false,
            matrices,
            vertexConsumers,
            instance.world,
            light,
            OverlayTexture.DEFAULT_UV,
            0
        );
        matrices.pop();
    }
}
