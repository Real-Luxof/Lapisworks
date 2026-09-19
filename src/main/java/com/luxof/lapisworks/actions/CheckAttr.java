package com.luxof.lapisworks.actions;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.DoubleIota;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.misc.MediaConstants;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributes;

import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;

import com.luxof.lapisworks.mixinsupport.LapisworksInterface;
import com.luxof.lapisworks.nocarpaltunnel.ConstMediaActionNCT;
import com.luxof.lapisworks.nocarpaltunnel.HexIotaStack;

import java.util.List;

public class CheckAttr extends ConstMediaActionNCT {
    public List<EntityAttribute> attributes = List.of(
        EntityAttributes.GENERIC_MAX_HEALTH,
        EntityAttributes.GENERIC_ATTACK_DAMAGE,
        EntityAttributes.GENERIC_MOVEMENT_SPEED,
        ReachEntityAttributes.REACH,
        ReachEntityAttributes.ATTACK_RANGE
    );

    public int argc = 2;
    public long mediaCost = (long)(MediaConstants.DUST_UNIT * 0.01);

    @Override
    public List<Iota> execute(HexIotaStack args, CastingEnvironment ctx) {
        int chosen = args.getIntBetween(1, 0, 4);
        return List.of(
            new DoubleIota(
                ((LapisworksInterface)(
                    chosen == 2 || chosen == 4
                        ? args.getPlayer(0)
                        : args.getLivingEntityButNotArmorStand(0)
                    )
                ).getAmountOfAttrJuicedUpByAmel(
                    this.attributes.get(
                        chosen
                    )
                ) * (chosen == 2 ? 10 : 1)
            )
        );
    }
}
