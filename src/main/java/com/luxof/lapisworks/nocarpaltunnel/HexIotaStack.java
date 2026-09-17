package com.luxof.lapisworks.nocarpaltunnel;

import at.petrak.hexcasting.api.casting.OperatorUtils;
import at.petrak.hexcasting.api.casting.SpellList;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.iota.DoubleIota;
import at.petrak.hexcasting.api.casting.iota.EntityIota;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.IotaType;
import at.petrak.hexcasting.api.casting.iota.Vec3Iota;
import at.petrak.hexcasting.api.casting.math.HexPattern;
import at.petrak.hexcasting.api.casting.mishaps.MishapInvalidIota;
import at.petrak.hexcasting.api.casting.mishaps.MishapNotEnoughArgs;
import at.petrak.hexcasting.common.lib.hex.HexIotaTypes;

import com.luxof.lapisworks.interop.hierophantics.data.Amalgamation;
import com.luxof.lapisworks.interop.hierophantics.data.Amalgamation.AmalgamationIota;
import com.luxof.lapisworks.media.MediaTransferInterface;
import com.luxof.lapisworks.mixin.IotaAccessor;

import static com.luxof.lapisworks.Lapisworks.HEXAL_INTEROP;

import com.mojang.datafixers.util.Either;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.MobEntity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import ram.talia.hexal.common.entities.BaseCastingWisp;

public class HexIotaStack {
    private int getReverseIdx(int idx) {
        return argc == 0 ? idx : argc - (idx + 1);
    }
    private boolean closeEnough(double A, double B) {
        return DoubleIota.tolerates(A, B); // epsilon in hex is 0.0001
    }

    public List<? extends Iota> stack;
    public int argc;
    /** keeping you believing in magic, one stupid implementation at a time */
    protected CastingEnvironment ctx;

    public HexIotaStack(
        List<? extends Iota> stack,
        int argc,
        CastingEnvironment ctx
    ) {
        this.stack = stack; this.argc = argc; this.ctx = ctx;
        if (stack.size() < this.argc)
            throw new MishapNotEnoughArgs(argc, stack.size());
    }

    public BlockPos getBlockPos(int idx) { return OperatorUtils.getBlockPos(stack, idx, argc); }
    public boolean getBool(int idx) { return OperatorUtils.getBool(stack, idx, argc); }
    public double getDouble(int idx) { return OperatorUtils.getDouble(stack, idx, argc); }
    public double getDoubleBetween(int idx, double min, double max) { return OperatorUtils.getDoubleBetween(stack, idx, min, max, argc); }
    public Entity getEntity(int idx) { return OperatorUtils.getEntity(stack, idx, argc); }
    public int getInt(int idx) { return OperatorUtils.getInt(stack, idx, argc); }
    public int getIntBetween(int idx, int min, int max) { return OperatorUtils.getIntBetween(stack, idx, min, max, argc); }
    public ItemEntity getItemEntity(int idx) { return OperatorUtils.getItemEntity(stack, idx, argc); }
    public SpellList getList(int idx) { return OperatorUtils.getList(stack, idx, argc); }
    public LivingEntity getLivingEntityButNotArmorStand(int idx) { return OperatorUtils.getLivingEntityButNotArmorStand(stack, idx, argc); }
    public long getLong(int idx) { return OperatorUtils.getLong(stack, idx, argc); }
    public Either<Long, SpellList> getLongOrList(int idx) { return OperatorUtils.getLongOrList(stack, idx, argc); }
    public MobEntity getMob(int idx) { return OperatorUtils.getMob(stack, idx, argc); }
    public Either<Double, Vec3d> getNumOrVec(int idx) { return OperatorUtils.getNumOrVec(stack, idx, argc); }
    public HexPattern getPattern(int idx) { return OperatorUtils.getPattern(stack, idx, argc); }
    public ServerPlayerEntity getPlayer(int idx) { return OperatorUtils.getPlayer(stack, idx, argc); }
    public double getPositiveDouble(int idx) { return OperatorUtils.getPositiveDouble(stack, idx, argc); }
    public double getPositiveDoubleUnder(int idx, double under) { return OperatorUtils.getPositiveDoubleUnder(stack, idx, under, argc); }
    public double getPositiveDoubleUnderInclusive(int idx, double under) { return OperatorUtils.getPositiveDoubleUnderInclusive(stack, idx, under, argc); }
    public int getPositiveInt(int idx) { return OperatorUtils.getPositiveInt(stack, idx, argc); }
    public int getPositiveIntUnder(int idx, int under) { return OperatorUtils.getPositiveIntUnder(stack, idx, under, argc); }
    public int getPositiveIntUnderInclusive(int idx, int under) { return OperatorUtils.getPositiveIntUnderInclusive(stack, idx, under, argc); }
    public long getPositiveLong(int idx) { return OperatorUtils.getPositiveLong(stack, idx, argc); }
    public Vec3d getVec3(int idx) { return OperatorUtils.getVec3(stack, idx, argc); }

