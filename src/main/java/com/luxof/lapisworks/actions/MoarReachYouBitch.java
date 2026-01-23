package com.luxof.lapisworks.actions;

import at.petrak.hexcasting.api.casting.OperatorUtils;
import at.petrak.hexcasting.api.casting.ParticleSpray;
import at.petrak.hexcasting.api.casting.RenderedSpell;
import at.petrak.hexcasting.api.casting.castables.SpellAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.OperationResult;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.misc.MediaConstants;

import com.google.common.collect.ImmutableMultimap;

import com.jamieswhiteshirt.reachentityattributes.ReachEntityAttributes;

import com.luxof.lapisworks.VAULT.Flags;
import com.luxof.lapisworks.VAULT.VAULT;
import com.luxof.lapisworks.init.Mutables.Mutables;
import com.luxof.lapisworks.mixinsupport.GetVAULT;

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
import net.minecraft.nbt.NbtCompound;

public class MoarReachYouBitch implements SpellAction {
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
    private final ImmutableMultimap<EntityAttribute, EntityAttributeModifier> modifiers = ImmutableMultimap.of(
        ReachEntityAttributes.REACH, REACH_MODIFIER,
        ReachEntityAttributes.ATTACK_RANGE, ATTACK_REACH_MODIFIER
    );

    public int getArgc() {
        return 1;
    }

    @Override
    public SpellAction.Result execute(List<? extends Iota> args, CastingEnvironment ctx) {
        LivingEntity entity = OperatorUtils.getPlayer(args, 0, getArgc());

        boolean expendShit = !entity.getAttributes().hasModifierForAttribute(
            ReachEntityAttributes.REACH,
            REACH_ENHANCEMENT_UUID
        );

        VAULT vault = ((GetVAULT)ctx).grabVAULT();
        assertItemAmount(ctx, Mutables::isAmel, AMEL, amelCost);

        return new SpellAction.Result(
            new Spell(entity, vault),
            Math.max(MediaConstants.SHARD_UNIT * (expendShit ? 4 : 0), 0),
            List.of(ParticleSpray.burst(ctx.mishapSprayPos(), 2, 25)),
            1
        );
    }

    public class Spell implements RenderedSpell {
        public final LivingEntity entity;
        public final VAULT vault;

        public Spell(
            LivingEntity entity,
            VAULT vault
        ) {
            this.entity = entity;
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
            entity.getAttributes().addTemporaryModifiers(modifiers);
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
