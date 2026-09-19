package com.luxof.lapisworks.actions.interact;

import at.petrak.hexcasting.api.casting.ParticleSpray;
import at.petrak.hexcasting.api.casting.SpellList;
import at.petrak.hexcasting.api.casting.castables.SpellAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.DoubleIota;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.mishaps.MishapBadBlock;
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota;
import at.petrak.hexcasting.api.misc.MediaConstants;

import com.luxof.lapisworks.blocks.entities.LiveJukeboxEntity;
import com.luxof.lapisworks.init.ModBlocks;
import com.luxof.lapisworks.nocarpaltunnel.HexIotaStack;
import com.luxof.lapisworks.nocarpaltunnel.SpellActionNCT;

import static com.luxof.lapisworks.LapisworksIDs.LIVE_JUKEBOX_BLOCK;
import static com.luxof.lapisworks.LapisworksIDs.NOTELIST;
import static com.luxof.lapisworks.LapisworksIDs.NOTELIST_MOFO;
import static com.luxof.lapisworks.LapisworksIDs.NOTELIST_OUTOFRANGE;
import static com.luxof.lapisworks.MishapThrowerJava.throwIfEmpty;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.util.math.BlockPos;

public class TeachSong extends SpellActionNCT {
    public int argc = 3;

    @Override
    public SpellAction.Result execute(HexIotaStack stack, CastingEnvironment ctx) {
        BlockPos liveJukeboxPos = stack.getBlockPos(0);
        ctx.assertPosInRange(liveJukeboxPos);

        LiveJukeboxEntity blockEntity = throwIfEmpty(
            ctx.getWorld().getBlockEntity(liveJukeboxPos, ModBlocks.LIVE_JUKEBOX_ENTITY_TYPE),
            new MishapBadBlock(liveJukeboxPos, LIVE_JUKEBOX_BLOCK)
        );

        SpellList iotaList = stack.getList(1);
        List<Integer> notes = new ArrayList<>();
        int mishapOnIndex = 1;
        Iota mishapOnIota = stack.get(mishapOnIndex);
        iotaList.forEach(iota -> {
            if (iota instanceof DoubleIota) {
                double doubleNote = ((DoubleIota)iota).getDouble();
                double roundedNote = (int)Math.round(doubleNote);
                if (Math.abs(doubleNote - roundedNote) > DoubleIota.TOLERANCE) {
                    throw new MishapInvalidIota(
                        mishapOnIota,
                        mishapOnIndex,
                        NOTELIST_MOFO
                    );
                } else if (roundedNote < 0.0 || roundedNote > 24.0) {
                    throw new MishapInvalidIota(
                        mishapOnIota,
                        mishapOnIndex,
                        NOTELIST_OUTOFRANGE
                    );
                }
                notes.add((int)roundedNote);
            } else {
                throw new MishapInvalidIota(
                    mishapOnIota,
                    mishapOnIndex,
                    NOTELIST
                );
            }
        });

        int frequency = stack.getIntBetween(2, 0, 20);

        return new SpellAction.Result(
            new Spell(blockEntity, notes, frequency),
            MediaConstants.SHARD_UNIT,
            List.of(ParticleSpray.burst(ctx.mishapSprayPos(), 2, 15)),
            1
        );
    }

    public class Spell implements RenderedSpellNCT {
        public final LiveJukeboxEntity blockEntity;
        public final List<Integer> notes;
        public final int frequency;

        public Spell(LiveJukeboxEntity blockEntity, List<Integer> notes, int frequency) {
            this.blockEntity = blockEntity;
            this.notes = notes;
            this.frequency = frequency;
        }

		@Override
		public void cast(CastingEnvironment ctx) {
            this.blockEntity.notes = List.copyOf(this.notes);
            this.blockEntity.frequency = this.frequency;
            this.blockEntity.playingNotes = List.of();
            this.blockEntity.hasBeenTimeBetweenNotes = 0;
            this.blockEntity.markDirty();
		}
    }
}
