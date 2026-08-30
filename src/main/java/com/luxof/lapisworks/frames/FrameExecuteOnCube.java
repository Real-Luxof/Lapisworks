package com.luxof.lapisworks.frames;

import at.petrak.hexcasting.api.casting.SpellList;
import at.petrak.hexcasting.api.casting.eval.CastResult;
import at.petrak.hexcasting.api.casting.eval.ResolvedPatternType;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import at.petrak.hexcasting.api.casting.eval.vm.CastingVM;
import at.petrak.hexcasting.api.casting.eval.vm.ContinuationFrame;
import at.petrak.hexcasting.api.casting.eval.vm.FrameEvaluate;
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.ListIota;
import at.petrak.hexcasting.api.casting.iota.Vec3Iota;
import at.petrak.hexcasting.api.utils.HexUtils;
import at.petrak.hexcasting.common.lib.hex.HexEvalSounds;
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes;

import static com.luxof.lapisworks.Lapisworks.deserializeBlockPos;
import static com.luxof.lapisworks.Lapisworks.deserializeVec3d;
import static com.luxof.lapisworks.Lapisworks.serializeBlockPos;
import static com.luxof.lapisworks.Lapisworks.serializeVec3d;

import java.util.ArrayList;
import java.util.List;

import kotlin.Pair;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class FrameExecuteOnCube implements ContinuationFrame {
    public SpellList instrs;
    public List<? extends Iota> baseStack;
    public Vec3d offset;
    // questionable type choice because i didn't wanna use Vec3i (serializeBlockPos exists)
    public BlockPos size;
    public boolean hollow;
    public int at;

    public FrameExecuteOnCube(
        SpellList instrs,
        List<? extends Iota> baseStack,
        Vec3d offset,
        BlockPos size,
        boolean hollow
    ) {
        this.instrs = instrs;
        this.baseStack = baseStack;
        this.offset = offset;
        this.size = size;
        this.hollow = hollow;
    }
    public FrameExecuteOnCube(
        SpellList instrs,
        List<? extends Iota> baseStack,
        Vec3d offset,
        BlockPos size,
        boolean hollow,
        int at
    ) {
        this(instrs, baseStack, offset, size, hollow);
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
        return new Pair<>(
            at + 1,
            new Vec3Iota(offset.add(new Vec3d(
                at % size.getX(), (at / size.getX()) % size.getY(), (at / (size.getX() * size.getY()))
            )))
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
        if (at < size.getX()*size.getY()*size.getZ()) {
            var next = getNextAt();

            newCont = cont.pushFrame(new FrameExecuteOnCube(instrs, baseStack, offset, size, hollow, next.getFirst()))
                          .pushFrame(new FrameEvaluate(instrs, true));
            // evil?
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

    public static final Type<FrameExecuteOnCube> TYPE = new Type<>() {
        @SuppressWarnings("null")
        @Override
        public FrameExecuteOnCube deserializeFromNBT(NbtCompound nbt, ServerWorld world) {
            List<Iota> newStack = new ArrayList<>();
            HexIotaTypes.LIST.deserialize(
                nbt.getList("baseStack", NbtElement.COMPOUND_TYPE),
                world
            ).getList().forEach(newStack::add);

            return new FrameExecuteOnCube(
                HexIotaTypes.LIST.deserialize(
                    nbt.getList("instrs", NbtElement.COMPOUND_TYPE),
                    world
                ).getList(),
                newStack,
                deserializeVec3d(nbt.get("offset")),
                deserializeBlockPos(nbt.get("size")),
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
        nbt.put("size", serializeBlockPos(size));
        nbt.putBoolean("hollow", hollow);
        nbt.putInt("at", at);
        return nbt;
    }

    @Override
    public int size() { return instrs.size() + baseStack.size(); }
}
