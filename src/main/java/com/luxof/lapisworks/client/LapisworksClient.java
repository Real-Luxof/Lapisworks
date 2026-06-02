package com.luxof.lapisworks.client;

import com.luxof.lapisworks.Lapisworks;
import com.luxof.lapisworks.blocks.bers.*;
import com.luxof.lapisworks.blocks.bigchalk.BigChalkCenterRenderer;
import com.luxof.lapisworks.blocks.bigchalk.BigChalkPart;
import com.luxof.lapisworks.client.trinkets.*;
import com.luxof.lapisworks.collar.CollarItemRenderer;
import com.luxof.lapisworks.init.*;
import com.luxof.lapisworks.interop.hexcessible.LapiscessibleInterface;
import com.luxof.lapisworks.interop.hextended.items.AmelOrb;
import com.luxof.lapisworks.mixinsupport.AcceleratableEntity;
import com.luxof.lapisworks.mixinsupport.EnchSentInterface;

import static com.luxof.lapisworks.Lapisworks.FULL_HEXICAL_INTEROP;
import static com.luxof.lapisworks.Lapisworks.HEXAL_INTEROP;
import static com.luxof.lapisworks.Lapisworks.HEXCESSIBLE_INTEROP;
import static com.luxof.lapisworks.Lapisworks.HIEROPHANTICS_INTEROP;
import static com.luxof.lapisworks.Lapisworks.dim;
import static com.luxof.lapisworks.Lapisworks.err;
import static com.luxof.lapisworks.Lapisworks.id;
import static com.luxof.lapisworks.Lapisworks.log;
import static com.luxof.lapisworks.Lapisworks.nullConfigFlags;
import static com.luxof.lapisworks.LapisworksIDs.APPLY_PULL_FOR_TIME;
import static com.luxof.lapisworks.LapisworksIDs.DOWSE_RESULT;
import static com.luxof.lapisworks.LapisworksIDs.DOWSE_TS;
import static com.luxof.lapisworks.LapisworksIDs.GIB_DUST;
import static com.luxof.lapisworks.LapisworksIDs.ROBBIES_EXALT_PACKET;
import static com.luxof.lapisworks.LapisworksIDs.SEND_PWSHAPE_PATS;
import static com.luxof.lapisworks.LapisworksIDs.SEND_SENT;
import static com.luxof.lapisworks.LapisworksIDs.UNLOCK_SHIT_FOR_HEXCESSIBLE;
import static com.luxof.lapisworks.init.ModItems.AMEL_JAR;
import static com.luxof.lapisworks.init.ModItems.COLLAR;
import static com.luxof.lapisworks.init.ModItems.COLLAR_WITH_MODEL;
import static com.luxof.lapisworks.init.ModItems.FOCUS_NECKLACE;
import static com.luxof.lapisworks.init.ModItems.FOCUS_NECKLACE2;
import static com.luxof.lapisworks.init.ModItems.FOCUS_NECKLACE2_WORN;
import static com.luxof.lapisworks.init.ModItems.FOCUS_NECKLACE_WORN;
import static com.luxof.lapisworks.init.ModItems.IRON_SWORD;
import static com.luxof.lapisworks.init.ModItems.TOTEM_NECKLACE;
import static com.luxof.lapisworks.init.ModItems.TOTEM_NECKLACE_WORN;
import static com.luxof.lapisworks.init.ThemConfigFlags.chosenFlags;

import dev.emi.trinkets.api.client.TrinketRendererRegistry;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.BuiltinItemRendererRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;

import net.minecraft.block.Block;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.block.entity.BlockEntityRendererFactories;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import vazkii.patchouli.api.PatchouliAPI;

public class LapisworksClient implements ClientModInitializer {
    public Vec3d bufferSentinelPos = null;
    public Double bufferSentinelAmbit = null;
    public boolean playerHasJoined = false;

    public static void registerMPPs() {
        ModelPredicateProviderRegistry.register(
            IRON_SWORD,
            // first person doesn't work and i don't fuckign know why
            id("blocking"),
            (stack, world, entity, seed) -> {
                return entity != null
                    && entity.isUsingItem()
                    && entity.getActiveItem() == stack
                        ? 1.0F : 0.0F;
            }
        );
        if (Lapisworks.HEXTENDED_INTEROP) {
            ModelPredicateProviderRegistry.register(
                com.luxof.lapisworks.interop.hextended.Lapixtended.AMEL_ORB,
                id("amel_orb_is_filled"),
                (stack, world, entity, seed) -> {
                    AmelOrb orb = (AmelOrb)stack.getItem();
                    return orb.getPlaceInAmbit(stack) == null ? 0.0F : 1.0F;
                }
            );
        }
    }

