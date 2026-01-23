package com.luxof.lapisworks.client;

import at.petrak.hexcasting.api.casting.math.HexPattern;
import at.petrak.hexcasting.api.client.ScryingLensOverlayRegistry;
import at.petrak.hexcasting.api.misc.MediaConstants;
import at.petrak.hexcasting.common.lib.HexItems;

import com.luxof.lapisworks.Lapisworks;
import com.luxof.lapisworks.blocks.bers.*;
import com.luxof.lapisworks.blocks.entities.*;
import com.luxof.lapisworks.init.*;
import com.luxof.lapisworks.interop.hextended.items.AmelOrb;
import com.luxof.lapisworks.mixinsupport.BlockDowser;
import com.luxof.lapisworks.mixinsupport.EnchSentInterface;

import static com.luxof.lapisworks.Lapisworks.LOGGER;
import static com.luxof.lapisworks.Lapisworks.clamp;
import static com.luxof.lapisworks.Lapisworks.id;
import static com.luxof.lapisworks.Lapisworks.nullConfigFlags;
import static com.luxof.lapisworks.Lapisworks.prettifyFloat;
import static com.luxof.lapisworks.Lapisworks.prettifyDouble;
import static com.luxof.lapisworks.LapisworksIDs.DOWSE_RESULT;
import static com.luxof.lapisworks.LapisworksIDs.DOWSE_TS;
import static com.luxof.lapisworks.LapisworksIDs.SCRYING_MIND_END;
import static com.luxof.lapisworks.LapisworksIDs.SCRYING_MIND_START;
import static com.luxof.lapisworks.LapisworksIDs.SEND_PWSHAPE_PATS;
import static com.luxof.lapisworks.LapisworksIDs.SEND_SENT;
import static com.luxof.lapisworks.init.ModItems.FOCUS_NECKLACE;
import static com.luxof.lapisworks.init.ModItems.FOCUS_NECKLACE2;
import static com.luxof.lapisworks.init.ModItems.IRON_SWORD;
import static com.luxof.lapisworks.init.ThemConfigFlags.chosenFlags;

import java.util.List;

import com.mojang.datafixers.util.Pair;

import dev.architectury.registry.client.rendering.BlockEntityRendererRegistry;

import dev.emi.trinkets.api.client.TrinketRendererRegistry;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.client.rendering.v1.WorldRenderEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;

import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

import vazkii.patchouli.api.PatchouliAPI;

public class LapisworksClient implements ClientModInitializer {
    public Vec3d bufferSentinelPos = null;
    public Double bufferSentinelAmbit = null;
    public boolean playerHasJoined = false;

