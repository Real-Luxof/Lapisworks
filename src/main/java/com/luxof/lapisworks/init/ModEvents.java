package com.luxof.lapisworks.init;

import com.luxof.lapisworks.mixinsupport.CollarControllable;
import com.luxof.lapisworks.mixinsupport.LapisworksInterface;

import static com.luxof.lapisworks.init.ModItems.COLLAR;

import com.luxof.lapisworks.collar.LapisCollarAdditions;

import dev.onyxstudios.cca.api.v3.entity.PlayerCopyCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerChunkEvents;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.TameableEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.hit.HitResult;

public class ModEvents {
    public static void finallyBeUsed() {
        PlayerCopyCallback.EVENT.register((op, np, alive) -> {
            if (!alive) ((LapisworksInterface)np).copyCrossDeath(op);
            else ((LapisworksInterface)np).copyCrossDimensional(op);
        });
        
        ServerChunkEvents.CHUNK_UNLOAD.register((world, chunk) -> {
            var state = PersistentStateCircleBlockCache.getState(world);
            state.stopCachingChunk(chunk.getPos());
        });

        /*UseEntityCallback.EVENT.register((plr, world, hand, entity, hitRes) -> {
            ItemStack stack = plr.getStackInHand(hand);
            if (
                plr.isSpectator() ||
                (!(entity instanceof LivingEntity living)) ||
                (hitRes != null && hitRes.getType() == HitResult.Type.MISS) ||
                !(entity instanceof CollarControllable collarable) ||
                (plr.isSneaking() ? !stack.isEmpty() : !stack.isOf(COLLAR)) ||
                (entity instanceof TameableEntity tameable && !tameable.isOwner(plr))
            ) return ActionResult.PASS;

            ItemStack alreadyThereCollar = collarable.getCollar();
            plr.setStackInHand(hand, collarable.setCollar(stack));
            if (!alreadyThereCollar.isEmpty())
                LapisCollarAdditions.toAllAdditions(
                    alreadyThereCollar,
                    (addition, id) -> addition.onUnequip(alreadyThereCollar, living)
                );
            if (!stack.isEmpty())
                LapisCollarAdditions.toAllAdditions(
                    alreadyThereCollar,
                    (addition, id) -> addition.onEquip(alreadyThereCollar, living)
                );

            return ActionResult.SUCCESS;
        });*/

        // i'll use events for it.. eventually...
        /*
        AttackEntityCallback.EVENT.register((plr, world, hand, entity, hitRes) -> {
            if (hitRes.getType() == HitResult.Type.MISS) return ActionResult.PASS;
        });
        */
    }
}
