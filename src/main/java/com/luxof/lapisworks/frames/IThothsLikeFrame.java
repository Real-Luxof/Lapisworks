package com.luxof.lapisworks.frames;

import at.petrak.hexcasting.api.casting.SpellList;
import at.petrak.hexcasting.api.casting.eval.vm.ContinuationFrame;
import at.petrak.hexcasting.api.casting.iota.Iota;

import java.util.List;

/** implementing this interface allows your continuation frame to be manipulated by the
 * Thoth's-like Manipulation patterns. */
public interface IThothsLikeFrame {
    public List<Iota> getDatum();
    public ContinuationFrame withDatum(List<Iota> newDatum);
    public SpellList getHex();
    public ContinuationFrame withHex(SpellList hex);
}
