package com.luxof.lapisworks.mixin;

import at.petrak.hexcasting.api.casting.eval.CastResult;
import at.petrak.hexcasting.api.casting.eval.OperationResult;
import at.petrak.hexcasting.api.casting.eval.vm.CastingVM;
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation;
import at.petrak.hexcasting.api.casting.iota.EntityIota;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.PatternIota;

import com.llamalad7.mixinextras.sugar.Local;

import com.luxof.lapisworks.interop.hierophantics.data.Amalgamation;
import com.luxof.lapisworks.interop.valkyrienskies.ValkyrienUtils;
import com.luxof.lapisworks.mixinsupport.ChariotServerPlayer;

import static com.luxof.lapisworks.Lapisworks.VALKYRIEN_SKIES_INTEROP;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

import org.jetbrains.annotations.NotNull;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import robotgiggle.hierophantics.HieroMindCastEnv;

@Mixin(value = PatternIota.class, remap = false)
public class PatternIotaMixin2 {
    private ArrayList<Iota> oldStack = new ArrayList<>();
    @Inject(
        method = "lookupAndOperate",
        at = @At("HEAD")
    )
    public @NotNull void execute(
        CastingVM vm,
        ServerWorld world,
        SpellContinuation continuation,
        CallbackInfoReturnable<CastResult> cir
    ) {
        if (
            vm.getEnv() instanceof HieroMindCastEnv &&
            vm.getImage().getUserData().getBoolean("counterspell_cast")
        )
            return;
        oldStack = new ArrayList<>(vm.getImage().getStack());
    }

    @Unique
    private ArrayList<ChariotServerPlayer> getOnlyPlayers(Iterable<Iota> stack) {
        ArrayList<ChariotServerPlayer> players = new ArrayList<>();
        for (Iota iota : stack) {
            if (
                iota instanceof EntityIota entIota &&
                entIota.getEntity() instanceof ChariotServerPlayer player
            )
                players.add(player);

            Iterable<Iota> sub = iota.subIotas();
            if (sub != null)
                players.addAll(getOnlyPlayers(sub));
        }
        return players;
    }

    @Inject(
        method = "lookupAndOperate",
        at = @At(
            value = "NEW",
            target = "at/petrak/hexcasting/api/casting/eval/CastResult"
        ),
        locals = LocalCapture.CAPTURE_FAILHARD
    )
    public @NotNull void execute(
        CastingVM vm,
        ServerWorld world,
        SpellContinuation continuation,
        CallbackInfoReturnable<CastResult> cir,
        @Local OperationResult result
    ) {
        if (
            vm.getEnv() instanceof HieroMindCastEnv &&
            vm.getImage().getUserData().getBoolean("counterspell_cast")
        )
            return;

        var newStack = new ArrayList<>(result.getNewImage().getStack());
        newStack.removeAll(oldStack);

        Vec3d thisPos = vm.getEnv().mishapSprayPos();
        Set<ChariotServerPlayer> relevantPlayers = Set.copyOf(getOnlyPlayers(newStack));

        ServerPlayerEntity ohByTheWayThisIsTheCaster = vm.getEnv()
            .getCastingEntity() instanceof ServerPlayerEntity player
                ? player
                : null;
        ServerWorld comingFromWorld = vm.getEnv().getWorld();

        for (ChariotServerPlayer sp : relevantPlayers) {
            ServerPlayerEntity spe = (ServerPlayerEntity)sp;
            if (
                spe == ohByTheWayThisIsTheCaster ||
                spe.getServerWorld() != comingFromWorld
            )
                continue;

            List<Amalgamation> amalgams = sp.getFusedAmalgamations();
            int usedAmalgams = sp.getUsedAmalgamsThisTick();

            for (Amalgamation amalgam : amalgams.subList(usedAmalgams, amalgams.size())) {

                double distance = VALKYRIEN_SKIES_INTEROP
                    ? ValkyrienUtils.distance(comingFromWorld, thisPos, spe.getPos())
                    : thisPos.distanceTo(spe.getPos());

                if (distance > amalgam.range)
                    continue;
                else if (amalgam.willCast())
                    amalgam.cast(spe, thisPos);
                sp.incrementUsedAmalgamsThisTick();
            }
        }
    }
}