    public static void overlayWorld(MatrixStack ms, float tickDelta) {
        PlayerEntity player = MinecraftClient.getInstance().player;
        if (player != null) {
            Vec3d sentinel = ((EnchSentInterface)player).getEnchantedSentinel();
            if (sentinel != null) { THEGRANDROTATER.renderEnchantedSentinel(sentinel, ms, tickDelta); }
        }
    }

    public static void initInterop() {
        if (FULL_HEXICAL_INTEROP) {
            com.luxof.lapisworks.interop.hexical.FullLapixicalClient.initTheFullLapixicalClient();
        }
        if (HEXAL_INTEROP) {
            com.luxof.lapisworks.interop.hexal.LapisalClient.beCoolOnTheClient();
        }
        if (HIEROPHANTICS_INTEROP) {
            com.luxof.lapisworks.interop.hierophantics.LapisphanticsClient.doMyShitTwin();
        }
    }

    @Override
    public void onInitializeClient() {
        // the eternal fucking grammar battle with this simple Markiplier ass log will drive me insane
        // thankful i won't have to edit this file anymore
        // ^^^^ what was that, chief?
        log("Hello everybody my name is LapisworksClient and today what we are going to do is: scrying lens tooltips, make blocks transparent, keybinds, networking, Model Predicate Providers, make blocks translucent, spin 4D hypercubes for the FUNNY, Block Entity Renderers (shudder), render trinkets, make particles, and client-side rendering!");
        log("Does NONE of that sound fun? Well, that's because it isn't. So let's get started, shall we?");

        LapisParticles.clientTicklesPaw();
        ModScreens.registerOnClient();
        Dowser.registerMyCuteness();

        initInterop();

        TrinketRendererRegistry.registerRenderer(AMEL_JAR, new JarTrinketRenderer());
        TrinketRendererRegistry.registerRenderer(
            FOCUS_NECKLACE,
            new NecklaceTrinketRenderer(new ItemStack(FOCUS_NECKLACE_WORN))
        );
        TrinketRendererRegistry.registerRenderer(
            FOCUS_NECKLACE2,
            new NecklaceTrinketRenderer(new ItemStack(FOCUS_NECKLACE2_WORN))
        );
        TrinketRendererRegistry.registerRenderer(
            TOTEM_NECKLACE,
            new NecklaceTrinketRenderer(new ItemStack(TOTEM_NECKLACE_WORN))
        );
        TrinketRendererRegistry.registerRenderer(
            COLLAR,
            new CollarTrinketRenderer()
        );

        BlockEntityRendererFactories.register(
            ModBlocks.ENCH_BREWER_ENTITY_TYPE,
            EnchBrewerRenderer::new
        );
        BlockEntityRendererFactories.register(
            ModBlocks.CHALK_ENTITY_TYPE,
            ChalkRenderer::new
        );
        BlockEntityRendererFactories.register(
            ModBlocks.CHALK_WITH_PATTERN_ENTITY_TYPE,
            ChalkWithPatternRenderer::new
        );
        BlockEntityRendererFactories.register(
            ModBlocks.BIG_CHALK_CENTER_ENTITY_TYPE,
            BigChalkCenterRenderer::new
        );

        ScryingOverlaysClient.addOverlays();

        WorldRenderEvents.AFTER_TRANSLUCENT.register((ctx) -> {
            overlayWorld(ctx.matrixStack(), ctx.tickDelta());
        });
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.MIND_BLOCK, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.TUNEABLE_AMETHYST, RenderLayer.getCutout());

        KeyEvents.staticInit();
        ClientTickEvents.END_CLIENT_TICK.register(KeyEvents::endClientTick);

        ClientPlayNetworking.registerGlobalReceiver(
            SEND_SENT,
            (client, handler, buf, responseSender) -> {
                boolean banishSentinel = buf.readBoolean();
                if (banishSentinel) {
                    if (!this.playerHasJoined) {
                        this.bufferSentinelPos = null;
                        this.bufferSentinelAmbit = null;
                    } else
                        ((EnchSentInterface)client.player).setEnchantedSentinel(null, null);
                    return;
                }
                Vec3d newPos = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
                Double newAmbit = buf.readDouble();
                if (!this.playerHasJoined) {
                    this.bufferSentinelPos = newPos;
                    this.bufferSentinelAmbit = newAmbit;
                } else
                    ((EnchSentInterface)client.player).setEnchantedSentinel(newPos, newAmbit);
            }
        );

