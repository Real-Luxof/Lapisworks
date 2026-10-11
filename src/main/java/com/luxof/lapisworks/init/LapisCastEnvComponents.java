package com.luxof.lapisworks.init;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironmentComponent;
import at.petrak.hexcasting.api.casting.eval.env.CircleCastEnv;
import at.petrak.hexcasting.api.casting.eval.env.PlayerBasedCastEnv;

import com.luxof.lapisworks.mixinsupport.EnchSentInterface;

import static com.luxof.lapisworks.Lapisworks.interactWithLinkableMediaBlocks;

import java.util.Set;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.Vec3d;

public class LapisCastEnvComponents {

    public static void init() {
        CastingEnvironment.addCreateEventListener((env, userData) -> {
            if (env instanceof PlayerBasedCastEnv playerEnv)
                playerEnv.addExtension(new CastingEnvironmentComponent.IsVecInRange() {

                    @Override
                    public boolean onIsVecInRange(Vec3d vec, boolean current) {
                        if (current || !(env.getCastingEntity() instanceof PlayerEntity))
                            return current;

                        EnchSentInterface player = (EnchSentInterface)playerEnv.getCastingEntity();
                        Vec3d sentPos = player.getEnchantedSentinel();
                        Double sentAmbit = player.getEnchantedSentinelAmbit();

                        if (sentPos == null) return current;
                        if (vec.squaredDistanceTo(sentPos) > sentAmbit*sentAmbit) return false;

                        return true;
                    }

                    public Key<?> key = new Key<CastingEnvironmentComponent>() {};
                    @Override public Key<?> getKey() { return key; }

                });

            if (env instanceof CircleCastEnv circleEnv)
                circleEnv.addExtension(new CastingEnvironmentComponent.ExtractMedia.Post() {

                    @Override
                    public long onExtractMedia(long cost, boolean simulate) {
                        return cost - interactWithLinkableMediaBlocks(
                            (ServerWorld)circleEnv.getImpetus().getWorld(),
                            Set.of(circleEnv.circleState().impetusPos),
                            cost,
                            false,
                            simulate
                        ).getLeft();
                    }

                    public Key<?> key = new Key<CastingEnvironmentComponent>() {};
                    @Override public Key<?> getKey() { return key; }

                });
        });
    }
}
