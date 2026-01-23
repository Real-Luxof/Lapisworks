package com.luxof.lapisworks.mixin;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment.HeldItemInfo;
import at.petrak.hexcasting.api.casting.eval.env.CircleCastEnv;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;

import com.luxof.lapisworks.VAULT.CastEnvVAULT;
import com.luxof.lapisworks.VAULT.VAULT;
import com.luxof.lapisworks.mixinsupport.GetStacks;
import com.luxof.lapisworks.mixinsupport.GetVAULT;

import static com.luxof.lapisworks.Lapisworks.interactWithLinkableMediaBlocks;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

// who up mixing they ins rn
@Mixin(value = CastingEnvironment.class, remap = false)
public abstract class CastingEnvironmentMixin implements GetStacks, GetVAULT {
    @Unique private final VAULT vault = new CastEnvVAULT((CastingEnvironment)(Object)this);
    @Unique @Override public VAULT grabVAULT() {
        ((CastEnvVAULT)vault).initInnerServPlayerVAULT(); // <-- silly shenanigans that are required
        return this.vault;
    }

    @Shadow
    protected abstract List<HeldItemInfo> getPrimaryStacks();
    @Unique @Override
    public List<HeldItemInfo> getHeldStacks() {
        List<HeldItemInfo> stacks = new ArrayList<>(this.getPrimaryStacks());
        if (stacks.size() == 2 && stacks.get(0).hand() != Hand.MAIN_HAND) {
            // hexcasting does it the wrong way (they do offhand first then mainhand)
            HeldItemInfo buffer = stacks.get(0);
            stacks.set(0, stacks.get(1));
            stacks.set(1, buffer);
        }
        return List.copyOf(stacks);
    }
    @Unique @Override
    public List<HeldItemInfo> getHeldStacksOtherFirst() { return this.getPrimaryStacks(); }
    @Unique @Override
    public List<ItemStack> getHeldItemStacks() {
        return this.getHeldStacks().stream().map((held) -> held.stack()).toList();
    }
    @Unique @Override
    public List<ItemStack> getHeldItemStacksOtherFirst() {
        return this.getHeldStacksOtherFirst().stream().map((held) -> held.stack()).toList();
    }



    @WrapMethod(method = "extractMedia")
    public long extractMedia(
        long cost,
        boolean simulate,
        Operation<Long> og
    ) {
        cost = og.call(cost, simulate);
        if (!((Object)this instanceof CircleCastEnv)) return cost;
        CircleCastEnv env = (CircleCastEnv)(Object)this;
        long interactAmount = interactWithLinkableMediaBlocks(
            env.getWorld(),
            Set.of(env.circleState().impetusPos),
            cost,
            false,
            simulate
        );
        //LOGGER.info("cost, interactAmount: " + cost + ", " + interactAmount);
        cost -= interactAmount;
        return cost;
    }
}
