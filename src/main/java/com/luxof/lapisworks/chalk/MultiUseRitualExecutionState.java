package com.luxof.lapisworks.chalk;

import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.pigment.FrozenPigment;

import com.luxof.lapisworks.LapisConfig;
import com.luxof.lapisworks.Lapisworks;
import com.luxof.lapisworks.blocks.entities.RitusEntity;

import static com.luxof.lapisworks.Lapisworks.getPigmentFromDye;
import static com.luxof.lapisworks.Lapisworks.nbtListOf;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import org.jetbrains.annotations.Nullable;

public class MultiUseRitualExecutionState extends RitualExecutionState {
    public BlockPos startingPos;
    public List<BlockPos> visitedPositions;

    public MultiUseRitualExecutionState(
        BlockPos currentPos,
        Direction forward,
        CastingImage currentImage,
        @Nullable UUID caster,
        @Nullable FrozenPigment pigment,
        @Nullable Iota tunedFrequency,
        BlockPos startingPos,
        List<BlockPos> visitedPositions
    ) {
        super(currentPos, forward, currentImage, caster, pigment);
        this.startingPos = startingPos;
        this.visitedPositions = new ArrayList<>(visitedPositions);
    }

    private double retrieveTuneableAmbitMul() {
        return LapisConfig.getCurrentConfig()
            .getMultiUseRitualSettings()
            .tuneable_amethyst_ambit_multiplier();
    }
    @Override
    public boolean isVecInAmbit(Vec3d vec, ServerWorld world) {
        return isVecInAmbitOfTuneableAmethyst(vec, world, retrieveTuneableAmbitMul());
    }

    @Override
    public void save(NbtCompound nbt) {
        saveBase(nbt);
        nbt.put("startingPos", Lapisworks.serializeBlockPos(startingPos));
        nbt.put(
            "visitedPositions",
            nbtListOf(
                visitedPositions.stream()
                    .map(Lapisworks::serializeBlockPos)
                    .toList()
            )
        );
    }

    public static MultiUseRitualExecutionState load(NbtCompound nbt, ServerWorld world) {
        BaseConstructorArguments base = loadBase(nbt, world);
        return new MultiUseRitualExecutionState(
            base.currentPos(),
            base.forward(),
            base.currentImage(),
            base.caster(),
            base.pigment(),
            base.tunedFrequency(),
            Lapisworks.deserializeBlockPos(nbt.getCompound("startingPos")),
            nbt.getList("visitedPositions", NbtElement.COMPOUND_TYPE)
                .stream()
                .map(e -> Lapisworks.deserializeBlockPos((NbtCompound)e))
                .toList()
        );
    }

    @Override
    public long extractMedia(long cost, boolean simulate, ServerWorld world) {
        return ((RitusEntity)world.getBlockEntity(startingPos)).withdrawMedia(cost, simulate);
    }

    @Override
    public void printMessage(Text message, ServerWorld world) {
        ((RitusEntity)world.getBlockEntity(startingPos)).postPrint(message);
    }

    @Override
    public void printMishap(Text mishapMessage, ServerWorld world) {
        ((RitusEntity)world.getBlockEntity(startingPos)).postMishap(mishapMessage);
    }

    @Override
    public Vec3d getMishapSprayPos() {
        return Vec3d.ofCenter(currentPos);
    }

    @Override
    public boolean tick(ServerWorld world) {
        RitusEntity ritus = (RitusEntity)world.getBlockEntity(startingPos);
        tunedFrequency = ritus.getTunedFrequency(world);

        RitualCastEnv env = new RitualCastEnv(world, this);

        if (!(world.getBlockEntity(currentPos) instanceof RitualComponent ritualComponent)) {
            return false;
        }

        visitedPositions.add(currentPos);
        Pair<BlockPos, CastingImage> result = ritualComponent.execute(env);
        unpowerTrailing(world, 5);

        if (result == null || result.getLeft() == null) {

            sprayParticlesOutOf(
                world,
                startingPos,
                (RitualComponent)world.getBlockEntity(startingPos),
                getPigmentFromDye(DyeColor.RED)
            );

            unpowerTrailing(world, 0);
            return false;

        }

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

            if (!(world.getBlockEntity(pos) instanceof RitualComponent ritualComponent))
                continue;

            ritualComponent.unpower();
        }
    }
}
