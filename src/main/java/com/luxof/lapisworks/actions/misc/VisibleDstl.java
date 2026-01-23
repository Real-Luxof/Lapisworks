package com.luxof.lapisworks.actions.misc;

import at.petrak.hexcasting.api.casting.OperatorUtils;
import at.petrak.hexcasting.api.casting.castables.ConstMediaAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.OperationResult;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation;
import at.petrak.hexcasting.api.casting.iota.BooleanIota;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.mishaps.MishapBadLocation;
import at.petrak.hexcasting.api.misc.MediaConstants;

import com.luxof.lapisworks.MishapThrowerJava;

import java.util.List;

import net.minecraft.entity.Entity;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;

public class VisibleDstl implements ConstMediaAction {
    @Override
    public List<Iota> execute(List<? extends Iota> args, CastingEnvironment ctx) {
        Entity entity = OperatorUtils.getEntity(args, 0, getArgc());
        Vec3d start = entity.getEyePos();
        Vec3d end = OperatorUtils.getVec3(args, 1, getArgc());

        try {
            ctx.assertVecInRange(start);
            ctx.assertVecInRange(end);
        } catch (MishapBadLocation e) { MishapThrowerJava.throwMishap(e); }

        //If the entity isn't even facing the block
        if (entity.getRotationVector().dotProduct(end.subtract(start).normalize()) < 0.1)
            return List.of(new BooleanIota(false));

        BlockHitResult blockHitResult = ctx.getWorld().raycast(new RaycastContext(
            start,
            end,
            RaycastContext.ShapeType.COLLIDER,
            RaycastContext.FluidHandling.ANY,
            null //Some mods let players with certain ability not to collide with blocks (like phantom origin), if not null may cause unexpected behaviour.
        ));
        return List.of(new BooleanIota(
            blockHitResult.getBlockPos().equals(BlockPos.ofFloored(end)) //If the block hit is the block we wanted
            || blockHitResult.getType() ==  HitResult.Type.MISS //Or we went nowhere, which is still ok
        ));
    }

    @Override
    public CostMediaActionResult executeWithOpCount(List<? extends Iota> arg0, CastingEnvironment arg1) {
        return ConstMediaAction.DefaultImpls.executeWithOpCount(this, arg0, arg1);
    }

    @Override
    public int getArgc() {
        return 2;
    }

    @Override
    public long getMediaCost() {
        return (long)(MediaConstants.DUST_UNIT * 0.01);
    }

    @Override
    public OperationResult operate(CastingEnvironment arg0, CastingImage arg1, SpellContinuation arg2) {
        return ConstMediaAction.DefaultImpls.operate(this, arg0, arg1, arg2);
    }
}
