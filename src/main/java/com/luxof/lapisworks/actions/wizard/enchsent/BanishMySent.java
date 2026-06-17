package com.luxof.lapisworks.actions.wizard.enchsent;

import at.petrak.hexcasting.api.casting.ParticleSpray;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.mishaps.MishapBadCaster;

import com.luxof.lapisworks.mixinsupport.EnchSentInterface;
import com.luxof.lapisworks.nocarpaltunnel.HexIotaStack;
import com.luxof.lapisworks.nocarpaltunnel.SpellActionNCT;

import static com.luxof.lapisworks.LapisworksIDs.SEND_SENT;

import java.util.List;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

import org.joml.Vector3f;

public class BanishMySent extends SpellActionNCT {
    public int argc = 0;

    public Result execute(HexIotaStack stack, CastingEnvironment ctx) {
        if !(ctx.getCastingEntity() instanceof ServerPlayerEntity)
            throw new MishapBadCaster();
        
        return new Result(
            new Spell(),
            dust(0.01),
            List.of(ParticleSpray.burst(ctx.mishapSprayPos(), 3, 20)),
            1L
        );
    }

    public class Spell implements RenderedSpellNCT {
        public void cast(CastingEnvironment ctx) {
            ServerPlayerEntity caster = (ServerPlayerEntity)ctx.getCastingEntity();

            ((EnchSentInterface)caster).setEnchantedSentinel(null, null);

            PacketByteBuf buf = PacketByteBufs.create();
            buf.writeBoolean(true);
            buf.writeVector3f(new Vector3f());
            buf.writeDouble(0.0);

            ServerPlayNetworking.send(caster, SEND_SENT, buf);
        }
    }
}
