package com.luxof.lapisworks.mixin;

import com.luxof.lapisworks.init.LapisConfig;
import com.luxof.lapisworks.init.ModItems;

import java.util.Map;

import javax.annotation.Nullable;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.AnvilScreenHandler;
import net.minecraft.screen.ForgingScreenHandler;
import net.minecraft.screen.Property;
import net.minecraft.screen.ScreenHandlerContext;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.text.Text;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilScreenHandler.class)
public abstract class AnvilScreenHandlerMixin extends ForgingScreenHandler {
    public AnvilScreenHandlerMixin(ScreenHandlerType<?> type, int syncId, PlayerInventory playerInventory,
            ScreenHandlerContext context) {
        super(type, syncId, playerInventory, context);
    }

    @Shadow public Property levelCost;
    @Shadow @Nullable public String newItemName;

    // i didn't need to go this overboard... i just needed EnchantmentHelperMixin.
    // oh well.
    @Inject(
        method = "updateResult",
        at = @At("HEAD"),
        cancellable = true
    )
    private void lapisworks$handleSEnchBooks(CallbackInfo ci) {
        ItemStack enchant = input.getStack(0).copy();
        ItemStack book = input.getStack(1);
        int finalLevelCost = 0;
        if (
            enchant.isEmpty() ||
            !book.isOf(ModItems.SUPER_ENCHANTED_BOOK) ||
            EnchantedBookItem.getEnchantmentNbt(book).isEmpty()
        ) return;

        Map<Enchantment, Integer> itemEnchants = EnchantmentHelper.get(enchant);
        Map<Enchantment, Integer> bookEnchants = EnchantmentHelper.get(book);

        boolean didSomething = false;

        for (Enchantment enchantment : bookEnchants.keySet()) {
            int bookEnchantmentLevel = bookEnchants.get(enchantment);
            int itemEnchantmentLevel = itemEnchants.getOrDefault(bookEnchants, 0);
            int finalEnchantmentLevel = bookEnchantmentLevel == itemEnchantmentLevel
                ? Math.min(
                    bookEnchantmentLevel + 1,
                    LapisConfig.overenchant_limit_in_imbue_amel.getOverenchantLimitFor(enchantment)
                )
                : Math.max(
                    bookEnchantmentLevel,
                    itemEnchantmentLevel
                );

            if (!enchantment.isAcceptableItem(enchant)) {
                didSomething = false;
                break;
            }
            if (
                itemEnchants.keySet().stream()
                    .filter(other -> !enchantment.canCombine(other))
                    .count() > 0
            ) {
                didSomething = false;
                break;
            }

            itemEnchants.put(enchantment, bookEnchantmentLevel);
            didSomething = true;

            int raritySquared = 1 << enchantment.getRarity().ordinal();
            finalLevelCost += enchant.getCount() > 1
                ? 40
                : Math.max(1, raritySquared / 2) * finalEnchantmentLevel;
        }

        if (!didSomething) return;

        if (newItemName != null && !newItemName.isBlank()) {
            if (!newItemName.equals(enchant.getName().toString())) {
                finalLevelCost += 1;
                enchant.setCustomName(Text.literal(newItemName));
            }
        } else if (enchant.hasCustomName()) {
            finalLevelCost += 1;
            enchant.removeCustomName();
        }

        if (finalLevelCost == 40 && !player.getAbilities().creativeMode) return;

        EnchantmentHelper.set(itemEnchants, enchant);
        levelCost.set(finalLevelCost);
        output.setStack(0, enchant);
        sendContentUpdates();
        ci.cancel();
    }
}
