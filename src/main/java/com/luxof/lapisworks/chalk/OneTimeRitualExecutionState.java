package com.luxof.lapisworks.chalk;

import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import at.petrak.hexcasting.api.pigment.FrozenPigment;

import com.luxof.lapisworks.LapisConfig;
import com.luxof.lapisworks.Lapisworks;
import com.luxof.lapisworks.LapisConfig.OneTimeRitualSettings;

import static com.luxof.lapisworks.Lapisworks.getPigmentFromDye;
import static com.luxof.lapisworks.Lapisworks.nbtListOf;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.minecraft.block.Blocks;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import org.jetbrains.annotations.Nullable;

public class OneTimeRitualExecutionState extends RitualExecutionState {
    private OneTimeRitualSettings getSettings() {
        return LapisConfig.getCurrentConfig().getOneTimeRitualSettings();
    }
    private double getPlayerAmbitMult() {
        return getSettings().player_ambit_multiplier();
    }
    private double getTuneableAmbitMult() {
        return getSettings().tuneable_amethyst_ambit_multiplier();
    }

    public long media;
    public UUID casterWhileCasterIsDisabled = null;
    public boolean isCasterDisabled = false;
    public List<BlockPos> visitedPositions;

    public OneTimeRitualExecutionState(
        BlockPos currentPos,
        Direction forward,
        CastingImage currentImage,
        @Nullable UUID caster,
        @Nullable FrozenPigment pigment,
        long startingMedia,
        List<BlockPos> visitedPositions
    ) {
        super(currentPos, forward, currentImage, caster, pigment);
        this.media = startingMedia;
        this.visitedPositions = new ArrayList<>(visitedPositions);
    }

    public void disableCaster() {
        if (isCasterDisabled) return;
        casterWhileCasterIsDisabled = caster;
        caster = null;
        isCasterDisabled = true;
    }

    public void enableCaster() {
        if (!isCasterDisabled) return;
        caster = casterWhileCasterIsDisabled;
        casterWhileCasterIsDisabled = caster;
        isCasterDisabled = false;
    }

    /** bypasses disabled casters and shiet. */
    @Nullable
    public ServerPlayerEntity getCasterAbsolute(ServerWorld world) {
        if (!isCasterDisabled)
            return getCaster(world);
        else if (world.getEntity(casterWhileCasterIsDisabled) instanceof ServerPlayerEntity sp)
            return sp;
        else
            return null;
    }

    @Override
    public boolean isVecInAmbit(Vec3d vec, ServerWorld world) {
        return isVecInAmbitOfPlayer(vec, world, getPlayerAmbitMult())
            || isVecInAmbitOfTuneableAmethyst(vec, world, getTuneableAmbitMult());
    }

    @Override
    public void save(NbtCompound nbt) {
        super.saveBase(nbt);
        nbt.putLong("media", media);
        nbt.put(
            "visitedPositions",
            nbtListOf(
                visitedPositions.stream()
                    .map(Lapisworks::serializeBlockPos)
                    .toList()
            )
        );
    }

    public static OneTimeRitualExecutionState load(NbtCompound nbt, ServerWorld world) {
        BaseConstructorArguments base = loadBase(nbt, world);
        return new OneTimeRitualExecutionState(
            base.currentPos(),
            base.forward(),
            base.currentImage(),
            base.caster(),
            base.pigment(),
            nbt.getLong("media"),
            nbt.getList("visitedPositions", NbtElement.COMPOUND_TYPE)
                .stream()
                .map(e -> Lapisworks.deserializeBlockPos((NbtCompound)e))
                .toList()
        );
    }

    @Override
    public long extractMedia(long cost, boolean simulate, ServerWorld world) {
        long take = Math.min(cost, media);
        if (!simulate) media -= take;
        return take;
    }

    @Override
    public void printMessage(Text message, ServerWorld world) {
        ServerPlayerEntity caster = getCasterAbsolute(world);
        if (caster == null) return;
        caster.sendMessage(message);
    }

    @Override
    public void printMishap(Text mishapMessage, ServerWorld world) {
        printMessage(mishapMessage, world);
    }

    @Override
    public Vec3d getMishapSprayPos() {
        return Vec3d.ofCenter(currentPos);
    }

    @Override
    public boolean tick(ServerWorld world) {
        RitualCastEnv env = new RitualCastEnv(world, this);

        if (!(world.getBlockEntity(currentPos) instanceof RitualComponent ritualComponent)) {
            // explosions, Break Block, damn near anything could cause this.
            return false;
        }

        visitedPositions.add(currentPos);
        Pair<BlockPos, CastingImage> result = ritualComponent.execute(env);
        unpowerTrailing(world, 1);

        if (result == null || result.getLeft() == null) {
            world.setBlockState(currentPos, Blocks.AIR.getDefaultState());
            return false;
        }

        FrozenPigment particleP = pigment == null ? getPigmentFromDye(DyeColor.PINK) : pigment;
        sprayParticlesOutOf(world, currentPos, ritualComponent, particleP);

        forward = Direction.fromVector(
            result.getLeft().getX() - currentPos.getX(),
            result.getLeft().getY() - currentPos.getY(),
            result.getLeft().getZ() - currentPos.getZ()
        );
        currentPos = result.getLeft();
        currentImage = result.getRight();

        return true;
    }

    private void unpowerTrailing(ServerWorld world, int trailLength) {
        if (visitedPositions.size() <= trailLength) return;
        for (
            int i = visitedPositions.size() - 1 - trailLength;
            i >= 0;
            i--
        ) {
            BlockPos pos = visitedPositions.remove(i);

            if (!(world.getBlockEntity(pos) instanceof RitualComponent))
                continue;

            world.setBlockState(pos, Blocks.AIR.getDefaultState());
        }
    }
}
