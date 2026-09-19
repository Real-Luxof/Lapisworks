package com.luxof.lapisworks.interop.hierophantics.data;

import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import at.petrak.hexcasting.api.casting.eval.vm.CastingVM;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.IotaType;
import at.petrak.hexcasting.api.casting.iota.Vec3Iota;
import at.petrak.hexcasting.common.lib.HexSounds;

import com.luxof.lapisworks.init.LapisConfig;
import com.luxof.lapisworks.interop.hierophantics.blocks.ChariotMindEntity;

import static com.luxof.lapisworks.Lapisworks.clamp;
import static com.luxof.lapisworks.Lapisworks.deserializeBlockPos;
import static com.luxof.lapisworks.Lapisworks.nbtListOf;
import static com.luxof.lapisworks.Lapisworks.serializeBlockPos;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.packet.s2c.play.TitleS2CPacket;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundEvents;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

// Lazy imports, cover me please!
import robotgiggle.hierophantics.HieroMindCastEnv;

public class Amalgamation {
    public final BlockPos origin;
    public final boolean forNoobs;
    public int notifLevel;
    public double range;
    public ArrayList<Iota> hex;
    public Amalgamation(
        BlockPos origin,
        boolean forNoobs,
        int notifLevel,
        double range,
        List<Iota> hex
    ) {
        this.origin = origin;
        this.forNoobs = forNoobs;
        this.notifLevel = notifLevel;
        this.range = range;
        this.hex = new ArrayList<>(hex);
    }
    public Amalgamation(NbtCompound nbt, ServerWorld world) {
        this(
            deserializeBlockPos(nbt.getCompound("origin")),
            nbt.getBoolean("forNoobs"),
            nbt.getInt("notifLevel"),
            nbt.getDouble("range"),
            nbt.getList("hex", NbtElement.COMPOUND_TYPE)
                .stream()
                .map(
                    ele -> IotaType.deserialize(
                        (NbtCompound)ele,
                        world
                    )
                )
                .toList()
        );
    }
    /** constructor for client shenanigans. gets everything but the hex. */
    public Amalgamation(NbtCompound nbt) {
        this(
            deserializeBlockPos(nbt.getCompound("origin")),
            nbt.getBoolean("forNoobs"),
            nbt.getInt("notifLevel"),
            nbt.getDouble("range"),
            List.of()
        );
    }

    public NbtCompound serialize() {
        NbtCompound nbt = new NbtCompound();
        nbt.put("origin", serializeBlockPos(origin));
        nbt.putBoolean("forNoobs", forNoobs);
        nbt.putInt("notifLevel", notifLevel);
        nbt.putDouble("range", range);
        nbt.put("hex", nbtListOf(hex.stream().map(IotaType::serialize).toList()));
        return nbt;
    }

    public double getErr() {
        return clamp(
            forNoobs
                ? (range - 32) * LapisConfig.hierophantics_interop.simple_amalgam_err_multiplier
                : range * LapisConfig.hierophantics_interop.complex_amalgam_err_multiplier,
            0.0,
            32.0
        );
    }

    public boolean willCast() {
        return forNoobs ? (1 / getErr()) < Math.random() : true;
    }

    public double getMaxRange() {
        return forNoobs
            ? LapisConfig.hierophantics_interop.max_simple_amalgam_range
            : LapisConfig.hierophantics_interop.max_complex_amalgam_range;
    }

    public void updateOrigin(ServerWorld world) {
        if (!(world.getBlockEntity(origin) instanceof ChariotMindEntity chariotMind))
            return;

        chariotMind.storedAmalgamationNbt = serialize();
        chariotMind.save();
    }

