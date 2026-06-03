package com.luxof.lapisworks.items;

import at.petrak.hexcasting.common.lib.HexSounds;

import com.luxof.lapisworks.init.Mutables.Mutables;

import static com.luxof.lapisworks.Lapisworks.id;
import static com.luxof.lapisworks.LapisworksIDs.DIARIES_TOOLTIP_1;
import static com.luxof.lapisworks.LapisworksIDs.DIARIES_TOOLTIP_2;
import static com.luxof.lapisworks.LapisworksIDs.DIARIES_TOOLTIP_3;
import static com.luxof.lapisworks.LapisworksIDs.DIARIES_TOOLTIP_4;
import static com.luxof.lapisworks.LapisworksIDs.DIARY_UNREADABLE;
import static com.luxof.lapisworks.LapisworksIDs.GOT_ALL_DIARIES;
import static com.luxof.lapisworks.LapisworksIDs.UNLOCK_SHIT_FOR_HEXCESSIBLE;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import net.minecraft.advancement.Advancement;
import net.minecraft.advancement.PlayerAdvancementTracker;
import net.minecraft.advancement.criterion.Criteria;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.ServerAdvancementLoader;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import vazkii.patchouli.client.base.ClientAdvancements;

public class WizardDiaries extends Item {
    public WizardDiaries(Settings settings) { super(settings); }

    public TypedActionResult<ItemStack> useClient(
        World world, PlayerEntity user, Hand hand, ItemStack stack, List<Identifier> shuffled
    ) {
        return ClientAdvancements.hasDone("lapisworks:got_lapis")
            ? TypedActionResult.success(stack)
            : TypedActionResult.fail(stack);
    }

    public TypedActionResult<ItemStack> useServer(
        World world, PlayerEntity user, Hand hand, ItemStack stack, List<Identifier> shuffled
    ) {
        ServerPlayerEntity suser = (ServerPlayerEntity)user;
        ServerAdvancementLoader advLoader = suser.getServer().getAdvancementLoader();
        PlayerAdvancementTracker advTracker = suser.getAdvancementTracker();

        Advancement gotLapisAdvancement = advLoader.get(id("got_lapis"));
        if (gotLapisAdvancement == null || !advTracker.getProgress(gotLapisAdvancement).isDone()) {
            user.sendMessage(DIARY_UNREADABLE);
            return TypedActionResult.fail(stack);
        }

        Identifier chosenAdvancement = null;
        for (int i = 0; i < shuffled.size(); i++) {
            Identifier advId = shuffled.get(i);

            Advancement adv = advLoader.get(advId);
            if (adv != null && advTracker.getProgress(adv).isDone()) {
                chosenAdvancement = advId;
                break;
            }
        }

        if (chosenAdvancement == null) {
            user.sendMessage(GOT_ALL_DIARIES, true);
            user.addExperience(100);
            return TypedActionResult.success(stack);
        }

        suser.getAdvancementTracker().grantCriterion(advLoader.get(chosenAdvancement), "grant");

        Criteria.CONSUME_ITEM.trigger(suser, stack);
        suser.getStatHandler().increaseStat(suser, Stats.USED.getOrCreateStat(this), 1);
        if (!suser.isCreative())
            stack.decrement(1);

        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeIdentifier(chosenAdvancement);
        ServerPlayNetworking.send(suser, UNLOCK_SHIT_FOR_HEXCESSIBLE, buf);

        return TypedActionResult.success(stack);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        user.playSound(HexSounds.READ_LORE_FRAGMENT, 1.0F, 1.0F);
        ItemStack stack = user.getStackInHand(hand);

        List<Identifier> shuffled = new ArrayList<>(
            Mutables.wizardDiariesGainableAdvancements.keySet()
        );
        Collections.shuffle(shuffled);

        return user.getWorld().isClient
            ? useClient(world, user, hand, stack, shuffled)
            : useServer(world, user, hand, stack, shuffled);
    }


    @Override
    public void appendTooltip(ItemStack stack, World world, List<Text> tooltip, TooltipContext ctx) {
        tooltip.add(DIARIES_TOOLTIP_1.copy().formatted(Formatting.DARK_PURPLE));
        tooltip.add(DIARIES_TOOLTIP_2.copy().formatted(Formatting.DARK_PURPLE));
        tooltip.add(DIARIES_TOOLTIP_3.copy().formatted(Formatting.DARK_PURPLE));
        tooltip.add(DIARIES_TOOLTIP_4.copy().formatted(Formatting.DARK_PURPLE));
    }
}
