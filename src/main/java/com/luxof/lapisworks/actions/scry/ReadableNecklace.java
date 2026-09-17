package com.luxof.lapisworks.actions.scry;

import at.petrak.hexcasting.api.addldata.ADIotaHolder;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.BooleanIota;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.xplat.IXplatAbstractions;

import static com.luxof.lapisworks.Lapisworks.getFirstTrinketIfEquipped;
import static com.luxof.lapisworks.init.ModItems.FOCUS_NECKLACE;

import dev.emi.trinkets.api.SlotReference;

import java.util.List;

import com.luxof.lapisworks.nocarpaltunnel.ConstMediaActionNCT;
import com.luxof.lapisworks.nocarpaltunnel.HexIotaStack;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Pair;

public class ReadableNecklace extends ConstMediaActionNCT {
    public int argc = 0;
    public long mediaCost = 0L;

    @Override
    public List<Iota> execute(HexIotaStack stack, CastingEnvironment ctx) {
        List<Iota> FALSE = List.of(new BooleanIota(false));
        List<Iota> TRUE = List.of(new BooleanIota(true));
        LivingEntity ent = ctx.getCastingEntity();
        if (ent == null) return FALSE;

        Pair<SlotReference, ItemStack> necklace = getFirstTrinketIfEquipped(ent, FOCUS_NECKLACE);

        if (necklace == null) return FALSE;

        ItemStack trinket = necklace.getRight();
        ADIotaHolder iotaHolder = IXplatAbstractions.INSTANCE.findDataHolder(trinket);
        if (iotaHolder == null ||
            (iotaHolder.readIota(ctx.getWorld()) == null &
             iotaHolder.emptyIota() == null)) {
            return FALSE;
        }
        return TRUE;
    }
}
