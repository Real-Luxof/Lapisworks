package com.luxof.lapisworks.actions;

import at.petrak.hexcasting.api.casting.ParticleSpray;
import at.petrak.hexcasting.api.casting.RenderedSpell;
import at.petrak.hexcasting.api.casting.castables.SpellAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.OperationResult;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment.HeldItemInfo;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.mishaps.MishapBadOffhandItem;
import at.petrak.hexcasting.api.casting.mishaps.MishapDisallowedSpell;
import at.petrak.hexcasting.api.misc.MediaConstants;

import com.luxof.lapisworks.init.LapisConfig;
import com.luxof.lapisworks.init.Mutables.Mutables;

import static com.luxof.lapisworks.Lapisworks.id;

import java.util.List;

import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Hand;
import net.minecraft.util.math.Vec3d;

public class ReclaimAmeth implements SpellAction {
    public int getArgc() {
        return 0;
    }

    @Override
    public SpellAction.Result execute(List<? extends Iota> args, CastingEnvironment ctx) {
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

    public class Spell implements RenderedSpell {
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

        @Override
        public CastingImage cast(CastingEnvironment arg0, CastingImage arg1) {
            return RenderedSpell.DefaultImpls.cast(this, arg0, arg1);
        }
    }

    @Override
    public boolean awardsCastingStat(CastingEnvironment ctx) {
        return SpellAction.DefaultImpls.awardsCastingStat(this, ctx);
    }

    @Override
    public Result executeWithUserdata(List<? extends Iota> args, CastingEnvironment env, NbtCompound userData) {
        return SpellAction.DefaultImpls.executeWithUserdata(this, args, env, userData);
    }

    @Override
    public boolean hasCastingSound(CastingEnvironment ctx) {
        return SpellAction.DefaultImpls.hasCastingSound(this, ctx);
    }

    @Override
    public OperationResult operate(CastingEnvironment arg0, CastingImage arg1, SpellContinuation arg2) {
        return SpellAction.DefaultImpls.operate(this, arg0, arg1, arg2);
    }
}
