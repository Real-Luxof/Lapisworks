package com.luxof.lapisworks.actions.misc;

import at.petrak.hexcasting.api.casting.OperatorUtils;
import at.petrak.hexcasting.api.casting.SpellList;
import at.petrak.hexcasting.api.casting.castables.Action;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.OperationResult;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import at.petrak.hexcasting.api.casting.eval.vm.FrameFinishEval;
import at.petrak.hexcasting.api.casting.eval.vm.FrameForEach;
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.Vec3Iota;
import at.petrak.hexcasting.api.casting.mishaps.MishapNotEnoughArgs;
import at.petrak.hexcasting.common.lib.hex.HexEvalSounds;

import static com.luxof.lapisworks.Lapisworks.ceil;

import java.util.ArrayList;
import java.util.List;

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

        SpellList datum = OperatorUtils.getBool(stack, lastIdx, getArgc()) ?
            generatePointsInHollowCube(pointA, pointB) :
            generatePointsInFilledCube(pointA, pointB);
        FrameForEach frame = new FrameForEach(datum, instrs, null, new ArrayList<Iota>());

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
    
    public static SpellList generatePointsInFilledCube(Vec3d pointA, Vec3d pointB) {
        List<Iota> points = new ArrayList<>();
        boolean[] lessers = new boolean[] {
            pointA.x < pointB.x,
            pointA.y < pointB.y,
            pointA.z < pointB.z
        };
        Vec3d least = new Vec3d(
            lessers[0] ? pointA.x : pointB.x,
            lessers[1] ? pointA.y : pointB.y,
            lessers[2] ? pointA.z : pointB.z
        );
        Vec3d most = new Vec3d(
            lessers[0] ? pointB.x : pointA.x,
            lessers[1] ? pointB.y : pointA.y,
            lessers[2] ? pointB.z : pointA.z
        );
        Vec3d difference = new Vec3d(
            ceil(most.x - least.x),
            ceil(most.y - least.y),
            ceil(most.z - least.z)
        );

        for (double z = 0; z <= difference.z; z++) {
            for (double y = 0; y <= difference.y; y++) {
                for (double x = 0; x <= difference.x; x++) {
                    points.add(new Vec3Iota(new Vec3d(
                        x + least.x, y + least.y, z + least.z
                    )));
                }
            }
        }

        return new SpellList.LList(points);
    }

    public static SpellList generatePointsInHollowCube(Vec3d pointA, Vec3d pointB) {
        List<Iota> points = new ArrayList<>();

        // my brain is too small for the other approach (tried and skill issued)
        generatePointsInFilledCube(pointA, pointB).forEach((Iota anyIota) -> {
            Vec3d any = ((Vec3Iota)anyIota).getVec3();
            if (any.x == pointA.x || any.y == pointA.y || any.z == pointA.z ||
                any.x == pointB.x || any.y == pointB.y || any.z == pointB.z) {
                points.add(new Vec3Iota(any));
            }
        });

        return new SpellList.LList(points);
    }
}
