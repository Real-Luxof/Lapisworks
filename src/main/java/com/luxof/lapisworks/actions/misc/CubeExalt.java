package com.luxof.lapisworks.actions.misc;

import at.petrak.hexcasting.api.casting.OperatorUtils;
import at.petrak.hexcasting.api.casting.SpellList;
import at.petrak.hexcasting.api.casting.castables.Action;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.OperationResult;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import at.petrak.hexcasting.api.casting.eval.vm.FrameFinishEval;
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.mishaps.MishapNotEnoughArgs;
import at.petrak.hexcasting.common.lib.hex.HexEvalSounds;

import java.util.ArrayList;
import java.util.List;

import com.luxof.lapisworks.frames.FrameExecuteOnCube;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class CubeExalt implements Action {
    public int getArgc() {
        return 4;
    }

    @Override
    public OperationResult operate(CastingEnvironment ctx, CastingImage img, SpellContinuation cont) {
        List<Iota> stack = new ArrayList<Iota>(img.getStack());
        if (stack.size() < getArgc()) throw new MishapNotEnoughArgs(4, stack.size());

        int lastIdx = stack.size() - 1;

        SpellList instrs = OperatorUtils.evaluatable(
            stack.get(lastIdx - 3),
            lastIdx - 3
        ).map(iota -> new SpellList.LList(List.of(iota)), list -> list);

        Vec3d pointA = OperatorUtils.getVec3(stack, lastIdx - 2, getArgc());
        Vec3d pointB = OperatorUtils.getVec3(stack, lastIdx - 1, getArgc());

        Vec3d perfectPointA = new Vec3d(
            pointA.x < pointB.x ? pointA.x : pointB.x,
            pointA.y < pointB.y ? pointA.y : pointB.y,
            pointA.z < pointB.z ? pointA.z : pointB.z
        );
        Vec3d perfectPointB = new Vec3d(
            pointA.x > pointB.x ? pointA.x : pointB.x,
            pointA.y > pointB.y ? pointA.y : pointB.y,
            pointA.z > pointB.z ? pointA.z : pointB.z
        );

        FrameExecuteOnCube frame = new FrameExecuteOnCube(
            instrs,
            stack,
            pointA,
            BlockPos.ofFloored(perfectPointB.subtract(perfectPointA)),
            OperatorUtils.getBool(stack, lastIdx, getArgc())
        );

        stack.remove(lastIdx);
        stack.remove(lastIdx - 1);
        stack.remove(lastIdx - 2);
        stack.remove(lastIdx - 3);

        CastingImage img2 = img.withUsedOp().copy(
            stack,
            img.getParenCount(),
            img.getParenthesized(),
            img.getEscapeNext(),
            img.getOpsConsumed(),
            img.getUserData()
        );

        SpellContinuation newCont = cont instanceof SpellContinuation.NotDone notDone &&
            notDone.getFrame() instanceof FrameFinishEval
            ? cont
            : cont.pushFrame(FrameFinishEval.INSTANCE);

        return new OperationResult(img2, List.of(), newCont.pushFrame(frame), HexEvalSounds.THOTH);
    }
}
