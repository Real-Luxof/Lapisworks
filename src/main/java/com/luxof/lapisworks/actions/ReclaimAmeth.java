package com.luxof.lapisworks.actions;

import at.petrak.hexcasting.api.casting.ParticleSpray;
import at.petrak.hexcasting.api.casting.castables.SpellAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment.HeldItemInfo;
import at.petrak.hexcasting.api.casting.mishaps.MishapBadOffhandItem;
import at.petrak.hexcasting.api.casting.mishaps.MishapDisallowedSpell;
import at.petrak.hexcasting.api.misc.MediaConstants;

import com.luxof.lapisworks.init.LapisConfig;
import com.luxof.lapisworks.init.Mutables.Mutables;
import com.luxof.lapisworks.nocarpaltunnel.HexIotaStack;
import com.luxof.lapisworks.nocarpaltunnel.SpellActionNCT;

import static com.luxof.lapisworks.Lapisworks.id;

import java.util.List;

import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;

public class ReclaimAmeth extends SpellActionNCT {
    public int argc = 0;

    @Override
    public SpellAction.Result execute(HexIotaStack args, CastingEnvironment ctx) {
        if (!LapisConfig.spells.allow_reclaim_amethyst_but_imbue_lapis_takes_items_instead_of_raw_media) {
            throw new MishapDisallowedSpell(
                "disallowed",
                id("reclaim_ameth")
            );
        }

        HeldItemInfo heldStackInfo = ctx.getHeldItemToOperateOn(Mutables::isAmel);
        if (heldStackInfo == null)
            throw MishapBadOffhandItem.of(ItemStack.EMPTY.copy(), "amel");

        int count = heldStackInfo.stack().getCount();

        return new SpellAction.Result(
            new Spell(count * 2, heldStackInfo.hand()),
            MediaConstants.SHARD_UNIT,
            List.of(ParticleSpray.burst(ctx.mishapSprayPos(), 1, 10 + count)),
            1
        );
    }

    public class Spell implements RenderedSpellNCT {
        public final int count;
        public final Hand hand;

        public Spell(int count, Hand hand) { this.count = count; this.hand = hand; }

		@Override
		public void cast(CastingEnvironment ctx) {
            ctx.replaceItem(Mutables::isAmel, new ItemStack(Items.AMETHYST_SHARD, Math.min(64, this.count)), hand);
            if (this.count > 64) {
                Vec3d spawn = ctx.mishapSprayPos();
                ItemEntity ent = new ItemEntity(
                    ctx.getWorld(),
                    spawn.x,
                    spawn.y,
                    spawn.z,
                    new ItemStack(Items.AMETHYST_SHARD, this.count - 64)
                );
                ctx.getWorld().spawnEntity(ent);
            }
		}
    }
}
