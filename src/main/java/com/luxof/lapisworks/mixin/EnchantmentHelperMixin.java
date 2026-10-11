package com.luxof.lapisworks.mixin;

import com.luxof.lapisworks.init.ModItems;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(EnchantmentHelper.class)
public class EnchantmentHelperMixin {
    @Redirect(
        method = "get(Lnet/minecraft/item/ItemStack;)Ljava/util/Map;",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z",
            ordinal = 0
        )
    )
    private static boolean isEnchantedBook1(ItemStack stack, Item item) {
        return stack.isOf(item) || stack.isOf(ModItems.SUPER_ENCHANTED_BOOK);
    }

    @Redirect(
        method = "set(Ljava/util/Map;Lnet/minecraft/item/ItemStack;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/item/ItemStack;isOf(Lnet/minecraft/item/Item;)Z"
        )
    )
    private static boolean isEnchantedBook2(ItemStack stack, Item item) {
        return stack.isOf(item) || stack.isOf(ModItems.SUPER_ENCHANTED_BOOK);
    }
}
