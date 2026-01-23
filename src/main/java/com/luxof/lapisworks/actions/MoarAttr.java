package com.luxof.lapisworks.actions;

import at.petrak.hexcasting.api.casting.ParticleSpray;
import at.petrak.hexcasting.api.casting.castables.SpellAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.misc.MediaConstants;

import com.luxof.lapisworks.VAULT.Flags;
import com.luxof.lapisworks.VAULT.VAULT;
import com.luxof.lapisworks.init.Mutables.Mutables;
import com.luxof.lapisworks.mixinsupport.GetVAULT;
import com.luxof.lapisworks.mixinsupport.LapisworksInterface;
import com.luxof.lapisworks.nocarpaltunnel.HexIotaStack;
import com.luxof.lapisworks.nocarpaltunnel.SpellActionNCT;

import static com.luxof.lapisworks.LapisworksIDs.AMEL;
import static com.luxof.lapisworks.MishapThrowerJava.assertItemAmount;

import java.util.List;

import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;

// "too late" (lazy) to make this use EntityAttributeModifiers instead
public class MoarAttr extends SpellActionNCT {
    public int argc = 2;

    // i always keep my shit public in case someone needs to do something cursed
    public EntityAttribute modifyAttribute;
    public double limitModifier;
    public double limitOffset; // "only give mobs +60 +2xbase hp at max!"
    public double attrCompensateMult; // base plr speed is 0.1? set this to 10 or smth.
    public int expendedAmelModifier;
    public boolean playerOnly;

    public MoarAttr(
        EntityAttribute modifyAttribute,
        double limitModifier,
        double limitOffset,
        double attrCompensateMult,
        int expendedAmelModifier,
        boolean playerOnly
    ) {
        this.modifyAttribute = modifyAttribute;
        this.limitModifier = limitModifier;
        this.limitOffset = limitOffset;
        this.attrCompensateMult = attrCompensateMult;
        this.expendedAmelModifier = expendedAmelModifier;
        this.playerOnly = playerOnly;
    }

    @Override
    public SpellAction.Result execute(HexIotaStack args, CastingEnvironment ctx) {
        LivingEntity entity;
        if (!playerOnly) { entity = args.getLivingEntityButNotArmorStand(0); }
        else { entity = args.getPlayer(0); }
        double count = args.getPositiveDouble(1);

        VAULT vault = ((GetVAULT)ctx).grabVAULT();

        double currentCombinedVal = entity.getAttributes()
            .getCustomInstance(this.modifyAttribute)
            .getBaseValue();
        double currentJuicedUpVal = ((LapisworksInterface)entity).getAmountOfAttrJuicedUpByAmel(
            this.modifyAttribute
        );
        double defaultVal = currentCombinedVal - currentJuicedUpVal;
        double defaultValCompensated = defaultVal * this.attrCompensateMult;
        double baseLimit = defaultValCompensated * this.limitModifier + this.limitOffset;
        double currentLimit = baseLimit - currentCombinedVal;

        double addToVal = Math.min(
            Math.min(defaultValCompensated + count, baseLimit) - defaultValCompensated,
            currentLimit
        );
        int expendedAmel = (int)Math.ceil(addToVal * this.expendedAmelModifier);

        assertItemAmount(ctx, Mutables::isAmel, AMEL, expendedAmel);

        return new SpellAction.Result(
            // caster is kinda being operated on but that's not the main effect so 2nd prio
            new Spell(
                entity, vault, this.modifyAttribute,
                expendedAmel, addToVal / this.attrCompensateMult),
            Math.max(MediaConstants.SHARD_UNIT * expendedAmel, 0),
            List.of(ParticleSpray.burst(ctx.mishapSprayPos(), 2, 25)),
            1
        );
    }

    public class Spell implements RenderedSpellNCT {
        public final LivingEntity entity;
        public final VAULT vault;
        public final EntityAttribute attr;
        public final int expendedAmel;
        public final double addVal;

        public Spell(
            LivingEntity entity,
            VAULT vault,
            EntityAttribute attr,
            int expendedAmel,
            double addVal
        ) {
            this.entity = entity;
            this.vault = vault;
            this.expendedAmel = expendedAmel;
            this.addVal = addVal;
            this.attr = attr;
        }

		@Override
		public void cast(CastingEnvironment ctx) {
            vault.drain(Mutables::isAmel, expendedAmel, false, Flags.PRESET_UpToHotbar);
            double juicedUpAttr = ((LapisworksInterface)this.entity).getAmountOfAttrJuicedUpByAmel(this.attr);
            ((LapisworksInterface)this.entity).setAmountOfAttrJuicedUpByAmel(
                this.attr,
                juicedUpAttr + this.addVal
            );
		}
    }
}
