package com.luxof.lapisworks.actions.interact;

import at.petrak.hexcasting.api.casting.ParticleSpray;
import at.petrak.hexcasting.api.casting.castables.SpellAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.env.PackagedItemCastEnv;
import at.petrak.hexcasting.api.casting.eval.env.PlayerBasedCastEnv;
import at.petrak.hexcasting.api.casting.mishaps.MishapNotEnoughMedia;
import at.petrak.hexcasting.api.misc.MediaConstants;
import at.petrak.hexcasting.xplat.IXplatAbstractions;

import com.luxof.lapisworks.blocks.stuff.IChalkBE;
import com.luxof.lapisworks.media.LinkableMediaBlock;
import com.luxof.lapisworks.media.MediaTransferInterface;
import com.luxof.lapisworks.mixin.PlayerBasedCastEnvAccessor;
import com.luxof.lapisworks.nocarpaltunnel.HexIotaStack;
import com.luxof.lapisworks.nocarpaltunnel.SpellActionNCT;

import static com.luxof.lapisworks.Lapisworks.ONEIRONAUT_INTEROP;
import static com.luxof.lapisworks.interop.oneironaut.FuckingInexhaustiblePhials.getBottomlessContrib;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.server.network.ServerPlayerEntity;

public class Deposit extends SpellActionNCT {
    public int argc = 2;

    private void assertNoFunnyMedia(long mediaCost) {
        long availableMedia = -ctx.extractMedia(-1, true);
        if (!(ctx instanceof PlayerBasedCastEnv pbcenv)) return;

        ServerPlayerEntity player = pbcenv.getCaster();
        if (player.isCreative()) return;

        availableMedia -= ((PlayerBasedCastEnvAccessor)pbcenv).lapisworks$invokeCanOvercast()
            && (
                !(ctx instanceof PackagedItemCastEnv pice) ||
                IXplatAbstractions.INSTANCE.findHexHolder(
                    player.getStackInHand(pice.getCastingHand())
                ).canDrawMediaFromInventory()
            )
            ? 20L*MediaConstants.DUST_UNIT
            : 0;
        availableMedia -= ONEIRONAUT_INTEROP ? getBottomlessContrib(pbcenv) : 0L;

        if (availableMedia < mediaCost)
            throw new MishapNotEnoughMedia(mediaCost);
    }

    @Override
    public Result execute(HexIotaStack stack, CastingEnvironment ctx) {
        MediaTransferInterface MTI = stack.getMediaTransferInterface(0);
        long amount = (long)(stack.getPositiveDouble(1) * MediaConstants.DUST_UNIT);

        List<ParticleSpray> particles = new ArrayList<>(List.of(
            ParticleSpray.cloud(ctx.mishapSprayPos(), 3, 20)
        ));

        long realAmount = Math.min(
            amount,
            MTI instanceof LinkableMediaBlock LMB
                ? LMB.getMaxMediaWithLinks() - LMB.getMediaHereWithLinks()
                : MTI.getMaxMedia() - MTI.getMediaHere()
        );

        long cost = (long)(1.1*realAmount);
        assertNoFunnyMedia(cost);

        if (MTI instanceof IChalkBE chalk)
            return new SpellAction.Result(
                new StartOneTimeRitualSpell(chalk, realAmount),
                cost,
                particles,
                1
            );

        return new SpellAction.Result(
            new MTISpell(MTI, realAmount),
            cost,
            particles,
            1
        );
    }

    public class MTISpell implements RenderedSpellNCT {
        public final MediaTransferInterface MTI;
        public final long amount;

        public MTISpell(MediaTransferInterface MTI, long amount) {
            this.MTI = MTI;
            this.amount = amount;
        }

        @Override
        public void cast(CastingEnvironment ctx) {
            if (MTI instanceof LinkableMediaBlock LMB)
                LMB.depositMediaWithLinks(amount, false);
            else
                MTI.depositMedia(amount, false);
        }
    }

    public class StartOneTimeRitualSpell implements RenderedSpellNCT {
        public final IChalkBE chalk;
        public final long amount;

        public StartOneTimeRitualSpell(IChalkBE chalk, long amount) {
            this.chalk = chalk;
            this.amount = amount;
        }

        @Override
        public void cast(CastingEnvironment ctx) {
            chalk.startCast(amount, (PlayerBasedCastEnv)ctx);
        }
    }
}
