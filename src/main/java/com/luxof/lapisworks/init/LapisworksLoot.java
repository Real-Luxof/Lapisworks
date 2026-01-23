package com.luxof.lapisworks.init;

import java.util.List;

import net.fabricmc.fabric.api.loot.v2.LootTableEvents;

import net.minecraft.loot.LootPool;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.util.Identifier;

import static net.minecraft.loot.provider.number.ConstantLootNumberProvider.create;

public class LapisworksLoot {
    public static final Identifier STRONGHOLD_LIBRARY = idMC("chests/stronghold_library");
    public static final List<Identifier> lowChanceOnes = List.of(
        idMC("chests/buried_treasure"),
        idMC("chests/desert_pyramid"),
        //idMC("chest/shipwreck_treasure"), would pirates still exist? would they even dare to do this?
        idMC("chests/igloo_chest")
    );
    public static final List<Identifier> midChanceOnes = List.of(
        idMC("chests/stronghold_corridor"),
        idMC("chests/stronghold_crossing")
    );

    public static void gibLootexclamationmark() {
		LootTableEvents.MODIFY.register(
			(resourceManager, lootManager, id, builder, lootTableSource) -> {

                LootPool.Builder lapisLoot = LootPool.builder()
                    .with(ItemEntry.builder(ModItems.WIZARD_DIARIES).build());
                
                float rolls = -1.0f;
                if (lowChanceOnes.indexOf(id) != -1) { rolls = 0.1f; }
                else if (midChanceOnes.indexOf(id) != -1) { rolls = 0.5f; }
                else if (id.equals(STRONGHOLD_LIBRARY)) { rolls = 1.5f; }

                if (rolls > -0.5f) { builder.pool(lapisLoot.rolls(create(rolls)).build()); }

            }
		);
    }
    // shorthand
    public static Identifier idMC(String any) { return new Identifier(any); }
}
