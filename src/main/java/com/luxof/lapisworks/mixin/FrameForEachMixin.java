package com.luxof.lapisworks.mixin;

import at.petrak.hexcasting.api.casting.SpellList;
import at.petrak.hexcasting.api.casting.eval.vm.ContinuationFrame;
import at.petrak.hexcasting.api.casting.eval.vm.FrameForEach;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.utils.TreeList;

import com.luxof.lapisworks.frames.IThothsLikeFrame;

import java.util.ArrayList;
import java.util.List;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(value = FrameForEach.class, remap = false)
public abstract class FrameForEachMixin implements IThothsLikeFrame {
    @Shadow public abstract SpellList getData();
    @Shadow public abstract SpellList getCode();
    @Shadow public abstract List<Iota> getBaseStack();
    @Shadow public abstract TreeList<Iota> getImmutableAcc();

    @Override
    public List<Iota> getDatum() {
        List<Iota> datum = new ArrayList<>();
        getData().forEach(datum::add);
        return datum;
    }

    @Override
    public ContinuationFrame withDatum(List<Iota> newDatum) {
        return new FrameForEach(
            new SpellList.LList(newDatum),
            getCode(),
            getBaseStack(),
            getImmutableAcc()
        );
    }

    @Override
    public SpellList getHex() {
        return getCode();
    }

    @Override
    public ContinuationFrame withHex(SpellList hex) {
        return new FrameForEach(
            getData(),
            hex,
            getBaseStack(),
            getImmutableAcc()
        );
    }
    }
