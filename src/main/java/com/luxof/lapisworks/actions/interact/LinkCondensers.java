package com.luxof.lapisworks.actions.interact;

import at.petrak.hexcasting.api.casting.ParticleSpray;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.misc.MediaConstants;

import com.luxof.lapisworks.VAULT.Flags;
import com.luxof.lapisworks.blocks.stuff.LinkableMediaBlock;
import com.luxof.lapisworks.init.Mutables.Mutables;
import com.luxof.lapisworks.mixinsupport.GetVAULT;
import com.luxof.lapisworks.nocarpaltunnel.HexIotaStack;
import com.luxof.lapisworks.nocarpaltunnel.SpellActionNCT;

import static com.luxof.lapisworks.Lapisworks.getDistance;
import static com.luxof.lapisworks.LapisworksIDs.AMEL;
import static com.luxof.lapisworks.MishapThrowerJava.assertIsntLinked;
import static com.luxof.lapisworks.MishapThrowerJava.assertItemAmount;
import static com.luxof.lapisworks.MishapThrowerJava.assertLinkableThere;
import static com.luxof.lapisworks.MishapThrowerJava.assertNotTooManyLinks;

import java.util.List;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;

// :face_holding_back_tears:
public class LinkCondensers extends SpellActionNCT {
    public int argc = 2;

    @Override
    public Result execute(HexIotaStack stack, CastingEnvironment ctx) {
        BlockPos pos1 = stack.getBlockPosInRange(0);
        BlockPos pos2 = stack.getBlockPosInRange(1);

        LinkableMediaBlock linkable1 = assertLinkableThere(pos1, ctx);
        LinkableMediaBlock linkable2 = assertLinkableThere(pos2, ctx);

        assertIsntLinked(linkable1, pos2);
        assertNotTooManyLinks(linkable1, linkable2, pos1, pos2);

        // "costs 1 amel per 32 blocks of distance, with a minimum of 1."
        int amelCost = (int)Math.max(1, Math.floor(getDistance(pos1, pos2) / 32.0));
        assertItemAmount(ctx, Mutables::isAmel, AMEL, amelCost);

        return new Result(
            new Spell(pos1, pos2, amelCost),
            MediaConstants.CRYSTAL_UNIT * 3,
            List.of(
                ParticleSpray.burst(pos1.toCenterPos(), 5, 30),
                ParticleSpray.burst(pos2.toCenterPos(), 5, 30)
            ),
            1
        );
    }
    
    public class Spell implements RenderedSpellNCT {
        public final BlockPos pos1;
        public final BlockPos pos2;
        public final int amel;
        public Spell(BlockPos pos1, BlockPos pos2, int amel) {
            this.pos1 = pos1;
            this.pos2 = pos2;
            this.amel = amel;
        }

        @Override
        public void cast(CastingEnvironment ctx) {
            ((GetVAULT)ctx).grabVAULT().drain(
                Mutables::isAmel,
                amel,
                false,
                Flags.PRESET_UpToHotbar
            );
            ServerWorld world = ctx.getWorld();
            ((LinkableMediaBlock)world.getBlockEntity(pos1)).addLink(pos2);
            ((LinkableMediaBlock)world.getBlockEntity(pos2)).addLink(pos1);
        }
    }
}
