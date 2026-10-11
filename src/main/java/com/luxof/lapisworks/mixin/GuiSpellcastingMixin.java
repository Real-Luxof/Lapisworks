package com.luxof.lapisworks.mixin;

import at.petrak.hexcasting.client.gui.GuiSpellcasting;

import com.luxof.lapisworks.Lapisworks;

import static com.luxof.lapisworks.Lapisworks.LOGGER;
import static com.luxof.lapisworks.init.ModItems.AMEL_RING;
import static com.luxof.lapisworks.init.ModItems.AMEL_RING2;

import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.tag.TagKey;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(value = GuiSpellcasting.class)
public class GuiSpellcastingMixin {
    @Redirect(
        method = "tick()V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/item/ItemStack;isIn(Lnet/minecraft/registry/tag/TagKey;)Z"
        )
    )
    private boolean lapisworks$IsHoldingCastingRing(ItemStack heldItem, TagKey<Item> tag) {
        PlayerEntity player = MinecraftClient.getInstance().player;
        return heldItem.isIn(tag) ||
            Lapisworks.trinketEquipped(player, AMEL_RING) ||
            Lapisworks.trinketEquipped(player, AMEL_RING2);
    }
}
