package com.luxof.lapisworks.chalk;

import at.petrak.hexcasting.api.HexAPI;
import at.petrak.hexcasting.api.casting.ParticleSpray;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.IotaType;
import at.petrak.hexcasting.api.casting.iota.NullIota;
import at.petrak.hexcasting.api.pigment.FrozenPigment;
import at.petrak.hexcasting.api.player.Sentinel;
import at.petrak.hexcasting.common.lib.HexAttributes;

import com.luxof.lapisworks.blocks.entities.TuneableAmethystEntity;
import com.luxof.lapisworks.mixinsupport.EnchSentInterface;
import com.luxof.lapisworks.mixinsupport.RitualsUtil;

import java.util.UUID;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;

import org.jetbrains.annotations.Nullable;

public abstract class RitualExecutionState {
    public BlockPos currentPos;
    /** for intersections. In case of 3-ways where forward exists not, simply pick a random side. */
    @Nullable public Direction forward;
    public CastingImage currentImage;
    @Nullable protected UUID caster;
    @Nullable protected FrozenPigment pigment;
    @Nullable protected Iota tunedFrequency;

    protected RitualExecutionState(
        BlockPos currentPos,
        Direction forward,
        CastingImage currentImage,
        @Nullable UUID caster,
        @Nullable FrozenPigment pigment
    ) {
        this.currentPos = currentPos;
        this.forward = forward;
        this.currentImage = currentImage;
        this.caster = caster;
        this.pigment = pigment;
        this.tunedFrequency = null;
    }
    protected RitualExecutionState(
        BlockPos currentPos,
        Direction forward,
        CastingImage currentImage,
        @Nullable UUID caster,
        @Nullable FrozenPigment pigment,
        @Nullable Iota tunedFrequency
    ) {
        this.currentPos = currentPos;
        this.forward = forward;
        this.currentImage = currentImage;
        this.caster = caster;
        this.pigment = pigment;
        this.tunedFrequency = tunedFrequency;
    }
    protected RitualExecutionState(
        NbtCompound nbt,
        ServerWorld world
    ) {
        loadBase(nbt, world);
    }



    @Nullable
    public ServerPlayerEntity getCaster(ServerWorld world) {
        if (caster == null) return null;
        if (world.getEntity(caster) instanceof ServerPlayerEntity sp) return sp;
        return null;
    }
    @Nullable
    public FrozenPigment getPigment() {
        return pigment;
    }
    @Nullable
    public FrozenPigment setPigment(@Nullable FrozenPigment pigment) {
        FrozenPigment old = this.pigment;
        this.pigment = pigment;
        return old;
    }
    @Nullable
    public Iota getTunedFrequency() {
        return tunedFrequency;
    }
    /** to clear, you can also pass in a NullIota. */
    @Nullable
    public Iota setTunedFrequency(@Nullable Iota frequency) {
        Iota old = tunedFrequency;
        tunedFrequency = frequency instanceof NullIota ? null : frequency;
        return old;
    }



    private boolean vecInRange(Vec3d vec, Vec3d pos, double range) {
        return vec.squaredDistanceTo(pos) <= range*range;
    }
    public boolean isVecInAmbitOfPlayer(Vec3d vec, ServerWorld world, double ambitMult) {
        ServerPlayerEntity caster = getCaster(world);
        if (caster == null) return false;


        double playerAmbit = caster.getAttributeValue(HexAttributes.AMBIT_RADIUS);
        if (vecInRange(vec, caster.getPos(), playerAmbit*ambitMult))
            return true;


        Sentinel sentinel = HexAPI.instance().getSentinel(caster);
        double sentinelAmbit = caster.getAttributeValue(HexAttributes.SENTINEL_RADIUS);
        if (
            sentinel != null &&
            sentinel.extendsRange() &&
            vecInRange(vec, sentinel.position(), sentinelAmbit*ambitMult)
        )
            return true;


        EnchSentInterface enchantedSentinel = (EnchSentInterface)caster;
        Vec3d enchSentPos = enchantedSentinel.getEnchantedSentinel();
        double enchSentAmbit = enchantedSentinel.getEnchantedSentinelAmbit();
        if (
            enchSentPos != null &&
            vecInRange(vec, enchSentPos, enchSentAmbit*ambitMult)
        )
            return true;


        return false;
    }

