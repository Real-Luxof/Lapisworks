package com.luxof.lapisworks.chalk;

import at.petrak.hexcasting.api.casting.ParticleSpray;
import at.petrak.hexcasting.api.casting.eval.CastResult;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.MishapEnvironment;
import at.petrak.hexcasting.api.casting.eval.sideeffects.OperatorSideEffect;
import at.petrak.hexcasting.api.pigment.FrozenPigment;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import org.jetbrains.annotations.Nullable;

public class RitualCastEnv extends CastingEnvironment {
    protected final RitualExecutionState ritual;

    public RitualCastEnv(ServerWorld world, RitualExecutionState ritual) {
        super(world);
        this.ritual = ritual;
    }

    public RitualExecutionState ritual() { return ritual; }

    @Override
    @Nullable
    public ServerPlayerEntity getCaster() {
        return getCastingEntity() instanceof ServerPlayerEntity sp ? sp : null;
    }

    @Override
    public @Nullable LivingEntity getCastingEntity() {
        return ritual.getCaster(world);
    }

    @Override
    public MishapEnvironment getMishapEnvironment() {
        return new RitualMishapEnv(world, ritual);
    }

    @Override
    public Vec3d mishapSprayPos() {
        return ritual.getMishapSprayPos();
    }

    @Override
    protected long extractMediaEnvironment(long cost, boolean simulate) {
        return cost - ritual.extractMedia(cost, simulate, getWorld());
    }

    @Override
    protected boolean isVecInRangeEnvironment(Vec3d vec) {
        return ritual.isVecInAmbit(vec, getWorld());
    }

    @Override
    protected boolean hasEditPermissionsAtEnvironment(BlockPos pos) { return true; }

    @Override
    public Hand getCastingHand() {
        return Hand.MAIN_HAND;
    }

    @Override
    protected List<ItemStack> getUsableStacks(StackDiscoveryMode mode) {
        if (getCastingEntity() instanceof ServerPlayerEntity sp)
            return getUsableStacksForPlayer(mode, null, sp);

        return new ArrayList<>();
    }

    @Override
    protected List<HeldItemInfo> getPrimaryStacks() {
        if (getCastingEntity() instanceof ServerPlayerEntity sp)
            return getPrimaryStacksForPlayer(Hand.OFF_HAND, sp);

        return List.of();
    }

    @Override
    public boolean replaceItem(Predicate<ItemStack> stackOk, ItemStack replaceWith, @Nullable Hand hand) {
        if (getCastingEntity() instanceof ServerPlayerEntity sp)
            return replaceItemForPlayer(stackOk, replaceWith, hand, sp);

        return false;
    }

    @Override
    public FrozenPigment getPigment() {
        return ritual.getPigment();
    }

    @Override
    public @Nullable FrozenPigment setPigment(@Nullable FrozenPigment pigment) {
        return ritual.setPigment(pigment);
    }

    @Override
    public void produceParticles(ParticleSpray particles, FrozenPigment colorizer) {
        particles.sprayParticles(world, colorizer);
    }

    @Override
    public void printMessage(Text message) {
        ritual.printMessage(message, getWorld());
    }

    public void printMishap(Text mishapMessage) {
        ritual.printMishap(mishapMessage, getWorld());
    }

    protected void printMishapMessage(OperatorSideEffect.DoMishap mishap) {
        Text message = mishap.getMishap().errorMessageWithName(this, mishap.getErrorCtx());
        if (message != null) printMishap(message);
    }

    @Override
    public void postExecution(CastResult result) {
        super.postExecution(result);

        for (OperatorSideEffect sideEffect : result.getSideEffects()) {
            if (sideEffect instanceof OperatorSideEffect.DoMishap mishap)
                printMishapMessage(mishap);
        }
    }
}