        ClientPlayNetworking.registerGlobalReceiver(
            SEND_PWSHAPE_PATS,
            (client, handler, buf, responseSender) -> {
                NbtCompound nbt = buf.readNbt();
                for (String flag : chosenFlags.keySet()) {
                    chosenFlags.put(flag, nbt.getInt(flag));
                    PatchouliAPI.get().setConfigFlag(
                        flag + String.valueOf(nbt.getInt(flag)),
                        true
                    );
                }

                if (HEXCESSIBLE_INTEROP)
                    client.execute(LapiscessibleInterface::recalibratePWShapeUnlocksInHexcessible);
            }
        );

        ClientPlayNetworking.registerGlobalReceiver(
            ROBBIES_EXALT_PACKET,
            (client, handler, buf, responseSender) -> {
                ROBBIES_EXALT_VARIANT_CLIENT = client.player != null
                    && client.player.getUuidAsString().equals(
                        // Fel will have estrogen, it has to be this way
                        "b14b3e5c-7405-48ac-84dc-5c0925de44f2"
                    )
                    ? 0
                    : buf.readInt();
            }
        );

        ClientPlayNetworking.registerGlobalReceiver(
            DOWSE_TS,
            (client, handler, buf, responseSender) -> {
                PacketByteBuf sendBuf = PacketByteBufs.create();
                sendBuf.writeString(buf.readString());

                Block find = Registries.BLOCK.get(buf.readIdentifier());
                Pair<BlockPos, Double> result = Dowser.dowse(find);

                sendBuf.writeBoolean(result != null);
                if (result != null) {
                    sendBuf.writeBlockPos(result.getLeft());
                    sendBuf.writeDouble(result.getRight());
                }
                ClientPlayNetworking.send(DOWSE_RESULT, sendBuf);
            }
        );

        ClientPlayNetworking.registerGlobalReceiver(
            GIB_DUST,
            (client, handler, buf, responseSender) -> {
                BlockPos pos = buf.readBlockPos();
                Direction attachedTo = Direction.byName(buf.readString());

                // WHY DOES THIS KEEP FUCKING HAPPENING
                World world = client.player.getWorld();
                try {
                    BigChalkPart.spawnDust(world, pos, attachedTo);
                } catch (Exception e) {
                    err("Error while spawning dust on big chalk break:");
                    e.printStackTrace();
                }
            }
        );

        ClientPlayNetworking.registerGlobalReceiver(
            APPLY_PULL_FOR_TIME,
            (client, handler, buf, responseSender) -> {
                Vec3d pull = new Vec3d(buf.readDouble(), buf.readDouble(), buf.readDouble());
                client.player.addVelocity(pull);
                ((AcceleratableEntity)client.player).applyLingeringAccel(
                    pull,
                    buf.readInt() - 1
                );
            }
        );

        ClientPlayNetworking.registerGlobalReceiver(
            UNLOCK_SHIT_FOR_HEXCESSIBLE,
            (client, handler, buf, responseSender) -> {
                if (HEXCESSIBLE_INTEROP)
                    LapiscessibleInterface.unlockPWShapeInHexcessibleByAdvancement(buf.readIdentifier());
            }
        );

        ClientPlayConnectionEvents.JOIN.register((
            handler,
            sender,
            client
        ) -> {
            this.playerHasJoined = true;
            ((EnchSentInterface)client.player).setEnchantedSentinel(
                this.bufferSentinelPos,
                this.bufferSentinelAmbit
            );
        });

        ClientPlayConnectionEvents.DISCONNECT.register((
            handler,
            client
        ) -> {
            this.playerHasJoined = false;
            this.bufferSentinelPos = null;
            this.bufferSentinelAmbit = null;
            nullConfigFlags();
        });


        BuiltinItemRendererRegistry.INSTANCE.register(COLLAR, new CollarItemRenderer());
        ColorProviderRegistry.ITEM.register(
            (stack, tint) -> switch (tint) {
                case 0 -> COLLAR.getColor(stack);
                case 1 -> dim(COLLAR.getColor(stack));
                default -> 0x808080;
            },
            COLLAR_WITH_MODEL
        );
    }

    public static int ROBBIES_EXALT_VARIANT_CLIENT = 0;
}
