package com.luxof.lapisworks.actions.misc;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota;

import com.luxof.lapisworks.nocarpaltunnel.ConstMediaActionNCT;
import com.luxof.lapisworks.nocarpaltunnel.HexIotaStack;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.text.Text;

// was cool for other reasons before, but those reasons were invalid and this got repurposed
// it's now cool because it's named "Dealer's Purification" (cool name)
public class TheCoolerEntropy extends ConstMediaActionNCT {
    public int argc = 1;
    public long mediaCost = 0L;

    public List<Iota> execute(HexIotaStack stack, CastingEnvironment ctx) {
        ArrayList<Iota> list = stack.getJUSTAList(0);

        if (list.size() == 0)
            throw new MishapInvalidIota(
                stack.get(0),
                0,
                Text.translatable("mishaps.lapisworks.descs.list_with_one")
            );

        return List.of(
            list.get(world.getRandom().nextInt(list.size()))
        );
    }
}
