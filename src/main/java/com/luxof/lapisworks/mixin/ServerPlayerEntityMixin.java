package com.luxof.lapisworks.mixin;

import at.petrak.hexcasting.api.casting.ParticleSpray;
import at.petrak.hexcasting.xplat.IXplatAbstractions;

import com.luxof.lapisworks.VAULT.ServPlayerVAULT;
import com.luxof.lapisworks.VAULT.VAULT;
import com.luxof.lapisworks.interop.hextended.items.AmelOrb;
import com.luxof.lapisworks.mixinsupport.EnchSentInterface;
import com.luxof.lapisworks.mixinsupport.GetVAULT;

import static com.luxof.lapisworks.Lapisworks.getAllHands;

import com.mojang.authlib.GameProfile;

import java.util.List;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public abstract class ServerPlayerEntityMixin extends PlayerEntity implements GetVAULT {
    public ServerPlayerEntityMixin(World world, BlockPos pos, float yaw, GameProfile gameProfile) {
        super(world, pos, yaw, gameProfile);
    }

    @Unique private final VAULT vault = new ServPlayerVAULT((ServerPlayerEntity)(Object)this);
    // yall ever think of a word so long it starts lookin wrong despite being correctly spelled n shit?
    // this phenomena is called "semantic satiation" iirc
    @Unique @Override public VAULT grabVAULT() { return this.vault; }

    @Inject(at = @At("HEAD"), method = "readCustomDataFromNbt")
    public void readCustomDataFromNbt(NbtCompound nbt, CallbackInfo ci) {
        if (!nbt.getBoolean("LAPISWORKS_EnchSent_exists")) return;
        Vec3d sentPos = new Vec3d(
            nbt.getDouble("LAPISWORKS_EnchSent_posX"),
            nbt.getDouble("LAPISWORKS_EnchSent_posY"),
            nbt.getDouble("LAPISWORKS_EnchSent_posZ")
        );
        double sentAmbit = nbt.getDouble("LAPISWORKS_EnchSent_Ambit");
        ((EnchSentInterface)this).setEnchantedSentinel(sentPos, sentAmbit);
    }

    @Inject(at = @At("HEAD"), method = "writeCustomDataToNbt")
    public void writeCustomDataToNbt(NbtCompound nbt, CallbackInfo ci) {
        Vec3d sentPos = ((EnchSentInterface)this).getEnchantedSentinel();
        Double ambit = ((EnchSentInterface)this).getEnchantedSentinelAmbit();

        nbt.putBoolean("LAPISWORKS_EnchSent_exists", sentPos != null);
        if (sentPos == null) return;

        nbt.putDouble("LAPISWORKS_EnchSent_posX", sentPos.x);
        nbt.putDouble("LAPISWORKS_EnchSent_posY", sentPos.y);
        nbt.putDouble("LAPISWORKS_EnchSent_posZ", sentPos.z);
        nbt.putDouble("LAPISWORKS_EnchSent_Ambit", ambit);
    }

    @Unique
    public void spawnEnchSentParticles() {
        Vec3d sentinelPosition = ((EnchSentInterface)this).getEnchantedSentinel();
        if (sentinelPosition == null) return;

        ParticleSpray.burst(sentinelPosition, 2, 1).sprayParticles(
            (ServerWorld)this.getWorld(), // is it as simple as this? do I not need the whole chain?
            // damn, it IS as simple as that.
            IXplatAbstractions.INSTANCE.getPigment((PlayerEntity)(Object)this)
        );
    }

    @Unique
    public void spawnOrbParticles() {
        List<Hand> hands = getAllHands();
        for (Hand hand : hands) {
            ItemStack stack = this.getStackInHand(hand);
            if (!(stack.getItem() instanceof AmelOrb orb)) continue;

            Vec3d placeInAmbit = orb.getPlaceInAmbit(stack);
            ParticleSpray particles = ParticleSpray.burst(placeInAmbit, 3, 2);
            particles.sprayParticles(
                (ServerWorld)this.getWorld(),
                IXplatAbstractions.INSTANCE.getPigment((PlayerEntity)(Object)this)
            );
        }
    }

    @Inject(at = @At("HEAD"), method = "tick")
    public void tick(CallbackInfo ci) {
        this.spawnEnchSentParticles();
        this.spawnOrbParticles();
    }
}
