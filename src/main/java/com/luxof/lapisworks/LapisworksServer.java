package com.luxof.lapisworks;

import at.petrak.hexcasting.api.casting.eval.ResolvedPattern;
import at.petrak.hexcasting.api.casting.eval.vm.CastingVM;
import at.petrak.hexcasting.api.casting.math.HexPattern;
import at.petrak.hexcasting.common.msgs.MsgClearSpiralPatternsS2C;
import at.petrak.hexcasting.common.msgs.MsgOpenSpellGuiS2C;
import at.petrak.hexcasting.xplat.IXplatAbstractions;

import com.luxof.lapisworks.blocks.entities.ChalkWithPatternEntity;
import com.luxof.lapisworks.init.LapisConfig;
import com.luxof.lapisworks.init.PersistentStateRituals;
import com.luxof.lapisworks.mixinsupport.EnchSentInterface;

import static com.luxof.lapisworks.Lapisworks.pickUsingSeed;
import static com.luxof.lapisworks.Lapisworks.pickConfigFlags;
import static com.luxof.lapisworks.Lapisworks.nullConfigFlags;
import static com.luxof.lapisworks.LapisworksIDs.GEODE_DOWSER_REQUEST;
import static com.luxof.lapisworks.LapisworksIDs.ROBBIES_EXALT_PACKET;
import static com.luxof.lapisworks.LapisworksIDs.SEND_PWSHAPE_PATS;
import static com.luxof.lapisworks.LapisworksIDs.SEND_SENT;
import static com.luxof.lapisworks.LapisworksIDs.SET_PATTERNS_ON_CHALK;
import static com.luxof.lapisworks.init.ModItems.GEODE_DOWSER;
import static com.luxof.lapisworks.init.ThemConfigFlags.turnChosenIntoNbt;

import io.netty.buffer.Unpooled;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.function.BiConsumer;

import kotlin.Pair;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;