    public Iota get(int idx) {
        try {
            return stack.get(idx);
        } catch (IndexOutOfBoundsException e) {
            throw new MishapNotEnoughArgs(argc + 1, stack.size());
        }
    }
    public Iota getOfType(int idx, IotaType<? extends Iota> type) {
        Iota iota = get(idx);

        Identifier typeA = HexIotaTypes.REGISTRY.getId(iota.getType());
        Identifier typeB = HexIotaTypes.REGISTRY.getId(type);
        if (typeA != null && typeA.equals(typeB))
            return iota;
        else
            throw new MishapInvalidIota(iota, getReverseIdx(idx), type.typeName());
    }
    public BlockPos getBlockPosInRange(int idx) {
        BlockPos ret = getBlockPos(idx);
        ctx.assertPosInRange(ret);
        return ret;
    }
    public Vec3d getVec3InRange(int idx) {
        Vec3d ret = getVec3(idx);
        ctx.assertVecInRange(ret);
        return ret;
    }

    private ArrayList<Iota> convertToJUSTAList(SpellList list) {
        ArrayList<Iota> theFuckingList = new ArrayList<>();

        for (int i = 0; i < list.size(); i++) {
            theFuckingList.add(list.getAt(i));
        }

        return theFuckingList;
    }
    public ArrayList<Iota> getJUSTAList(int idx) {
        // is handrolling your own List really necessary bro
        // regular Lists can ALREADY convert to iterators, can't they
        // this shit doesn't even have it's own .add() :broken_heart:
        return convertToJUSTAList(getList(idx));
    }

    public Amalgamation getAmalgamation(int idx) {
        Iota iota = get(idx);
        if (iota instanceof AmalgamationIota amalgamIota)
            return amalgamIota.getAmalgamation();
        else
            throw new MishapInvalidIota(
                iota,
                getReverseIdx(idx),
                Text.translatable("iota.lapisworks.amalgamation")
                    .formatted(Formatting.DARK_PURPLE)
            );
    }

    public MediaTransferInterface getMediaTransferInterface(int idx) {
        Iota iota = get(idx);
        Object iotaPayload = ((IotaAccessor)iota).lapisworks$getPayload();
        MediaTransferInterface MTI = null;
        if (iota instanceof MediaTransferInterface mti) {
            MTI = mti;

        } if (iotaPayload instanceof MediaTransferInterface mti) {
            MTI = mti;

        } else if (
            iota instanceof Vec3Iota vec3Iota &&
            ctx.getWorld().getBlockEntity(BlockPos.ofFloored(vec3Iota.getVec3())) instanceof
                MediaTransferInterface mti &&
            mti.isMTIAtThisTime(ctx)
        ) {
            ctx.assertVecInRange(vec3Iota.getVec3());
            MTI = mti;

        } else if (
            iota instanceof EntityIota entityIota &&
            entityIota.getEntity() instanceof MediaTransferInterface mti &&
            mti.isMTIAtThisTime(ctx)
        ) {
            ctx.assertEntityInRange(entityIota.getEntity());
            MTI = mti;

        }

        if (MTI == null || !MTI.isMTIAtThisTime(ctx)) {
            throw new MishapInvalidIota(
                iota,
                idx,
                Text.translatable("iota.lapisworks.media_transfer_interface")
                    .formatted(Formatting.LIGHT_PURPLE)
            );
        }
        return MTI;
    }

    // so magic is real
    // and it'll save me about 6 letters
    /** returns an empty <code>Optional</code> *only* if Hexal isn't loaded.
     * I'd make it return a <code>BaseCastingWisp</code> with mixin crimes against codekind,
     * but like. Let's be fr. I'm only saving you a <code>.get()</code> by doing that. */
    public Optional<BaseCastingWisp> getBaseCastingWisp(int idx) {
        return HEXAL_INTEROP
            ? LapisalHexIotaStack.getBaseCastingWisp(stack, idx, argc)
            : Optional.empty();
    }
    public Optional<BaseCastingWisp> getBaseCastingWispOwnedByThis(int idx) {
        return HEXAL_INTEROP
            ? LapisalHexIotaStack.getBaseCastingWispOwnedByThis(stack, idx, argc, ctx)
            : Optional.empty();
    }

    public int getIntAbove(int idx, int above) {
        Iota iota = get(idx);

        if (
            !(iota instanceof DoubleIota dub) ||
            !closeEnough(dub.getDouble(), Math.round(dub.getDouble())) ||
            dub.getDouble() <= above
        )
            throw new MishapInvalidIota(
                iota,
                idx,
                Text.translatable("iota.lapisworks.int_above_n", above)
            );

        return (int)Math.round(dub.getDouble());
    }

    /** grabs a pattern or a pattern list for you. */
    public ArrayList<Iota> getEvaluatable(int idx) {
        return OperatorUtils.evaluatable(get(idx), argc).map(
            iota -> new ArrayList<>(List.of(iota)),
            this::convertToJUSTAList
        );
    }

    public Either<BlockPos, Entity> getBlockPosOrEntity(int idx) {
        Iota iota = get(idx);

        if (iota instanceof Vec3Iota vecIota)
            return Either.left(BlockPos.ofFloored(vecIota.getVec3()));
        else if (iota instanceof EntityIota entityIota)
            return Either.right(entityIota.getEntity());

        throw new MishapInvalidIota(
            iota,
            idx,
            Text.translatable("mishaps.lapisworks.descs.entityorblockposiota")
        );
    }
}