    public boolean isVecInAmbitOfTuneableAmethyst(Vec3d vec, ServerWorld world, double ambitMult) {
        RitualsUtil ritualsUtil = (RitualsUtil)world;

        if (tunedFrequency == null) return false;

        for (BlockPos tunedPos : ritualsUtil.getTuneables(tunedFrequency)) {
            TuneableAmethystEntity tuned = (TuneableAmethystEntity)world.getBlockEntity(tunedPos);

            if (tunedPos.getSquaredDistance(vec) <= tuned.getAmbitSqr()*ambitMult)
                return true;
        }

        return false;
    }

    public abstract boolean isVecInAmbit(Vec3d vec, ServerWorld world);



    protected void saveBase(NbtCompound nbt) {
        NbtCompound posNbt = new NbtCompound();
        posNbt.putInt("x", currentPos.getX());
        posNbt.putInt("y", currentPos.getY());
        posNbt.putInt("z", currentPos.getZ());
        nbt.put("currentPos", posNbt);
        
        nbt.putString("forward", forward.toString());

        nbt.put("image", currentImage.serializeToNbt());

        if (caster != null)
            nbt.putUuid("caster", caster);

        if (pigment != null)
            nbt.put("pigment", pigment.serializeToNBT());

        if (tunedFrequency != null)
            nbt.put("tuned", tunedFrequency.serialize());
    }
    public NbtCompound save() {
        NbtCompound nbt = new NbtCompound();
        save(nbt);
        return nbt;
    }
    public abstract void save(NbtCompound nbt);
    protected static final record BaseConstructorArguments (
        BlockPos currentPos,
        Direction forward,
        CastingImage currentImage,
        @Nullable UUID caster,
        @Nullable FrozenPigment pigment,
        @Nullable Iota tunedFrequency
    ) {}
    protected static BaseConstructorArguments loadBase(NbtCompound nbt, ServerWorld world) {
        NbtCompound posNbt = nbt.getCompound("currentPos");
        BlockPos currentPos = new BlockPos(
            posNbt.getInt("x"),
            posNbt.getInt("y"),
            posNbt.getInt("z")
        );

        Direction forward = Direction.byName(nbt.getString("forward"));

        CastingImage img = CastingImage.loadFromNbt(nbt.getCompound("image"), world);

        UUID caster = null;
        if (nbt.contains("caster")) caster = nbt.getUuid("caster");

        FrozenPigment pigment = null;
        if (nbt.contains("pigment")) pigment = FrozenPigment.fromNBT(nbt.getCompound("pigment"));

        Iota tuned = null;
        if (nbt.contains("tuned")) tuned = IotaType.deserialize(nbt.getCompound("tuned"), world);

        return new BaseConstructorArguments(currentPos, forward, img, caster, pigment, tuned);
    }



    /** returns the amount extracted. */
    public abstract long extractMedia(long cost, boolean simulate, ServerWorld world);
    public abstract void printMessage(Text message, ServerWorld world);
    public abstract void printMishap(Text mishapMessage, ServerWorld world);



    public abstract Vec3d getMishapSprayPos();
    protected void sprayParticlesOutOf(
        ServerWorld world,
        BlockPos pos,
        RitualComponent component,
        FrozenPigment pigment
    ) {
        sprayParticlesOutOf(world, pos, component, pigment, 40);
    }
    protected void sprayParticlesOutOf(
        ServerWorld world,
        BlockPos pos,
        RitualComponent component,
        FrozenPigment pigment,
        int amount
    ) {
        Vec3d particleSprayDir = Vec3d.of(component.getParticleSprayDir().getVector());

        new ParticleSpray(
            pos.toCenterPos().add(
                particleSprayDir.multiply(-0.45)
            ),
            particleSprayDir.multiply(2.0),
            0.2,
            0.7853981633974483,
            amount
        ).sprayParticles(world, pigment);
    }
    /** Returns whether to continue. */
    public abstract boolean tick(ServerWorld world);
}
