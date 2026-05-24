package com.luxof.lapisworks.client;

import at.petrak.hexcasting.api.casting.math.HexPattern;
import at.petrak.hexcasting.api.misc.MediaConstants;
import at.petrak.hexcasting.common.lib.HexItems;

import static at.petrak.hexcasting.api.client.ScryingLensOverlayRegistry.addDisplayer;

import com.luxof.lapisworks.blocks.entities.ChalkWithPatternEntity;
import com.luxof.lapisworks.blocks.entities.MediaCondenserEntity;
import com.luxof.lapisworks.blocks.entities.MindEntity;
import com.luxof.lapisworks.blocks.entities.RitusEntity;
import com.luxof.lapisworks.blocks.entities.SimpleImpetusEntity;
import com.luxof.lapisworks.blocks.entities.TuneableAmethystEntity;
import com.luxof.lapisworks.init.ModBlocks;
import com.luxof.lapisworks.init.ModItems;

import static com.luxof.lapisworks.Lapisworks.clamp;
import static com.luxof.lapisworks.Lapisworks.prettifyDouble;
import static com.luxof.lapisworks.Lapisworks.prettifyFloat;
import static com.luxof.lapisworks.LapisworksIDs.SCRYING_MIND_END;
import static com.luxof.lapisworks.LapisworksIDs.SCRYING_MIND_START;

import com.mojang.datafixers.util.Pair;

import java.util.List;

import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public class ScryingOverlaysClient {
    private static String inlineify(HexPattern pattern) {
        return "HexPattern["
                    + pattern.getStartDir().toString()
                    + ", "
                    + pattern.anglesSignature() +
                "]";
    }
    private static String inlineify(List<HexPattern> patterns) {
        String ret = "";
        for (HexPattern pattern : patterns) { ret += inlineify(pattern); }
        return ret;
    }
    private static Pair<ItemStack, Text> displayMedia(long amount) {
        return new Pair<>(
            new ItemStack(HexItems.AMETHYST_DUST),
            Text.literal(String.valueOf((double)amount / (double)MediaConstants.DUST_UNIT))
        );
    }
    private static Pair<ItemStack, Text> displayMediaWithCap(long amount, long cap) {
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
    private static Pair<ItemStack, Text> displayTunedFrequency(Text iota) {
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

    public static void addOverlays() {

        // we all thank hexxy for adding a simple addDisplayer() instead of requiring mixin in unison
        addDisplayer(
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

        addDisplayer(
            ModBlocks.SIMPLE_IMPETUS,
            (lines, state, pos, observer, world, direction) -> {
                SimpleImpetusEntity bE = (SimpleImpetusEntity)world.getBlockEntity(pos);
                HexPattern tunedPattern = bE.getTunedPattern();

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

        addDisplayer(
            ModBlocks.MEDIA_CONDENSER,
            (lines, state, pos, observer, world, direction) -> {
                MediaCondenserEntity blockEntity = (MediaCondenserEntity)world.getBlockEntity(pos);

                lines.add(
                    displayMediaWithCap(blockEntity.media, blockEntity.mediaCap)
                );
            }
        );

        addDisplayer(
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

        addDisplayer(
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

        addDisplayer(
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
    }
}
