package com.luxof.lapisworks.actions.misc.thoth;

import at.petrak.hexcasting.api.casting.castables.Action;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.OperationResult;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.ListIota;
import at.petrak.hexcasting.common.lib.hex.HexEvalSounds;

import com.luxof.lapisworks.frames.IThothsLikeFrame;
import com.luxof.lapisworks.mishaps.MishapInvalidContinuation;

import static com.luxof.lapisworks.Lapisworks.CastingImgWithStack;
import static com.luxof.lapisworks.Lapisworks.pullThothsLikeFrame;

import java.util.ArrayList;
import java.util.List;

public class GetDatum implements Action {

    @Override
    public OperationResult operate(CastingEnvironment ctx, CastingImage img, SpellContinuation cont) {
        IThothsLikeFrame thothFrame = pullThothsLikeFrame(cont);
        if (thothFrame == null)
            throw new MishapInvalidContinuation("mishaps.lapisworks.descs.thothframe");

        List<Iota> stack = new ArrayList<>(img.getStack());
        stack.add(new ListIota(thothFrame.getDatum()));

        return new OperationResult(
            CastingImgWithStack(img.withUsedOp(), stack),
            List.of(),
            cont,
            HexEvalSounds.NORMAL_EXECUTE
        );
    }
}
