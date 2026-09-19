package com.luxof.lapisworks.actions;

import at.petrak.hexcasting.api.casting.ParticleSpray;
import at.petrak.hexcasting.api.casting.castables.SpellAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.misc.MediaConstants;

import com.google.common.collect.ImmutableMultimap;

import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;

import com.luxof.lapisworks.VAULT.Flags;
import com.luxof.lapisworks.VAULT.VAULT;
import com.luxof.lapisworks.init.Mutables.Mutables;
import com.luxof.lapisworks.mixinsupport.GetVAULT;
import com.luxof.lapisworks.nocarpaltunnel.HexIotaStack;
import com.luxof.lapisworks.nocarpaltunnel.SpellActionNCT;

import static com.luxof.lapisworks.LapisworksIDs.AMEL;
import static com.luxof.lapisworks.LapisworksIDs.ATK_RANGE_ENHANCEMENT_UUID;
import static com.luxof.lapisworks.LapisworksIDs.REACH_ENHANCEMENT_UUID;
import static com.luxof.lapisworks.MishapThrowerJava.assertItemAmount;

import java.util.List;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.AttributeContainer;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributeModifier.Operation;

public class MoarReachYouBitch extends SpellActionNCT {
    public int argc = 2;

    public static final int amelCost = 16;
    public static final int amelCostMultiplier = 4;
    public static final int reachIncrease = 3;
    public static final int attackReachIncrease = 1;
    public static final EntityAttributeModifier REACH_MODIFIER = new EntityAttributeModifier(
        REACH_ENHANCEMENT_UUID,
        "Lapisworks reach enhancement",
        reachIncrease,
        Operation.ADDITION
    );
    public static final EntityAttributeModifier ATTACK_REACH_MODIFIER = new EntityAttributeModifier(
        ATK_RANGE_ENHANCEMENT_UUID,
        "Lapisworks attack reach ehancement",
        attackReachIncrease,
        Operation.ADDITION
    );
    @SuppressWarnings("null")
    private final ImmutableMultimap<EntityAttribute, EntityAttributeModifier> modifiers = ImmutableMultimap.of(
        ReachEntityAttributes.REACH, REACH_MODIFIER,
        ReachEntityAttributes.ATTACK_RANGE, ATTACK_REACH_MODIFIER
    );

    @Override
    public SpellAction.Result execute(HexIotaStack stack, CastingEnvironment ctx) {
        LivingEntity entity = stack.getPlayer(0);
        boolean enableIt = stack.getBool(1);

        boolean expendShit = !entity.getAttributes().hasModifierForAttribute(
            ReachEntityAttributes.REACH,
            REACH_ENHANCEMENT_UUID
        );
        expendShit = enableIt ? expendShit : !expendShit;

        if (!expendShit)
            return new SpellAction.Result(
                new DoNothing.DoNothingSpell(),
                0L,
                List.of(ParticleSpray.burst(ctx.mishapSprayPos(), 2, 25)),
                1
            );

        VAULT vault = ((GetVAULT)ctx).grabVAULT();
        assertItemAmount(ctx, Mutables::isAmel, AMEL, amelCost);

        return new SpellAction.Result(
            new Spell(entity, enableIt, vault),
            MediaConstants.SHARD_UNIT * 4,
            List.of(ParticleSpray.burst(ctx.mishapSprayPos(), 2, 25)),
            1
        );
    }

    public class Spell implements RenderedSpellNCT {
        public final LivingEntity entity;
        public final boolean enableIt;
        public final VAULT vault;

        public Spell(
            LivingEntity entity,
            boolean enableIt,
            VAULT vault
        ) {
            this.entity = entity;
            this.enableIt = enableIt;
            this.vault = vault;
        }

        @Override
		public void cast(CastingEnvironment ctx) {
            AttributeContainer attrs = entity.getAttributes();
            if (attrs.hasModifierForAttribute(ReachEntityAttributes.REACH, REACH_ENHANCEMENT_UUID)) return;

            vault.drain(
                Mutables::isAmel,
                amelCost,
                false,
                Flags.PRESET_UpToHotbar
            );
            if (enableIt)
                entity.getAttributes().addTemporaryModifiers(modifiers);
            else
                entity.getAttributes().removeModifiers(modifiers);
		}
    }
}
