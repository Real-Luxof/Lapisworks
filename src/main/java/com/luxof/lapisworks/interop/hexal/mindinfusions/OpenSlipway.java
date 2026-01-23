package com.luxof.lapisworks.interop.hexal.mindinfusions;

import at.petrak.hexcasting.api.casting.ParticleSpray;

import com.luxof.lapisworks.Lapisworks;
import com.luxof.lapisworks.VAULT.Flags;
import com.luxof.lapisworks.init.Mutables.Mutables;
import com.luxof.lapisworks.init.Mutables.SMindInfusion;
import com.luxof.lapisworks.interop.hexal.Lapisal;

import static com.luxof.lapisworks.LapisworksIDs.AMEL;
import static com.luxof.lapisworks.LapisworksIDs.CLUEDIN_ADVANCEMENT;
import static com.luxof.lapisworks.MishapThrowerJava.assertItemAmount;

import net.minecraft.advancement.Advancement;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.passive.AllayEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.DyeColor;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World.ExplosionSourceType;

import ram.talia.hexal.common.entities.WanderingWisp;
import ram.talia.hexal.common.lib.HexalBlocks;

public class OpenSlipway extends SMindInfusion {
    private int neededAmel = 48;

    @Override
    public boolean testBlock() {
        if (!ctx.isEnlightened()) return false;
        return ctx.getWorld().getBlockState(blockPos).isOf(HexalBlocks.SLIPWAY);
    }

    @Override
    public void mishapIfNeeded() {
        assertItemAmount(ctx, Mutables::isAmel, AMEL, neededAmel);
    }

    @Override
    public void accept() {
        ServerWorld world = ctx.getWorld();
        LivingEntity castingEntity = ctx.getCastingEntity();
        if (castingEntity instanceof ServerPlayerEntity sp) {
            Advancement cluedIn = sp.getServer().getAdvancementLoader().get(CLUEDIN_ADVANCEMENT);
            if (!sp.getAdvancementTracker().getProgress(cluedIn).isDone())
                sp.getAdvancementTracker().grantCriterion(cluedIn, "grant");
        }
        vault.drain(Mutables::isAmel, neededAmel, false, Flags.PRESET_UpToHotbar);
        ParticleSpray particles = ParticleSpray.burst(blockPos.toCenterPos(), 5, 50);
        particles.sprayParticles(world, Lapisworks.getPigmentFromDye(DyeColor.PURPLE));
        world.createExplosion(
            null,
            blockPos.getX() + 0.5,
            blockPos.getY() + 0.5,
            blockPos.getZ() + 0.5, 0.25F,
            ExplosionSourceType.NONE
        );
        world.setBlockState(blockPos, Lapisal.ENCH_SLIPWAY.getDefaultState());
        world.playSound(
            null,
            blockPos,
            SoundEvents.BLOCK_END_PORTAL_SPAWN,
            SoundCategory.BLOCKS,
            2.0f,
            0.85f
        );
        Vec3d riftPos = blockPos.toCenterPos();
        Box aabb = Box.of(riftPos, 32.0, 32.0, 32.0);
        for (
            LivingEntity entity :
            world.getEntitiesByClass(
                LivingEntity.class,
                aabb,
                any -> !(any instanceof AllayEntity)
            )
        ) {
            Vec3d entPos = entity.getPos();
            Vec3d push = entPos.subtract(riftPos).normalize().multiply(
                2.5 * Math.max(1.0, (1.0 / riftPos.subtract(entPos).length()) / 5.0)
            );
            entity.addVelocity(push);
            entity.velocityModified = true;
        }

        for (int i = 0; i < world.random.nextBetween(2, 10); i++) {
            WanderingWisp wisp = new WanderingWisp(world, riftPos);
            wisp.setPigment(Lapisworks.getRandomPigment(world.random));
            world.spawnEntity(wisp);
        }
    }
}
