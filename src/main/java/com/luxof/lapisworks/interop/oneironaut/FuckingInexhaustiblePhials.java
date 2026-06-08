package com.luxof.lapisworks.interop.oneironaut;

import at.petrak.hexcasting.api.addldata.ADHexHolder;
import at.petrak.hexcasting.api.casting.eval.env.PackagedItemCastEnv;
import at.petrak.hexcasting.api.casting.eval.env.PlayerBasedCastEnv;
import at.petrak.hexcasting.xplat.IXplatAbstractions;

import java.util.ArrayList;
import java.util.List;

import net.beholderface.oneironaut.item.BottomlessMediaItem;
import net.beholderface.oneironaut.registry.OneironautItemRegistry;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class FuckingInexhaustiblePhials {
    //Get the item from registry to prevent crashing with Oneironaut Forge
    static final Item BOTTOMLESS_MEDIA_ITEM = Registries.ITEM.get(new Identifier("oneironaut", "endless_phial"));

    public static long getBottomlessContrib(PlayerBasedCastEnv ctx) {
        ServerPlayerEntity player = ((ServerPlayerEntity)ctx.getCastingEntity());

        if (
            ctx instanceof PackagedItemCastEnv
        ) {
            ItemStack trinket = player.getStackInHand(ctx.getCastingHand());
            ADHexHolder hexHolder = IXplatAbstractions.INSTANCE.findHexHolder(trinket);
            if (!hexHolder.canDrawMediaFromInventory())
                return trinket.isOf(OneironautItemRegistry.BOTTOMLESS_CASTING_ITEM.get())
                    ? IXplatAbstractions.INSTANCE.findMediaHolder(trinket).getMedia() : 0L;
        }
        PlayerInventory inv = player.getInventory();
        List<ItemStack> items = new ArrayList<>();
        items.addAll(inv.main);
        items.addAll(inv.offHand);
        items.addAll(inv.armor);
        long total = 0L;
        for (ItemStack item : items) {
            total += item.isOf(BOTTOMLESS_MEDIA_ITEM) ? ((BottomlessMediaItem)item.getItem()).getMedia(item) : 0;
        }
        return total;
    }
}
