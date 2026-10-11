package com.luxof.lapisworks.client;

import static com.luxof.lapisworks.Lapisworks.err;
import static com.luxof.lapisworks.Lapisworks.log;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class Dowser {
    private static Map<Block, Pair<BlockPos, Double>> map = new HashMap<>();
    private static Map<Block, Pair<BlockPos, Double>> mapHOT = new HashMap<>();

    public static void addTarget(Block block) {
        map.put(block, null);
        mapHOT.put(block, null);
    }

    public static void removeTarget(Block block) {
        map.remove(block);
        mapHOT.remove(block);
    }

    public static List<Block> getTargets() {
        return map.keySet().stream().toList();
    }

    public static Pair<BlockPos, Double> dowse(Block block) {
        return map.get(block);
    }

    private static long timeBetweenDowsesMillis = 10_000L;
    private static void tickDowser() {
        // i vaguely remember some issue where the explanation was about a tree falling in a forest
        log("The dowser's thread has started!");
        MinecraftClient client = MinecraftClient.getInstance();

        while (true) {
            if (client.isPaused()) {
                try {
                    Thread.sleep(timeBetweenDowsesMillis);
                } catch (InterruptedException e) {
                    err("Why the fuck was the dowser's sleeping interrupted?");
                    break;
                }
            } else if (client.world == null || client.player == null)
                return;

            mapHOT.forEach((block, any) -> {
                map.put(block, any);
                mapHOT.put(block, null);
            });
            Vec3d eyePos;
            try {
                eyePos = client.player.getEyePos();
            } catch (NullPointerException e) {
                // multi-threading is fire
                continue;
            }
            Vec3d size = new Vec3d(100, 100, 100);
            Vec3d min = eyePos.subtract(size);

            for (double i = 0; i < size.x*size.y*size.z; i++) {
                double x = min.x + (i % size.x);
                double y = min.y + ((i / size.x) % size.y);
                double z = min.z + (i / (size.x*size.y));

                BlockPos pos = BlockPos.ofFloored(x, y, z);
                double dist = pos.getSquaredDistance(eyePos);
                BlockState state = client.world.getBlockState(pos);
                Block block = state.getBlock();

                if (!mapHOT.containsKey(block)) continue;
                Pair<BlockPos, Double> contending = mapHOT.get(block);
                if (contending != null && contending.getRight() < dist) continue;

                mapHOT.put(block, new Pair<BlockPos, Double>(pos, dist));
            }

            try {
                Thread.sleep(timeBetweenDowsesMillis);
            } catch (InterruptedException e) {
                err("Why the fuck was the dowser's sleeping interrupted?");
                break;
            }
        }
    }

    private static Thread dowser = null;
    public static void registerMyCuteness() {
        addTarget(Blocks.BUDDING_AMETHYST);

        ClientTickEvents.END_WORLD_TICK.register(client -> {
            if (dowser == null || !dowser.isAlive()) {
                dowser = new Thread(Dowser::tickDowser);
                dowser.start();
            }
        });
    }
}
