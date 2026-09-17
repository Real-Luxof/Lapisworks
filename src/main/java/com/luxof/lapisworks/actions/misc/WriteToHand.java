package com.luxof.lapisworks.actions.misc;

import at.petrak.hexcasting.api.addldata.ADIotaHolder;
import at.petrak.hexcasting.api.casting.castables.SpellAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.mishaps.MishapOthersName;
import at.petrak.hexcasting.xplat.IXplatAbstractions;

import com.luxof.lapisworks.mishaps.MishapBadHandItem;
import com.luxof.lapisworks.nocarpaltunnel.HexIotaStack;
import com.luxof.lapisworks.nocarpaltunnel.SpellActionNCT;

import static com.luxof.lapisworks.Lapisworks.getStackFromHand;
import static com.luxof.lapisworks.Lapisworks.intToHand;
import static com.luxof.lapisworks.LapisworksIDs.NON_IOTAHOLDER;
import static com.luxof.lapisworks.LapisworksIDs.READONLY_HOLDER;
import static com.luxof.lapisworks.LapisworksIDs.WRITEABLE;
import static com.luxof.lapisworks.init.Mutables.Mutables.maxHands;

import java.util.List;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;

public class WriteToHand extends SpellActionNCT {
    public int argc = 2;

    @Override
    public Result execute(HexIotaStack args, CastingEnvironment ctx) {
        Iota iota = args.get(0);
        int hand = args.getIntBetween(1, 0, maxHands - 1);
        final Hand HAND = intToHand(hand);
        ItemStack stack = getStackFromHand(ctx, hand);
        if (stack == null) {
            throw new MishapBadHandItem(
                stack,
                WRITEABLE,
                HAND
            );
        }
        ADIotaHolder iotaHolder = IXplatAbstractions.INSTANCE.findDataHolder(stack);
        // "let's make the error message more helpful!"
        // :thumbsup:
        if (iotaHolder == null) {
            throw new MishapBadHandItem(
                stack,
                WRITEABLE,
                NON_IOTAHOLDER,
                HAND
            );
        } else if (!iotaHolder.writeIota(iota, true)) {
            throw new MishapBadHandItem(
                stack,
                WRITEABLE,
                READONLY_HOLDER,
                HAND
            );
        }
        PlayerEntity truename = MishapOthersName
            .getTrueNameFromDatum(iota, (PlayerEntity)ctx.getCastingEntity());
        if (truename != null) { throw new MishapOthersName(truename); }

        return new SpellAction.Result(
            new Spell(iota, iotaHolder),
            0,
            List.of(),
            1
        );
    }

    public class Spell implements RenderedSpellNCT {
        public final Iota iota;
        public final ADIotaHolder iotaHolder;
        public Spell(Iota iota, ADIotaHolder iotaHolder) {
            this.iota = iota;
            this.iotaHolder = iotaHolder;
        }

        @Override
        public void cast(CastingEnvironment ctx) {
            iotaHolder.writeIota(iota, false);
        }
    }

    @Override
    public boolean hasCastingSound(CastingEnvironment env) { return true; }
}
