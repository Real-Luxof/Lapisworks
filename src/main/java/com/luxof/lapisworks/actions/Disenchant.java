package com.luxof.lapisworks.actions;

import at.petrak.hexcasting.api.casting.ParticleSpray;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;

import java.util.List;

import com.luxof.lapisworks.init.EnchantCountKeeper;
import com.luxof.lapisworks.mixinsupport.LapisworksInterface;
import com.luxof.lapisworks.nocarpaltunnel.HexIotaStack;
import com.luxof.lapisworks.nocarpaltunnel.SpellActionNCT;

import net.minecraft.entity.LivingEntity;

public class Disenchant extends SpellActionNCT {
    public int argc = 2;

    public Result execute(HexIotaStack stack, CastingEnvironment ctx) {
        LivingEntity livingEntity = stack
            .getLivingEntityButNotArmorStandOrPlayersExceptTheCaster(0);
        LapisworksInterface ent = (LapisworksInterface)livingEntity;

        int enchantment = stack.getIntBetween(1, 0, EnchantCountKeeper.getTally());
        boolean doSomething = ent.getEnchant(enchantment) > 0;
        
        return new Result(
            new Spell(ent, enchantment),
            charged(doSomething ? 1 : 0),
            List.of(
                ParticleSpray.cloud(livingEntity.getPos(), 2, 20),
                ParticleSpray.cloud(ctx.mishapSprayPos(), 2, 20)
            ),
            1
        );
    }

    public class Spell implements RenderedSpellNCT {
        public final LapisworksInterface entity;
        public final int enchantment;

        public Spell(LapisworksInterface entity, int enchantment) {
            this.entity = entity;
            this.enchantment = enchantment;
        }

        public void cast(CastingEnvironment ctx) {
            entity.setEnchantmentLevel(
                enchantment,
                entity.getEnchant(enchantment) > 0 ? entity.getEnchant(enchantment) - 1 : 0
            );
        }
    }
}
