package com.luxof.lapisworks.VAULT;

import static com.luxof.lapisworks.LapisworksIDs.PLAYER_VAULT;

import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;

/** old me was undergoing 7 aneurysms at once to write whatever the fuck that was */
public class ServPlayerVAULT extends VAULT {
    private final ServerPlayerEntity player;
    public ServPlayerVAULT(ServerPlayerEntity player) { this.player = player; }
    @Override public Identifier getKindOfVault() { return PLAYER_VAULT; }

    @Override
    protected List<ItemStack> getTrinkets() {
        Optional<TrinketComponent> trinkCompOpt = TrinketsApi.getTrinketComponent(player);

        if (trinkCompOpt.isEmpty()) return List.of();

        return trinkCompOpt.get().getAllEquipped().stream().map(pair -> pair.getRight()).toList();
    }
    @Override
    protected List<ItemStack> getHands() {
        return List.of(player.getStackInHand(Hand.MAIN_HAND), player.getStackInHand(Hand.OFF_HAND));
    }
    @Override
    protected List<ItemStack> getHotbar() {
        PlayerInventory inv = player.getInventory();
        ArrayList<ItemStack> ret = new ArrayList<>();

        for (int i = 0; i < PlayerInventory.getHotbarSize(); i++) {
            ret.add(inv.getStack(i));
        }

        return ret;
    }
    @Override
    protected List<ItemStack> getInventory() {
        PlayerInventory inv = player.getInventory();
        ArrayList<ItemStack> ret = new ArrayList<>();

        for (int i = PlayerInventory.getHotbarSize(); i < inv.size(); i++) {
            ret.add(inv.getStack(i));
        }

        return ret;
    }
}
