package com.luxof.lapisworks.actions.misc;

import at.petrak.hexcasting.api.addldata.ADIotaHolder;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.BooleanIota;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.xplat.IXplatAbstractions;

import com.luxof.lapisworks.nocarpaltunnel.ConstMediaActionNCT;
import com.luxof.lapisworks.nocarpaltunnel.HexIotaStack;

import static com.luxof.lapisworks.Lapisworks.getStackFromHand;
import static com.luxof.lapisworks.init.Mutables.Mutables.maxHands;

import java.util.List;

public class WritableInHand extends ConstMediaActionNCT {
    public int argc = 1;
    public long mediaCost = 0L;

    @Override
    public List<Iota> execute(HexIotaStack stack, CastingEnvironment ctx) {
        ADIotaHolder iotaHolder = IXplatAbstractions.INSTANCE.findDataHolder(
            getStackFromHand(ctx, stack.getIntBetween(0, 0, maxHands - 1))
        );
        // apparently no, OperatorUtils.asActionResult can't be used here because it returns
        // a List<BooleanIota> instead of a List<Iota> (I'm doing the same thing??)
        return List.of(new BooleanIota(
            iotaHolder != null && (
                iotaHolder.writeable()
            )
        ));
    }
}
