package com.luxof.lapisworks.frames;

import at.petrak.hexcasting.api.casting.SpellList;
import at.petrak.hexcasting.api.casting.eval.CastResult;
import at.petrak.hexcasting.api.casting.eval.ResolvedPatternType;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import at.petrak.hexcasting.api.casting.eval.vm.CastingVM;
import at.petrak.hexcasting.api.casting.eval.vm.ContinuationFrame;
import at.petrak.hexcasting.api.casting.eval.vm.FrameEvaluate;
import at.petrak.hexcasting.api.casting.eval.vm.FrameForEach;
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.ListIota;
import at.petrak.hexcasting.api.casting.iota.Vec3Iota;
import at.petrak.hexcasting.api.utils.HexUtils;
import at.petrak.hexcasting.common.lib.hex.HexEvalSounds;
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes;

import static com.luxof.lapisworks.Lapisworks.deserializeVec3d;
import static com.luxof.lapisworks.Lapisworks.serializeVec3d;

import java.util.ArrayList;
import java.util.List;

import kotlin.Pair;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class FrameExecuteOnSphere implements ContinuationFrame, IThothsLikeFrame {
    public SpellList instrs;
    public List<? extends Iota> baseStack;
    public Vec3d offset;
    public int at;
    public BlockPos center;
    public int circumference;
    public int radiusSqr;
    public int innerRadSqr;
    public boolean hollow;

    public FrameExecuteOnSphere(
        SpellList instrs,
        List<? extends Iota> baseStack,
        Vec3d offset,
        int radius,
        boolean hollow
    ) {
        this.instrs = instrs;
        this.baseStack = baseStack;
        this.offset = offset;
        this.center = BlockPos.ofFloored(radius / 2, radius / 2, radius / 2);
        this.circumference = radius*2;
        this.radiusSqr = radius*radius;
        this.innerRadSqr = hollow ? (int)Math.pow(radius - 1, 2.0) : 0;
        this.hollow = hollow;
    }
    public FrameExecuteOnSphere(
        SpellList instrs,
        List<? extends Iota> baseStack,
        Vec3d offset,
        int radius,
        boolean hollow,
        int at
    ) {
        this(instrs, baseStack, offset, radius, hollow);
        this.at = at;
    }

    @Override
    public Pair<Boolean, List<Iota>> breakDownwards(List<? extends Iota> stack) {
        return new Pair<Boolean, List<Iota>>(
            true,
            List.copyOf(stack)
        );
    }


    public Pair<Integer, Vec3Iota> getNextAt() {
        int nextAt = this.at + 1;
        BlockPos atPos = new BlockPos(0, 0, 0);
        int iterationLimit = circumference*circumference*circumference;

        for(; nextAt < iterationLimit; nextAt++) {
            atPos = new BlockPos(
                nextAt % circumference,
                (nextAt / circumference) % circumference,
                (nextAt / (circumference*circumference))
            );
            double distanceSqr = atPos.getSquaredDistance(offset);
            if (distanceSqr <= radiusSqr && distanceSqr >= innerRadSqr) {
                break;
            }
        }

        return new Pair<>(
            nextAt,
            new Vec3Iota(offset.add(new Vec3d(atPos.getX(), atPos.getY(), atPos.getZ())))
        );
    }

    @Override
    public CastResult evaluate(SpellContinuation cont, ServerWorld world, CastingVM vm) {
        CastingImage oldImg = vm.getImage();
        List<Iota> stack = new ArrayList<>(baseStack);
        CastingImage newImg = oldImg.withResetEscape().copy(
            stack,
            oldImg.getParenCount(),
            oldImg.getParenthesized(),
            oldImg.getEscapeNext(),
            oldImg.getOpsConsumed(),
            oldImg.getUserData()
        );
        SpellContinuation newCont = cont;
        if (at < circumference*circumference*circumference) {
            var next = getNextAt();

            newCont = cont.pushFrame(new FrameExecuteOnSphere(instrs, baseStack, offset, circumference/2, hollow, next.getFirst()))
                          .pushFrame(new FrameEvaluate(instrs, true));
            stack.add(next.getSecond());
            newImg = newImg.withUsedOp();
        }
        return new CastResult(
            new ListIota(instrs),
            newCont,
            newImg,
            List.of(),
            ResolvedPatternType.EVALUATED,
            HexEvalSounds.THOTH
        );
    }

    public static final Type<FrameExecuteOnSphere> TYPE = new Type<>() {
        @SuppressWarnings("null")
        @Override
        public FrameExecuteOnSphere deserializeFromNBT(NbtCompound nbt, ServerWorld world) {
            List<Iota> newStack = new ArrayList<>();
            HexIotaTypes.LIST.deserialize(
                nbt.getList("baseStack", NbtElement.COMPOUND_TYPE),
                world
            ).getList().forEach(newStack::add);

            return new FrameExecuteOnSphere(
                HexIotaTypes.LIST.deserialize(
                    nbt.getList("instrs", NbtElement.COMPOUND_TYPE),
                    world
                ).getList(),
                newStack,
                deserializeVec3d(nbt.get("offset")),
                nbt.getInt("radius"),
                nbt.getBoolean("hollow"),
                nbt.getInt("at")
            );
        }
    };

    @Override
    public Type<?> getType() {
        return TYPE;
    }

    @Override
    public NbtCompound serializeToNBT() {
        NbtCompound nbt = new NbtCompound();
        nbt.put("instrs", HexUtils.serializeToNBT(instrs));
        nbt.put("baseStack", HexUtils.serializeToNBT(baseStack));
        nbt.put("offset", serializeVec3d(offset));
        nbt.putInt("radius", circumference/2);
        nbt.putBoolean("hollow", hollow);
        nbt.putInt("at", at);
        return nbt;
    }

    @Override
    public int size() { return instrs.size() + baseStack.size(); }


    @Override
    public List<Iota> getDatum() {
        List<Iota> accumulator = new ArrayList<>();
        int nextAt = this.at + 1;
        Vec3d atPos;
        int iterationLimit = circumference*circumference*circumference;

        for(; nextAt < iterationLimit; nextAt++) {
            atPos = new Vec3d(
                nextAt % circumference,
                (nextAt / circumference) % circumference,
                (nextAt / (circumference*circumference))
            );
            double distanceSqr = atPos.squaredDistanceTo(offset);
            if (distanceSqr > radiusSqr || distanceSqr < innerRadSqr)
                continue;
            accumulator.add(new Vec3Iota(atPos));
        }

        return accumulator;
    }
    @Override
    public ContinuationFrame withDatum(List<Iota> newDatum) {
        return new FrameForEach(new SpellList.LList(newDatum), instrs, baseStack, List.of());
    }
    @Override
    public SpellList getHex() {
        return instrs;
    }
    @Override
    public ContinuationFrame withHex(SpellList hex) {
        return new FrameExecuteOnSphere(hex, baseStack, offset, circumference/2, hollow, at);
    }
}
