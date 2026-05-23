package com.luxof.lapisworks.actions.interact;

import at.petrak.hexcasting.api.casting.ParticleSpray;
import at.petrak.hexcasting.api.casting.castables.SpellAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment.HeldItemInfo;
import at.petrak.hexcasting.api.casting.mishaps.MishapBadOffhandItem;
import at.petrak.hexcasting.api.item.MediaHolderItem;
import at.petrak.hexcasting.api.misc.MediaConstants;

import com.luxof.lapisworks.interop.hexal.actions.WithdrawIntoWisp;
import com.luxof.lapisworks.media.LinkableMediaBlock;
import com.luxof.lapisworks.media.MediaTransferInterface;
import com.luxof.lapisworks.media.MTIMediaHolder;
import com.luxof.lapisworks.nocarpaltunnel.HexIotaStack;
import com.luxof.lapisworks.nocarpaltunnel.SpellActionNCT;

import static com.luxof.lapisworks.Lapisworks.HEXAL_INTEROP;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.item.ItemStack;

public class Withdraw extends SpellActionNCT {
    public int getArgc() {
        return 2;
    }

    @Override
    public Result execute(HexIotaStack stack, CastingEnvironment ctx) {
        MediaTransferInterface from = stack.getMediaTransferInterface(0);
        long amount = (long)(stack.getPositiveDouble(1) * MediaConstants.DUST_UNIT);

        MediaTransferInterface into;
        if (HEXAL_INTEROP && WithdrawIntoWisp.isWisp(ctx)) {
            into = WithdrawIntoWisp.getCastingWispAsMTI(ctx);

        } else {
            // how do i make it not warn...
            HeldItemInfo heldInfo = ctx.getHeldItemToOperateOn(
                itemStack -> itemStack.getItem() instanceof MediaHolderItem mhi
                    && mhi.canRecharge(itemStack)
            );
            if (heldInfo == null)
                throw MishapBadOffhandItem.of(ItemStack.EMPTY.copy(), "rechargeable");

            ItemStack intoStack = heldInfo.component1();
            into = new MTIMediaHolder(intoStack);
        }

        List<ParticleSpray> particles = new ArrayList<>(List.of(
            ParticleSpray.cloud(ctx.mishapSprayPos(), 3, 20)
        ));

        long realAmount = Math.min(
            Math.min(
                amount,
                into.getMaxMedia() - into.getMediaHere()
            ),
            from instanceof LinkableMediaBlock lmb
                ? lmb.getMediaHereWithLinks()
                : from.getMediaHere()
        );


        return new SpellAction.Result(
            new Spell(from, realAmount, into),
            (long)(realAmount * 0.1),
            particles,
            1
        );
    }

    public class Spell implements RenderedSpellNCT {
        public final MediaTransferInterface from;
        public final long amount;
        public final MediaTransferInterface into;

        public Spell(MediaTransferInterface from, long amount, MediaTransferInterface into) {
            this.from = from;
            this.amount = amount;
            this.into = into;
        }

        @Override
        public void cast(CastingEnvironment ctx) {
            into.depositMedia(
                from instanceof LinkableMediaBlock lmb
                    ? lmb.withdrawMediaWithLinks(amount, false)
                    : from.withdrawMedia(amount, false),
                false
            );
        }
    }
}
