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

import com.luxof.lapisworks.frames.FrameExecuteOnSphere;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.math.Vec3d;

/** the vultures pick another piece off of HexKinetics */
public class SphereDst implements Action {
    private final boolean filledSphere;
    public SphereDst(boolean filledSphere) {
        this.filledSphere = filledSphere;
    }

    public int getArgc() {
        return 3;
    }

    @Override
    public OperationResult operate(CastingEnvironment ctx, CastingImage img, SpellContinuation cont) {
        List<Iota> stack = new ArrayList<>(img.getStack());
        if (stack.size() < getArgc())
            throw new MishapNotEnoughArgs(getArgc(), stack.size());

        int lastIdx = stack.size() - 1;

        SpellList instrs = OperatorUtils.evaluatable(
            stack.get(lastIdx - 2),
            lastIdx
        ).map(iota -> new SpellList.LList(List.of(iota)), list -> list);

        Vec3d pos = OperatorUtils.getVec3(stack, lastIdx - 1, getArgc());
        int radius = OperatorUtils.getIntBetween(stack, lastIdx, 1, 64, getArgc());
        stack.remove(lastIdx);
        stack.remove(lastIdx - 1);
        stack.remove(lastIdx - 2);

        CastingImage img2 = img.withUsedOp().copy(
            stack,
            img.getParenCount(),
            img.getParenthesized(),
            img.getEscapeNext(),
            img.getOpsConsumed(),
            img.getUserData()
        );
        FrameExecuteOnSphere frame = new FrameExecuteOnSphere(
            instrs,
            stack,
            pos,
            radius,
            filledSphere
        );

        SpellContinuation newCont = cont instanceof SpellContinuation.NotDone notDone &&
            notDone.getFrame() instanceof FrameFinishEval
            ? cont
            : cont.pushFrame(FrameFinishEval.INSTANCE);

        return new OperationResult(img2, List.of(), newCont.pushFrame(frame), HexEvalSounds.THOTH);
    }
}
