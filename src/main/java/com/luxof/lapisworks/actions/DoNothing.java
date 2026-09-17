package com.luxof.lapisworks.actions;

import at.petrak.hexcasting.api.casting.castables.SpellAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;

import java.util.List;

import com.luxof.lapisworks.nocarpaltunnel.HexIotaStack;
import com.luxof.lapisworks.nocarpaltunnel.SpellActionNCT;

public class DoNothing extends SpellActionNCT {
    public int argc = 0;

    @Override
    public boolean awardsCastingStat(CastingEnvironment arg0) { return false; }

    @Override
    public Result execute(HexIotaStack arg0, CastingEnvironment arg1) {
        return new SpellAction.Result(
            new DoNothingSpell(),
            0L,
            List.of(),
            0
        );
    }

    public static class DoNothingSpell implements RenderedSpellNCT {
        @Override public void cast(CastingEnvironment arg0) { return; }
    }
}
