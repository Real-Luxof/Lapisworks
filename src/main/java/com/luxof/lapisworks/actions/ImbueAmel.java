package com.luxof.lapisworks.actions;

import at.petrak.hexcasting.api.casting.ParticleSpray;
import at.petrak.hexcasting.api.casting.castables.SpellAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment.HeldItemInfo;
import at.petrak.hexcasting.api.casting.mishaps.MishapBadOffhandItem;
import at.petrak.hexcasting.api.misc.MediaConstants;

import com.luxof.lapisworks.VAULT.Flags;
import com.luxof.lapisworks.VAULT.VAULT;
import com.luxof.lapisworks.init.Mutables.BeegInfusion;
import com.luxof.lapisworks.init.Mutables.Mutables;
import com.luxof.lapisworks.init.Mutables.Mutables.BeegInfusions;
import com.luxof.lapisworks.inv.HandsInv;
import com.luxof.lapisworks.mishaps.MishapNotEnoughItems;
import com.luxof.lapisworks.mixinsupport.GetStacks;
import com.luxof.lapisworks.mixinsupport.GetVAULT;
import com.luxof.lapisworks.nocarpaltunnel.HexIotaStack;
import com.luxof.lapisworks.nocarpaltunnel.SpellActionNCT;
import com.luxof.lapisworks.recipes.ImbuementRec;

import static com.luxof.lapisworks.LapisworksIDs.AMEL;
import static com.luxof.lapisworks.LapisworksIDs.IMBUEABLE;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;

import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;

/** kyra (object-Object)'s honest reaction to seeing this:
 * "this code makes no sense"
 * "..and the code is incomprehensible anyway.."
 * I can confirm, this code is indeed incomprehensible. */
public class ImbueAmel extends SpellActionNCT {
    public int getArgc() {
        return 1;
    }

    @Override
    public SpellAction.Result execute(HexIotaStack stack, CastingEnvironment ctx) {
        int wantToInfuseAmount = stack.getPositiveInt(0);
        if (wantToInfuseAmount <= 0)
            // go fuck yourself
            return new SpellAction.Result(
                new DoNothing.DoNothingSpell(),
                0L,
                List.of(),
                1
            );


        VAULT vault = ((GetVAULT)ctx).grabVAULT();
        List<HeldItemInfo> heldInfos = ((GetStacks)ctx).getHeldStacksOtherFirst();


        // wanna prioritize recipes in the non-casting hand (so i don't accidentally imbue a staff)
        Optional<ImbuementRec> recipeOnOtherhand = getImbuementOnOneHand(
            ctx.getWorld(),
            heldInfos,
            ctx.getCastingHand() == Hand.MAIN_HAND ? Hand.OFF_HAND : Hand.MAIN_HAND
        );
        Optional<ImbuementRec> recipeOnCastinghand = getImbuementOnOneHand(
            ctx.getWorld(),
            heldInfos,
            ctx.getCastingHand()
        );
        Optional<ImbuementRec> recipeOpt = recipeOnOtherhand.isPresent()
            ? recipeOnOtherhand
            : recipeOnCastinghand;


        if (recipeOpt.isEmpty()) {
            // if no recipe, must test BeegInfusions which are lower prio
            Map<Identifier, BeegInfusion> beegInfusionRecipes = BeegInfusions.filter(
                heldInfos,
                ctx,
                stack.stack,
                vault
            );
            if (beegInfusionRecipes.isEmpty())
                throw new MishapBadOffhandItem(ItemStack.EMPTY.copy(), IMBUEABLE);

            BeegInfusion selected = beegInfusionRecipes.values().iterator().next();
            selected.mishapIfNeeded();

            return new SpellAction.Result(
                new SpellBeegInfusion(selected),
                selected.getCost(),
                List.of(ParticleSpray.burst(ctx.mishapSprayPos(), 1, 10)),
                1
            );
        }


        ImbuementRec recipe = recipeOpt.get();
        ItemStack items = ItemStack.EMPTY;
        Hand hand = null;
        for (HeldItemInfo held : heldInfos) {
            if (recipe.getNormal().test(held.stack()) || held.stack().isOf(recipe.getPartAmel())) {
                items = held.stack();
                hand = held.hand();
            }
        }


        var result = recipe.craft(items, vault, wantToInfuseAmount);
        if (result == null)
            throw new MishapNotEnoughItems(
                AMEL,
                vault.drain(Mutables::isAmel, 99999, true, Flags.PRESET_UpToHotbar),
                recipe.getFullAmelsCost()
            );

        int infuseAmount = result.getRight();
        return new SpellAction.Result(
            new Spell(result.getLeft(), hand, infuseAmount, vault),
            MediaConstants.DUST_UNIT * 2 * infuseAmount,
            List.of(ParticleSpray.burst(ctx.mishapSprayPos(), 1, 10 + infuseAmount)),
            1
        );
    }

    public Optional<ImbuementRec> getImbuementOnOneHand(
        ServerWorld world,
        List<HeldItemInfo> heldInfos,
        Hand hand
    ) {
        return world.getRecipeManager().getFirstMatch(
            ImbuementRec.Type.INSTANCE,
            new HandsInv(
                heldInfos.stream()
                    .flatMap(
                        info -> {
                            return info.component2() == hand
                                ? Stream.of(info.component1())
                                : Stream.of();
                        }
                    )
                    .toList()
            ),
            world
        );
    }

    public class Spell implements RenderedSpellNCT {
        public final List<ItemStack> changeToItem;
        public final Hand hand;
        public final int count;
        public final VAULT vault;

        public Spell(List<ItemStack> changeToItem, Hand hand, int count, VAULT vault) {
            this.changeToItem = changeToItem;
            this.hand = hand;
            this.count = count;
            this.vault = vault;
        }

		@Override
		public void cast(CastingEnvironment ctx) {
            vault.drain(Mutables::isAmel, count, false, Flags.PRESET_UpToHotbar);

            int changeHandTo = changeToItem.get(0).isEmpty() ? 1 : 0;
            ctx.replaceItem(any -> true, changeToItem.get(changeHandTo), hand);

            if (changeHandTo == 1 && changeToItem.size() < 3)
                return;

            ItemScatterer.spawn(
                ctx.getWorld(),
                BlockPos.ofFloored(ctx.mishapSprayPos()),
                DefaultedList.copyOf(
                    ItemStack.EMPTY,
                    changeToItem.subList(changeHandTo + 1, changeToItem.size())
                        .toArray(new ItemStack[0])
                )
            );
		}
    }

    /** really should just call it a sophisticated infusion */
    public class SpellBeegInfusion implements RenderedSpellNCT {
        public final BeegInfusion recipe;

        public SpellBeegInfusion( BeegInfusion recipe ) { this.recipe = recipe; }

        @Override
        public void cast(CastingEnvironment ctx) { this.recipe.accept(); }
    }
}