import net.minecraft.block.Block;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class LapisworksServer {
    public static void onJoinEnchSentStuff(
        ServerPlayNetworkHandler handler
    ) {
        ServerPlayerEntity player = handler.getPlayer();
        Vec3d sentPos = ((EnchSentInterface)player).getEnchantedSentinel();
        Double sentAmbit = ((EnchSentInterface)player).getEnchantedSentinelAmbit();
        if (sentPos == null) { return; }
        PacketByteBuf buf = PacketByteBufs.copy(Unpooled.buffer());
        buf.writeBoolean(false);
        buf.writeDouble(sentPos.x);
        buf.writeDouble(sentPos.y);
        buf.writeDouble(sentPos.z);
        buf.writeDouble(sentAmbit);
        ServerPlayNetworking.send(player, SEND_SENT, buf);
    }

    public static void onJoinPWShapeStuff(
        ServerPlayNetworkHandler handler
    ) {
        ServerPlayerEntity player = handler.getPlayer();
        PacketByteBuf patsBuf = PacketByteBufs.create();
        // hell naw i'm not dealing with the two extra args to writeMap() (i dunno wtf those are)
        patsBuf.writeNbt(turnChosenIntoNbt());
        ServerPlayNetworking.send(player, SEND_PWSHAPE_PATS, patsBuf);
    }

    public static void onJoinRobbiesStuff(
        ServerPlayNetworkHandler handler
    ) {
        ServerPlayerEntity player = handler.getPlayer();
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(ROBBIES_EXALT_VARIANT);
        ServerPlayNetworking.send(player, ROBBIES_EXALT_PACKET, buf);
    }

    public static void handleCastingGridPacket(
        MinecraftServer server,
        ServerPlayerEntity player,
        ServerPlayNetworkHandler handler,
        PacketByteBuf buf,
        PacketSender responseSender
    ) {
        boolean clearGrid = buf.readBoolean();
        if (clearGrid) {
            IXplatAbstractions.INSTANCE.clearCastingData(player);
            // i don't know why, it's just in the itemstaff code
            MsgClearSpiralPatternsS2C packet = new MsgClearSpiralPatternsS2C(player.getUuid());
            IXplatAbstractions.INSTANCE.sendPacketToPlayer(player, packet);
            IXplatAbstractions.INSTANCE.sendPacketTracking(player, packet);
        }

        CastingVM vm = IXplatAbstractions.INSTANCE.getStaffcastVM(player, Hand.MAIN_HAND);
        List<ResolvedPattern> patterns = IXplatAbstractions.INSTANCE.getPatternsSavedInUi(player);
        Pair<List<NbtCompound>, NbtCompound> descs = vm.generateDescs();

        IXplatAbstractions.INSTANCE.sendPacketToPlayer(
            player,
            new MsgOpenSpellGuiS2C(
                Hand.MAIN_HAND,
                patterns,
                descs.getFirst(),
                descs.getSecond(),
                0 // it says "todo fix" in the hex casting github 'round here, wonder why
            )
        );
    }

    /** public so anyone can easily fw it */
    public static Map<String, BiConsumer<ServerPlayerEntity, PacketByteBuf>> dowseResultTakers = new HashMap<>();
    private static int configRefreshCountdown = 100;

    public static void lockIn() {
        dowseResultTakers.put(GEODE_DOWSER_REQUEST, GEODE_DOWSER::serverHandleDowseResult);

        ServerTickEvents.START_SERVER_TICK.register((server) -> {
            configRefreshCountdown--;
            if (configRefreshCountdown < 0) {
                LapisConfig.renewCurrentConfig();
                configRefreshCountdown++;
            }
        });
        ServerPlayNetworking.registerGlobalReceiver(
            LapisworksIDs.OPEN_CASTING_GRID,
            (
                MinecraftServer server,
                ServerPlayerEntity player,
                ServerPlayNetworkHandler handler,
                PacketByteBuf buf,
                PacketSender responseSender
            ) -> handleCastingGridPacket(server, player, handler, buf, responseSender)
        );
        ServerPlayNetworking.registerGlobalReceiver(
            LapisworksIDs.DOWSE_RESULT,
            (
                server, player, handler, buf, responseSender
            ) -> {
                BiConsumer<ServerPlayerEntity, PacketByteBuf> dowseResultTaker = dowseResultTakers.get(buf.readString());
                if (dowseResultTaker == null) return;
                dowseResultTaker.accept(player, buf);
            }
        );
        ServerPlayNetworking.registerGlobalReceiver(
            SET_PATTERNS_ON_CHALK,
            (server, player, handler, buf, responseSender) -> {
                BlockPos position = buf.readBlockPos();

                int sentPatterns = buf.readInt();
                List<HexPattern> newPatterns = new ArrayList<>();
                for (int i = 0; i < sentPatterns; i++) {
                    newPatterns.add(HexPattern.fromNBT(buf.readNbt()));
                }

                ServerWorld sw = (ServerWorld)player.getWorld();

                server.execute(() -> {

                    ChalkWithPatternEntity chalk = (ChalkWithPatternEntity)sw.getBlockEntity(position);

                    chalk.pats = newPatterns;

                    chalk.markDirty();
                    sw.updateListeners(
                        position,
                        chalk.getCachedState(),
                        chalk.getCachedState(),
                        Block.NOTIFY_LISTENERS
                    );

                });
            }
        );

        ServerPlayConnectionEvents.JOIN.register((handler, sender, server) -> {
            onJoinEnchSentStuff(handler);
            onJoinPWShapeStuff(handler);
            onJoinRobbiesStuff(handler);
        });
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            pickConfigFlags(pickUsingSeed(server.getOverworld().getSeed()));
            ROBBIES_EXALT_VARIANT = new Random(server.getOverworld().getSeed()).nextInt(2);
        });
        ServerLifecycleEvents.SERVER_STOPPING.register(server -> nullConfigFlags());

        ServerTickEvents.END_SERVER_TICK.register(server -> {
            server.getWorlds().forEach(
                world -> PersistentStateRituals.getState(world).tick(world)
            );
        });
    }
    
    public static int ROBBIES_EXALT_VARIANT = 0;
}
