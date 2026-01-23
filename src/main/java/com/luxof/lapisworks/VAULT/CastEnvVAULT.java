package com.luxof.lapisworks.VAULT;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;

import com.luxof.lapisworks.mixinsupport.GetStacks;
import com.luxof.lapisworks.mixinsupport.GetVAULT;

import static com.luxof.lapisworks.LapisworksIDs.CASTENV_VAULT;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class CastEnvVAULT extends VAULT {
    private final CastingEnvironment castEnv;
    private boolean inited = false;
    private VAULT playerVAULT = null;
    public CastEnvVAULT(CastingEnvironment castEnv) {
        this.castEnv = castEnv;
        /*
        // cannot init here, execState is null.
        if (castEnv.getCastingEntity() != null &&
            castEnv.getCastingEntity() instanceof ServerPlayerEntity plr) {
            playerVAULT = ((GetVAULT)plr).grabVAULT();
        } else {
            playerVAULT = null;
        }*/
    }
    @Override public Identifier getKindOfVault() { return CASTENV_VAULT; }

    // guaranteed to run only when the castenv is ready
    public void initInnerServPlayerVAULT() {
        if (inited) return;
        inited = true;
        playerVAULT = castEnv.getCastingEntity() instanceof ServerPlayerEntity plr ?
            ((GetVAULT)plr).grabVAULT() : null;
    }

    @Override
    protected List<ItemStack> getTrinkets() {
        if (playerVAULT == null) return List.of();
        return playerVAULT.getTrinkets();
    }
    @Override
    protected List<ItemStack> getHands() {
        ArrayList<ItemStack> hands = new ArrayList<>(((GetStacks)castEnv).getHeldItemStacks());

        if (playerVAULT == null) return hands;

        for (ItemStack stack : playerVAULT.getHands()) {
            // identity checks because ItemStack doesn't implement .equals(Object) yay
            if (!hands.contains(stack)) hands.add(stack);
        }
        return hands;
    }
    @Override
    protected List<ItemStack> getHotbar() {
        if (playerVAULT == null) return List.of();
        return playerVAULT.getHotbar();
    }
    @Override
    protected List<ItemStack> getInventory() {
        if (playerVAULT == null) return List.of();
        return playerVAULT.getInventory();
    }
}
