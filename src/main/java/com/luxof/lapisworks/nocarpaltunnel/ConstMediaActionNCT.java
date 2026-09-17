package com.luxof.lapisworks.nocarpaltunnel;

import at.petrak.hexcasting.api.casting.castables.ConstMediaAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.Iota;

import com.luxof.lapisworks.mixinsupport.GetVAULT;

import static com.luxof.lapisworks.Lapisworks.err;

import java.util.List;

public abstract class ConstMediaActionNCT extends PatternNCTBase implements ConstMediaAction {
    public boolean requiresEnlightenment = false;

    public List<? extends Iota> execute(HexIotaStack stack, CastingEnvironment ctx) {
        throw new IllegalStateException("call executeWithOpCount instead.");
    }

    public CostMediaActionResult executeWithOpCount(HexIotaStack stack, CastingEnvironment ctx) {
        return ConstMediaAction.super.executeWithOpCount(stack.stack, ctx);
    }

    protected <AnyIota extends Iota> List<AnyIota> asActionResult(AnyIota iota) {
        return List.of(iota);
    }


    @Override
    public List<Iota> execute(List<? extends Iota> arg0, CastingEnvironment arg1) {
        this.ctx = arg1;
        this.world = arg1.getWorld();
        this.vault = ((GetVAULT)arg1).grabVAULT();
        _assertIsEnlightenedIfRequiresEnlightenment();
        return execute(new HexIotaStack(arg0, getArgc(), arg1), arg1).stream().map(it -> (Iota)it).toList();
    }

    @Override
    public CostMediaActionResult executeWithOpCount(List<? extends Iota> arg0, CastingEnvironment arg1) {
        this.ctx = arg1;
        this.world = arg1.getWorld();
        this.vault = ((GetVAULT)arg1).grabVAULT();
        _assertIsEnlightenedIfRequiresEnlightenment();
        return executeWithOpCount(new HexIotaStack(arg0, getArgc(), arg1), arg1);
    }



    // boo!
    @Override
    public int getArgc() {
        try {
            return this.getClass().getField("argc").getInt(this);
        } catch (NoSuchFieldException e) {
            err("you must have an argc field in the first place.", e);
        } catch (IllegalAccessException e) {
            err("your argc field must be accessible.", e);
        } catch (IllegalArgumentException e) {
            err("your argc field must be an int.", e);
        }
        return 0;
    }

    // boo! (part 2: electric boogaloo)
    @Override
    public long getMediaCost() {
        try {
            return this.getClass().getField("mediaCost").getLong(this);
        } catch (NoSuchFieldException e) {
            err("you must have a mediaCost field in the first place.", e);
        } catch (IllegalAccessException e) {
            err("your mediaCost field must be accessible.", e);
        } catch (IllegalArgumentException e) {
            err("your mediaCost field must be a long.", e);
        }
        return 0;
    }

    @Override
    public boolean getRequiresEnlightenment() {
        try {
            return this.getClass().getField("requiresEnlightenment").getBoolean(this);
        } catch (IllegalArgumentException e) {
            err("your requiresEnlightenment field must be a boolean.", e);
        } catch (IllegalAccessException e) {
            e.printStackTrace(); // Never happens
        } catch (NoSuchFieldException e) {
            e.printStackTrace(); // Never happens
        }
        return false;
    }
}
