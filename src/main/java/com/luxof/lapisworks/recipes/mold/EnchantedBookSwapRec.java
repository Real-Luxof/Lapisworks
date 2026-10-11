package com.luxof.lapisworks.recipes.mold;

import static com.luxof.lapisworks.Lapisworks.LOGGER;

import com.luxof.lapisworks.init.ModItems;
import com.luxof.lapisworks.inv.HandsInv;

import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;

public class EnchantedBookSwapRec extends DynamicMoldRec {
    public EnchantedBookSwapRec(Identifier id) {
        super(id);
    }

    @Override
    public ItemStack craft(HandsInv inventory) {
        ItemStack oldStack = inventory.getHands().stream()
            .filter(stack ->
                stack.isOf(Items.ENCHANTED_BOOK) || stack.isOf(ModItems.SUPER_ENCHANTED_BOOK)
            )
            .findFirst()
            .get();
        ItemStack newStack = new ItemStack(
            oldStack.isOf(Items.ENCHANTED_BOOK)
                ? ModItems.SUPER_ENCHANTED_BOOK
                : EnchantmentHelper.get(oldStack).entrySet().stream()
                    .filter(entry -> entry.getValue() < entry.getKey().getMaxLevel())
                    .count() > 0
                    ? ModItems.SUPER_ENCHANTED_BOOK
                    : Items.ENCHANTED_BOOK,
            oldStack.getCount()
        );
        newStack.setNbt(oldStack.getNbt());

        return newStack;
    }

    @Override
    public RecipeSerializer<?> getSerializer() {
        return EnchantedBookSwapRecSerializer.INSTANCE;
    }

    @Override
    public boolean matches(ItemStack stack) {
        return stack.isOf(Items.ENCHANTED_BOOK) || stack.isOf(ModItems.SUPER_ENCHANTED_BOOK);
    }
}
