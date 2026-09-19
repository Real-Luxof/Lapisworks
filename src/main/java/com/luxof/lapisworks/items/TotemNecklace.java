package com.luxof.lapisworks.items;

import com.luxof.lapisworks.init.ModItems;
import com.luxof.lapisworks.items.shit.ITotem;

import dev.emi.trinkets.api.SlotReference;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class TotemNecklace extends Item implements ITotem {
    public TotemNecklace() {
        super(
            new FabricItemSettings()
                .maxCount(1)
                .maxDamage(3)
        );
    }

    @Override
    public void onCraft(ItemStack stack, World world, PlayerEntity player) {
        stack.setDamage(2);
    }

    @Override
    public void revive(
        LivingEntity entity,
        ItemStack stack,
        SlotReference slot
    ) {
        entity.setHealth(1.0f);
        entity.clearStatusEffects();
        entity.getWorld().sendEntityStatus(entity, (byte)35);
        stack.damage(1, entity, whatever -> {});
    }

    @Override
    public ItemStack getFloatingItemToShowClientPlayerOnRevive(
        ItemStack stack,
        SlotReference slot
    ) {
        return new ItemStack(ModItems.TOTEM_NECKLACE_FLOATY_DISPLAY);
    }

    @Override
    public boolean isEnchantable(ItemStack stack) {
        return false;
    }
}
