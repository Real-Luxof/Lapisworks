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
import at.petrak.hexcasting.api.utils.HexUtils;
import at.petrak.hexcasting.api.utils.TreeList;
import at.petrak.hexcasting.common.lib.hex.HexEvalSounds;
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes;

import java.util.ArrayList;
import java.util.List;

import kotlin.Pair;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.server.world.ServerWorld;

public class FrameExecuteManyTimes implements ContinuationFrame, IThothsLikeFrame {
    public SpellList instrs;
    public List<? extends Iota> baseStack;
    public int howManyTimes;
    public FrameExecuteManyTimes(
        SpellList instrs,
        List<? extends Iota> baseStack,
        int howManyTimes
    ) {
        this.instrs = instrs;
        this.baseStack = baseStack;
        this.howManyTimes = howManyTimes;
    }

    @Override
    public Pair<Boolean, List<Iota>> breakDownwards(List<? extends Iota> stack) {
        return new Pair<Boolean, List<Iota>>(
            true,
            List.copyOf(stack)
        );
    }

    @Override
    public CastResult evaluate(SpellContinuation cont, ServerWorld world, CastingVM vm) {
        // i've produced at least 1.2 amethyst dust figuring this out
        // anyway, the "why" is fairly obvious if you consider what OpForNInRange does.
        CastingImage oldImg = vm.getImage();
        CastingImage newImg = oldImg.withResetEscape().copy(
            baseStack,
            oldImg.getParenCount(),
            oldImg.getParenthesized(),
            oldImg.getEscapeNext(),
            oldImg.getOpsConsumed(),
            oldImg.getUserData()
        );
        SpellContinuation newCont = cont;
        if (howManyTimes > 0) {
            newCont = cont.pushFrame(new FrameExecuteManyTimes(instrs, baseStack, howManyTimes - 1))
                          .pushFrame(new FrameEvaluate(instrs, true));
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

    public static final Type<FrameExecuteManyTimes> TYPE = new Type<>() {
        @SuppressWarnings("null")
        @Override
        public FrameExecuteManyTimes deserializeFromNBT(NbtCompound nbt, ServerWorld world) {
            List<Iota> newStack = new ArrayList<>();
            HexIotaTypes.LIST.deserialize(
                nbt.getList("baseStack", NbtElement.COMPOUND_TYPE),
                world
            ).getList().forEach(newStack::add);

            return new FrameExecuteManyTimes(
                HexIotaTypes.LIST.deserialize(
                    nbt.getList("instrs", NbtElement.COMPOUND_TYPE),
                    world
                ).getList(),
                newStack,
                nbt.getInt("howManyTimes")
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
        nbt.putInt("howManyTimes", howManyTimes);
        return nbt;
    }

    @Override
    public int size() { return instrs.size() + baseStack.size(); }


    @Override
    public List<Iota> getDatum() {
        return List.of();
    }

    @Override
    public ContinuationFrame withDatum(List<Iota> newDatum) {
        return new FrameForEach(new SpellList.LList(newDatum), instrs, baseStack, TreeList.empty());
    }

    @Override
    public SpellList getHex() {
        return instrs;
    }

    @Override
    public ContinuationFrame withHex(SpellList hex) {
        return new FrameExecuteManyTimes(hex, baseStack, howManyTimes);
    }
}