    public void cast(ServerPlayerEntity player, Vec3d onPos) {
        if (notifLevel >= 1)
            player.sendMessage(Text.translatable("notif.lapisworks.amalgamation.heyicasted"));
        if (notifLevel >= 2) {
            player.playSound(SoundEvents.BLOCK_AMETHYST_BLOCK_CHIME, 5f, 2f);
            player.playSound(HexSounds.CAST_HERMES, 5f, 2f);
        }
        if (notifLevel >= 3)
            player.networkHandler.sendPacket(
                new TitleS2CPacket(Text.translatable("notif.lapisworks.amalgamation.heyyyicasted"))
            );

        Vec3d castWithPos = onPos;
        if (!forNoobs) {
            double theta = Math.random() * 2*Math.PI;
            double err = Math.random() * getErr();
            double z = Math.random() * 2 - 1;
            castWithPos = onPos.add(
                Math.cos(theta)*err,
                Math.sin(theta)*err,
                z*err
            );
        }

        NbtCompound userData = new NbtCompound();
        userData.putBoolean("counterspell_cast", true);

        new CastingVM(
            new CastingImage().copy(
                List.of(new Vec3Iota(castWithPos)),
                0,
                List.of(),
                false,
                0,
                userData
            ),
            new HieroMindCastEnv(player, Hand.MAIN_HAND, notifLevel <= 0)
        ).queueExecuteAndWrapIotas(hex, player.getServerWorld());
    }

    public boolean equals(Object other) {
        return other != null
            && other instanceof Amalgamation otherAmalgamation
            && notifLevel == otherAmalgamation.notifLevel
            && range == otherAmalgamation.range
            && forNoobs == otherAmalgamation.forNoobs;
    }

    public static class AmalgamationIota extends Iota {
        public static IotaType<AmalgamationIota> TYPE = new IotaType<>() {

            @Override @Nullable
            public AmalgamationIota deserialize(NbtElement nbt, ServerWorld world) {
                return AmalgamationIota.deserialize((NbtCompound)nbt, world);
            }

            @Override
            public Text display(NbtElement nbt) {
                return AmalgamationIota.display((NbtCompound)nbt);
            }

            @Override
            public int color() {
                return 0x7000ff;
            }
            
        };

        public AmalgamationIota(Amalgamation payload) {
            super(TYPE, payload);
        }

        public Amalgamation getAmalgamation() {
            return (Amalgamation)this.payload;
        }

        @Override
        public boolean isTruthy() {
            return true;
        }

        @Override
        public boolean toleratesOther(Iota that) {
            return typesMatch(this, that)
                && that instanceof AmalgamationIota thatIota
                && getAmalgamation().equals(thatIota.getAmalgamation());
        }

        @Override
        public @NotNull NbtElement serialize() {
            return getAmalgamation().serialize();
        }

        public static Text display(NbtCompound nbt) {
            Amalgamation amalgam = new Amalgamation(nbt);
            int notifLevel = amalgam.notifLevel;
            boolean forNoobs = amalgam.forNoobs;
            double range = amalgam.range;

            return Text.translatable(
                "render.lapisworks.iota_descs.amalgamation.general",
                Text.translatable(
                    "render.lapisworks.iota_descs.amalgamation.notiflevel." +
                    String.valueOf(notifLevel)
                ),
                forNoobs
                    ? Text.translatable(
                        "render.lapisworks.iota_descs.amalgamation.lesser"
                    ).setStyle(Style.EMPTY.withColor(0x7029FF))
                    : Text.translatable(
                        "render.lapisworks.iota_descs.amalgamation.greater"
                    ).setStyle(Style.EMPTY.withColor(0x440088)),
                range
            ).setStyle(Style.EMPTY.withColor(0x7000FF));
        }

        @Nullable
        public static AmalgamationIota deserialize(NbtCompound nbt, ServerWorld world) {
            return new AmalgamationIota(new Amalgamation(nbt, world));
        }

        public static boolean originIsValid(NbtCompound nbt, ServerWorld world) {
            return world.getBlockEntity(
                deserializeBlockPos(
                    nbt.getCompound("origin")
                )
            ) instanceof ChariotMindEntity;
        }
    }
}