    public static void registerMPPs() {
        ModelPredicateProviderRegistry.register(
            IRON_SWORD,
            id("blocking"), // first person doesn't work but WHATEVER
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
        if (Lapisworks.FULL_HEXICAL_INTEROP) {
            com.luxof.lapisworks.interop.hexical.FullLapixicalClient.initTheFullLapixicalClient();
        }
        if (Lapisworks.HEXAL_INTEROP) {
            com.luxof.lapisworks.interop.hexal.LapisalClient.beCoolOnTheClient();
        }
    }

    private String inlineify(HexPattern pattern) {
        return "HexPattern["
                    + pattern.getStartDir().toString()
                    + ", "
                    + pattern.anglesSignature() +
                "]";
    }
    private String inlineify(List<HexPattern> patterns) {
        String ret = "";
        for (HexPattern pattern : patterns) { ret += inlineify(pattern); }
        return ret;
    }
    private Pair<ItemStack, Text> displayMedia(long amount) {
        return new Pair<>(
            new ItemStack(HexItems.AMETHYST_DUST),
            Text.literal(String.valueOf((double)amount / (double)MediaConstants.DUST_UNIT))
        );
    }
    private Pair<ItemStack, Text> displayMediaWithCap(long amount, long cap) {
        return new Pair<>(
            new ItemStack(HexItems.AMETHYST_DUST),
            Text.translatable(

                "render.lapisworks.scryinglens.dust_with_cap",
                String.valueOf((double)amount / (double)cap * 100.0),
                (double)amount / (double)MediaConstants.DUST_UNIT,
                (double)cap / (double)MediaConstants.DUST_UNIT

            ).formatted(Formatting.LIGHT_PURPLE)
        );
    }
    private Pair<ItemStack, Text> displayTunedFrequency(Text iota) {
        return new Pair<ItemStack, Text>(
            new ItemStack(ModItems.TUNEABLE_AMETHYST),
            Text.translatable(
                "render.lapisworks.scryinglens.tuneable.tuned"
            ).append(
                iota != null ? iota : Text.translatable(
                    "render.lapisworks.scryinglens.tuneable.nothing"
                )
            )
        );
    }
    @Override
    public void onInitializeClient() {
        // the eternal fucking grammar battle with this simple Markiplier ass log will drive me insane
        // thankful i won't have to edit this file anymore
        // ^^^^ what was that, chief?
        LOGGER.info("Hello everybody my name is LapisworksClient and today what we are going to do is: scrying lens tooltips, make blocks transparent, keybinds, networking, Model Predicate Providers, make blocks translucent, spin 4D hypercubes for the FUNNY, Block Entity Renderers (shudder), render trinkets, make particles, and client-side rendering!");
        LOGGER.info("Does NONE of that sound fun? Well, that's because it isn't. So let's get started, shall we?");

        LapisParticles.clientTicklesPaw();
        ModScreens.registerOnClient();

        initInterop();

        TrinketRendererRegistry.registerRenderer(ModItems.AMEL_JAR, new JarTrinketRenderer());
        TrinketRendererRegistry.registerRenderer(FOCUS_NECKLACE, new NecklaceTrinketRenderer());
        TrinketRendererRegistry.registerRenderer(FOCUS_NECKLACE2, new NecklaceTrinketRenderer());

        BlockEntityRendererRegistry.register(
            ModBlocks.ENCH_BREWER_ENTITY_TYPE,
            EnchBrewerRenderer::new
        );
        BlockEntityRendererRegistry.register(
            ModBlocks.CHALK_ENTITY_TYPE,
            ChalkRenderer::new
        );
        BlockEntityRendererRegistry.register(
            ModBlocks.CHALK_WITH_PATTERN_ENTITY_TYPE,
            ChalkWithPatternRenderer::new
        );

        // we all thank hexxy for adding a simple addDisplayer() instead of requiring mixin in unison
        ScryingLensOverlayRegistry.addDisplayer(
            ModBlocks.MIND_BLOCK,
            (lines, state, pos, observer, world, direction) -> {
                MindEntity blockEntity = (MindEntity)world.getBlockEntity(pos);

                lines.add(
                    new Pair<ItemStack, Text>(
                        new ItemStack(ModItems.MIND),
                        SCRYING_MIND_START.copy().append(
                            Text.literal(
                                prettifyFloat(clamp(blockEntity.mindCompletion, 0f, 100f))
                            )
                        ).append(
                            SCRYING_MIND_END
                        ).formatted(
                            Formatting.LIGHT_PURPLE
                        )
                    )
                );
            }
        );
        ScryingLensOverlayRegistry.addDisplayer(
            ModBlocks.SIMPLE_IMPETUS,
            (lines, state, pos, observer, world, direction) -> {
                SimpleImpetusEntity bE = (SimpleImpetusEntity)world.getBlockEntity(pos);
                HexPattern tunedPattern = bE.getTunedPattern();

                lines.add(
                    displayMedia(bE.getMedia())
                );
                lines.add(
                    new Pair<ItemStack, Text>(
                        new ItemStack(ModItems.SIMPLE_IMPETUS),
                        tunedPattern != null ? Text.translatable(
                                "render.lapisworks.scryinglens.simp.listening",
                                inlineify(tunedPattern)
                            ) :
                            Text.translatable("render.lapisworks.scryinglens.simp.not_listening")
                    )
                );
            }
        );
        ScryingLensOverlayRegistry.addDisplayer(
            ModBlocks.MEDIA_CONDENSER,
            (lines, state, pos, observer, world, direction) -> {
                MediaCondenserEntity blockEntity = (MediaCondenserEntity)world.getBlockEntity(pos);

                lines.add(
                    displayMediaWithCap(blockEntity.media, blockEntity.mediaCap)
                );
            }
        );
        ScryingLensOverlayRegistry.addDisplayer(
            ModBlocks.CHALK_WITH_PATTERN,
            (lines, state, pos, observer, world, direction) -> {
                ChalkWithPatternEntity chalk = (ChalkWithPatternEntity)world.getBlockEntity(pos);

                lines.add(
                    new Pair<ItemStack, Text>(
                        new ItemStack(ModItems.CHALK),
                        chalk.pats.size() > 0 ? Text.literal(inlineify(chalk.pats)) :
                        Text.translatable("render.lapisworks.scryinglens.chalk.no_patterns")
                    )
                );
            }
        );
        ScryingLensOverlayRegistry.addDisplayer(
            ModBlocks.TUNEABLE_AMETHYST,
            (lines, state, pos, observer, world, direction) -> {
                TuneableAmethystEntity tuneable = (TuneableAmethystEntity)world.getBlockEntity(pos);

                lines.add(
                    displayMediaWithCap(tuneable.media, tuneable.mediaCap)
                );
                lines.add(
                    new Pair<>(
                        new ItemStack(HexItems.AMETHYST_DUST),
                        Text.translatable(
                            "render.lapisworks.scryinglens.tuneable_amethyst.ambit",
                            prettifyDouble(tuneable.getAmbit()),
                            tuneable.ambitCap,
                            tuneable.minAmbit,
                            prettifyDouble(Math.sqrt(tuneable.getMediaInDust()))
                        )
                    )
                );
                Text iota = tuneable.getTunedFrequencyDisplay();
                lines.add(displayTunedFrequency(iota));
            }
        );
        ScryingLensOverlayRegistry.addDisplayer(
            ModBlocks.RITUS,
            (lines, state, pos, observer, world, direction) -> {
                RitusEntity ritus = (RitusEntity)world.getBlockEntity(pos);

                lines.add(
                    displayMedia(ritus.media)
                );

                var display = ritus.getDisplay();
                if (display != null) {
                    lines.add(
                        new Pair<ItemStack, Text>(
                            display.getLeft(),
                            display.getRight()
                        )
                    );
                }

                lines.add(displayTunedFrequency(ritus.getTunedFrequencyDisplay()));
            }
        );

        WorldRenderEvents.AFTER_TRANSLUCENT.register((ctx) -> {
            overlayWorld(ctx.matrixStack(), ctx.tickDelta());
        });
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.MIND_BLOCK, RenderLayer.getTranslucent());
        BlockRenderLayerMap.INSTANCE.putBlock(ModBlocks.TUNEABLE_AMETHYST, RenderLayer.getCutout());

        KeyEvents.staticInit();
        ClientTickEvents.END_CLIENT_TICK.register(KeyEvents::endClientTick);

        ClientPlayNetworking.registerGlobalReceiver(
            SEND_SENT,
            (
                client,
                handler,
                buf,
                responseSender
            ) -> {
                boolean banishSentinel = buf.readBoolean();
                if (banishSentinel) {
                    if (!this.playerHasJoined) {
                        this.bufferSentinelPos = null;
                        this.bufferSentinelAmbit = null;
                    } else {
                        ((EnchSentInterface)client.player).setEnchantedSentinel(null, null);
                    }
                    return;
                }
                Vec3d newPos = new Vec3d(buf.readVector3f());
                Double newAmbit = buf.readDouble();
                if (!this.playerHasJoined) {
                    this.bufferSentinelPos = newPos;
                    this.bufferSentinelAmbit = newAmbit;
                } else {
                    ((EnchSentInterface)client.player).setEnchantedSentinel(newPos, newAmbit);
                }
            }
        );
        ClientPlayNetworking.registerGlobalReceiver(
            SEND_PWSHAPE_PATS,
            (client, handler, buf, responseSender) -> {
                NbtCompound nbt = buf.readNbt();
                for (String flag : chosenFlags.keySet()) {
                    chosenFlags.put(flag, nbt.getInt(flag));
                    // vv unused but may allow for neat stuff in the future
                    PatchouliAPI.get().setConfigFlag(
                        flag + String.valueOf(nbt.getInt(flag)),
                        true
                    );
                }
            }
        );

        ClientPlayNetworking.registerGlobalReceiver(
            DOWSE_TS,
            (client, handler, buf, responseSender) -> {
                PacketByteBuf sendBuf = PacketByteBufs.create();
                sendBuf.writeString(buf.readString());

                Block find = Registries.BLOCK.get(buf.readIdentifier());
                Pair<BlockPos, Double> result = ((BlockDowser)client.player).dowse(find);

                sendBuf.writeBoolean(result != null);
                if (result != null) {
                    sendBuf.writeBlockPos(result.getFirst());
                    sendBuf.writeDouble(result.getSecond());
                }
                ClientPlayNetworking.send(DOWSE_RESULT, sendBuf);
            }
        );

        ClientPlayConnectionEvents.JOIN.register((
            handler,
            sender,
            client
        ) -> {
            ((BlockDowser)client.player).addTarget(Blocks.BUDDING_AMETHYST);
            this.playerHasJoined = true;
            ((EnchSentInterface)client.player).setEnchantedSentinel(
                this.bufferSentinelPos,
                this.bufferSentinelAmbit
            );
        });

        // i could just use the server_stopping event and send a packet then but i already wrote this so
        // whatever
        ClientPlayConnectionEvents.DISCONNECT.register((
            handler,
            client
        ) -> {
            this.playerHasJoined = false;
            this.bufferSentinelPos = null;
            this.bufferSentinelAmbit = null;
            nullConfigFlags();
            if (client.player != null) {
                // i don't know, okay? just in case or something
                ((EnchSentInterface)client.player).setEnchantedSentinel(null, null);
            }
        });
    }
}
