package com.luxof.lapisworks.nocarpaltunnel;

import at.petrak.hexcasting.api.casting.RenderedSpell;
import at.petrak.hexcasting.api.casting.castables.SpellAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.Iota;

import com.luxof.lapisworks.mixinsupport.GetVAULT;

import static com.luxof.lapisworks.Lapisworks.err;

import java.util.List;

import net.minecraft.nbt.NbtCompound;

public class SpellActionNCT extends PatternNCTBase implements SpellAction {
    public boolean requiresEnlightenment = false;

    public SpellAction.Result execute(HexIotaStack stack, CastingEnvironment ctx) {
        throw new IllegalStateException("call executeWithUserdata instead.");
    }

    public SpellAction.Result executeWithUserdata(HexIotaStack stack, CastingEnvironment ctx, NbtCompound userData) {
        return SpellAction.super.executeWithUserdata(stack.stack, ctx, userData);
    }

    public interface RenderedSpellNCT extends RenderedSpell {

        default void cast(CastingEnvironment ctx) {
            throw new IllegalStateException("call cast(env, image) instead.");
        }

    }


    @Override
    public SpellAction.Result execute(List<? extends Iota> stack, CastingEnvironment ctx) {
        this.ctx = ctx;
        this.world = ctx.getWorld();
        this.vault = ((GetVAULT)ctx).grabVAULT();
        _assertIsEnlightenedIfRequiresEnlightenment();
        return execute(new HexIotaStack(stack, getArgc(), ctx), ctx);
    }

    @Override
    public SpellAction.Result executeWithUserdata(List<? extends Iota> arg0, CastingEnvironment arg1, NbtCompound arg2) {
        this.ctx = arg1;
        this.world = arg1.getWorld();
        this.vault = ((GetVAULT)arg1).grabVAULT();
        _assertIsEnlightenedIfRequiresEnlightenment();
        return executeWithUserdata(new HexIotaStack(arg0, getArgc(), arg1), arg1, arg2);
    }



    // reflection jumpscare
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
