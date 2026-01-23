package com.luxof.lapisworks.actions.great;

import at.petrak.hexcasting.api.casting.ParticleSpray;
import at.petrak.hexcasting.api.casting.castables.SpellAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;

import com.luxof.lapisworks.VAULT.Flags;
import com.luxof.lapisworks.VAULT.VAULT;
import com.luxof.lapisworks.init.EnchantCountKeeper;
import com.luxof.lapisworks.init.Mutables.Mutables;
import com.luxof.lapisworks.mishaps.MishapAlreadyHasEnchantment;
import com.luxof.lapisworks.mixinsupport.GetVAULT;
import com.luxof.lapisworks.mixinsupport.LapisworksInterface;
import com.luxof.lapisworks.nocarpaltunnel.HexIotaStack;
import com.luxof.lapisworks.nocarpaltunnel.SpellActionNCT;

import static com.luxof.lapisworks.LapisworksIDs.AMEL;
import static com.luxof.lapisworks.MishapThrowerJava.assertItemAmount;

import java.util.List;

import net.minecraft.entity.LivingEntity;
import net.minecraft.text.Text;

/** responsibility of mixin to make enchantment do something falls on the user of this.
 * Also take this' enchantmentIdx if you want the index you need to give to LapisworksInterface's stuff. */
public class GenericEnchant extends SpellActionNCT {
    public boolean requiresEnlightenment = true;
    public final int enchantmentIdx;
    public final int maxLevel;
    public final int requiredAmel;
    public final long requiredMedia;
    public final Text enchantmentLangKey;

    public int argc = 1;

    public GenericEnchant(
        int maxLevel,
        int requiredAmel,
        long requiredMedia,
        String enchantmentLangKey
    ) {
        this.enchantmentIdx = EnchantCountKeeper.registerMyEnchantment();
        this.maxLevel = maxLevel;
        this.requiredAmel = requiredAmel;
        this.requiredMedia = requiredMedia;
        this.enchantmentLangKey = Text.translatable(enchantmentLangKey);
    }
    public GenericEnchant(
        int maxLevel,
        int requiredAmel,
        long requiredMedia,
        Text enchantmentLangKey
    ) {
        this.enchantmentIdx = EnchantCountKeeper.registerMyEnchantment();
        this.maxLevel = maxLevel;
        this.requiredAmel = requiredAmel;
        this.requiredMedia = requiredMedia;
        this.enchantmentLangKey = enchantmentLangKey;
    }

    @Override
    public SpellAction.Result execute(HexIotaStack stack, CastingEnvironment ctx) {
        LivingEntity entity = stack.getLivingEntityButNotArmorStand(0);

        if (((LapisworksInterface)entity).getEnchant(this.enchantmentIdx) >= this.maxLevel) {
            throw new MishapAlreadyHasEnchantment(
                    entity,
                    this.enchantmentLangKey,
                    this.enchantmentIdx,
                    this.maxLevel
            );
        }

        VAULT vault = ((GetVAULT)ctx).grabVAULT();
        assertItemAmount(ctx, Mutables::isAmel, AMEL, argc);

        return new SpellAction.Result(
            new Spell(entity, vault),
            this.requiredMedia,
            List.of(ParticleSpray.burst(entity.getPos(), 3, 25)),
            1
        );
    }

    public class Spell implements RenderedSpellNCT {
        public final LivingEntity entity;
        public final VAULT vault;

        public Spell(LivingEntity entity, VAULT vault) {
            this.entity = entity; this.vault = vault;
        }

		@Override
		public void cast(CastingEnvironment ctx) {
            vault.drain(Mutables::isAmel, requiredAmel, false, Flags.PRESET_UpToHotbar);
            ((LapisworksInterface)this.entity).incrementEnchant(enchantmentIdx);
		}
    }
}
