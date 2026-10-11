package com.luxof.lapisworks.actions.wizard.enchsent;

import at.petrak.hexcasting.api.casting.ParticleSpray;
import at.petrak.hexcasting.api.casting.castables.SpellAction;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.mishaps.MishapBadCaster;
import at.petrak.hexcasting.api.casting.mishaps.MishapBadLocation;
import at.petrak.hexcasting.api.casting.mishaps.MishapUnenlightened;
import at.petrak.hexcasting.api.misc.MediaConstants;
import at.petrak.hexcasting.common.lib.HexAttributes;

import com.luxof.lapisworks.interop.valkyrienskies.ValkyrienUtils;
import com.luxof.lapisworks.mixinsupport.EnchSentInterface;
import com.luxof.lapisworks.nocarpaltunnel.HexIotaStack;
import com.luxof.lapisworks.nocarpaltunnel.SpellActionNCT;

import static com.luxof.lapisworks.Lapisworks.VALKYRIEN_SKIES_INTEROP;
import static com.luxof.lapisworks.LapisworksIDs.SEND_SENT;

import java.util.List;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.Vec3d;

public class CreateEnchSent extends SpellActionNCT {
    public int argc = 2;

    public Result execute(HexIotaStack stack, CastingEnvironment ctx) {
        if (!(ctx.getCastingEntity() instanceof ServerPlayerEntity caster))
            throw new MishapBadCaster();
        else if (!ctx.isEnlightened())
            throw new MishapUnenlightened();

        Vec3d pos = stack.getVec3(0);
        double distance = VALKYRIEN_SKIES_INTEROP
            ? ValkyrienUtils.distance(ctx.getWorld(), caster.getPos(), pos)
            : caster.getPos().distanceTo(pos);

        Double currEnchSentAmbit = ((EnchSentInterface)caster).getEnchantedSentinelAmbit();
        double casterAmbitWithoutEnchSent = caster.getAttributeValue(HexAttributes.AMBIT_RADIUS)
            + (currEnchSentAmbit != null ? currEnchSentAmbit * 2.0 : 0.0);

        double requestedAmbit = stack.getPositiveDoubleUnder(
            1,
            casterAmbitWithoutEnchSent * 2.0
        );

        if (distance > (casterAmbitWithoutEnchSent - requestedAmbit / 2.0))
            // you will NOT fuck with this to do better sent walk!
            throw new MishapBadLocation(pos, "too_far");


        return new SpellAction.Result(
            new Spell(caster, pos, requestedAmbit),
            MediaConstants.DUST_UNIT * 5,
            List.of(ParticleSpray.burst(caster.getPos(), 2, 15)),
            1
        );
    }

    public class Spell implements RenderedSpellNCT {
        public final PlayerEntity caster;
        public final Vec3d pos;
        public final double ambit;

        public Spell(PlayerEntity caster, Vec3d pos, double ambit) {
            this.caster = caster;
            this.pos = pos;
            this.ambit = ambit;
        }

		public void cast(CastingEnvironment ctx) {
            ((EnchSentInterface)this.caster).setEnchantedSentinel(this.pos, this.ambit);
            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeBoolean(false);
            buf.writeDouble(this.pos.x);
            buf.writeDouble(this.pos.y);
            buf.writeDouble(this.pos.z);
            buf.writeDouble(this.ambit);
            ServerPlayNetworking.send((ServerPlayerEntity)this.caster, SEND_SENT, buf);
		}
    }
}
