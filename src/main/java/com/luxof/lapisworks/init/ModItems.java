package com.luxof.lapisworks.init;

import at.petrak.hexcasting.common.items.ItemStaff;

import com.luxof.lapisworks.items.*;
import com.luxof.lapisworks.items.shit.AmelSword;

import static com.luxof.lapisworks.Lapisworks.id;
import static com.luxof.lapisworks.LapisworksIDs.LAPISMAGICSHITGROUPTEXT;
import static com.luxof.lapisworks.LapisworksIDs.LAPIS_MAGIC_SHIT_GROUP;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;

import net.minecraft.item.BlockItem;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Pair;

public class ModItems {
    private static FabricItemSettings fullStack = new FabricItemSettings().maxCount(64);
    private static FabricItemSettings unstackable = new FabricItemSettings().maxCount(1);

    public static final Item AMEL_ITEM = new Item(fullStack);
    public static final Item AMEL2_ITEM = new Item(fullStack);
    public static final Item AMEL3_ITEM = new Item(fullStack);
    public static final Item AMEL4_ITEM = new Item(fullStack);
    public static final AmelStaff AMEL_STAFF = new AmelStaff(unstackable);
    public static final PartiallyAmelStaff PARTAMEL_STAFF = new PartiallyAmelStaff();
    public static final PartiallyAmelStaff PARTAMEL_ACACIA_STAFF = new PartiallyAmelStaff();
    public static final PartiallyAmelStaff PARTAMEL_BAMBOO_STAFF = new PartiallyAmelStaff();
    public static final PartiallyAmelStaff PARTAMEL_BIRCH_STAFF = new PartiallyAmelStaff();
    public static final PartiallyAmelStaff PARTAMEL_CHERRY_STAFF = new PartiallyAmelStaff();
    public static final PartiallyAmelStaff PARTAMEL_CRIMSON_STAFF = new PartiallyAmelStaff();
    public static final PartiallyAmelStaff PARTAMEL_DARK_OAK_STAFF = new PartiallyAmelStaff();
    public static final PartiallyAmelStaff PARTAMEL_EDIFIED_STAFF = new PartiallyAmelStaff();
    public static final PartiallyAmelStaff PARTAMEL_JUNGLE_STAFF = new PartiallyAmelStaff();
    public static final PartiallyAmelStaff PARTAMEL_MANGROVE_STAFF = new PartiallyAmelStaff();
    public static final PartiallyAmelStaff PARTAMEL_MINDSPLICE_STAFF = new PartiallyAmelStaff();
    public static final PartiallyAmelStaff PARTAMEL_OAK_STAFF = new PartiallyAmelStaff();
    public static final PartiallyAmelStaff PARTAMEL_SPRUCE_STAFF = new PartiallyAmelStaff();
    public static final PartiallyAmelStaff PARTAMEL_WARPED_STAFF = new PartiallyAmelStaff();
    public static final ItemStaff AMEL_RING = new ItemStaff(unstackable);
    public static final ItemStaff AMEL_RING2 = new ItemStaff(unstackable);
    public static final AmelSword DIAMOND_SWORD = new DiamondSword();
    public static final AmelSword IRON_SWORD = new IronSword();
    public static final AmelSword GOLD_SWORD = new GoldSword();
    public static final Item WIZARD_DIARIES = new WizardDiaries(unstackable);
    public static final BlockItem MIND = new BlockItem(ModBlocks.MIND_BLOCK, fullStack);
    public static final BlockItem LIVE_JUKEBOX = new BlockItem(ModBlocks.LIVE_JUKEBOX_BLOCK, fullStack);
    public static final JumpSlateItem JUMP_SLATE_AM1 = new JumpSlateItem(ModBlocks.JUMP_SLATE_AM1, fullStack);
    public static final JumpSlateItem JUMP_SLATE_AM2 = new JumpSlateItem(ModBlocks.JUMP_SLATE_AM2, fullStack);
    public static final JumpSlateItem JUMP_SLATE_AMETH = new JumpSlateItem(ModBlocks.JUMP_SLATE_AMETH, fullStack);
    public static final JumpSlateItem JUMP_SLATE_LAPIS = new JumpSlateItem(ModBlocks.JUMP_SLATE_LAPIS, fullStack);
    public static final JumpSlateItem REBOUND_SLATE_1 = new JumpSlateItem(ModBlocks.REBOUND_SLATE_1, fullStack);
    public static final JumpSlateItem REBOUND_SLATE_2 = new JumpSlateItem(ModBlocks.REBOUND_SLATE_2, fullStack);
    public static final AmelJar AMEL_JAR = new AmelJar(unstackable, 256, false);
    public static final AmelJar ENERGY_CONTAINER = new AmelJar(unstackable, 1024, true);
    public static final GeodeDowser GEODE_DOWSER = new GeodeDowser(unstackable);
    public static final BlockItem SIMPLE_IMPETUS = new BlockItem(ModBlocks.SIMPLE_IMPETUS, fullStack);
    public static final BlockItem ENCH_BREWER = new BlockItem(ModBlocks.ENCH_BREWER, fullStack);
    public static final BlockItem MEDIA_CONDENSER = new MediaCondenserItem(unstackable);
    public static final BlockItem UNCRAFTED_CONDENSER = new BlockItem(ModBlocks.UNCRAFTED_CONDENSER, fullStack);
    public static final BlockItem CHALK = new ChalkItem();
    public static final BlockItem TUNEABLE_AMETHYST = new BlockItem(ModBlocks.TUNEABLE_AMETHYST, fullStack);
    public static final Stamp STAMP = new Stamp();
    public static final BlockItem RITUS = new BlockItem(ModBlocks.RITUS, fullStack);
    public static final FocusNecklace FOCUS_NECKLACE = new FocusNecklace(unstackable);
    public static final FocusNecklace FOCUS_NECKLACE2 = new FocusNecklace(unstackable);
    public static final TotemNecklace TOTEM_NECKLACE = new TotemNecklace();
    public static final Collar COLLAR = new Collar();
    public static final EnchantedBookItem SUPER_ENCHANTED_BOOK = new EnchantedBookItem(unstackable);

    public static final Item COLLAR_WITH_MODEL = new Item(unstackable);
    public static final Item COLLAR_BELL = new Item(unstackable);

    public static final Item TOTEM_NECKLACE_FLOATY_DISPLAY = new Item(unstackable);
    public static final Item TOTEM_NECKLACE_WORN = new Item(unstackable);
    public static final FocusNecklace FOCUS_NECKLACE_WORN = new FocusNecklace(unstackable);
    public static final FocusNecklace FOCUS_NECKLACE2_WORN = new FocusNecklace(unstackable);

    private static <ANY extends Object> List<Pair<String, Item>> mapOf(
        @SuppressWarnings("unchecked") ANY... stuff
    ) {
        // no err checking.
        List<Pair<String, Item>> map = new ArrayList<>();

        boolean item = false;
        String id = "";
        for (ANY thing : stuff) {
            if (!item) {
                id = (String)thing;
                item = true;
            } else {
                map.add(new Pair<>(id, (Item)thing));
                item = false;
            }
        }

        return map;
    }
    private static List<Pair<String, Item>> ITEMS = mapOf(
        "amel", AMEL_ITEM,
        "amel2", AMEL2_ITEM,
        "amel3", AMEL3_ITEM,
        "amel4", AMEL4_ITEM,
        "staves/amel_staff", AMEL_STAFF,
        "staves/incomplete/generic", PARTAMEL_STAFF,
        "staves/incomplete/acacia", PARTAMEL_ACACIA_STAFF,
        "staves/incomplete/bamboo", PARTAMEL_BAMBOO_STAFF,
        "staves/incomplete/birch", PARTAMEL_BIRCH_STAFF,
        "staves/incomplete/cherry", PARTAMEL_CHERRY_STAFF,
        "staves/incomplete/crimson", PARTAMEL_CRIMSON_STAFF,
        "staves/incomplete/dark_oak", PARTAMEL_DARK_OAK_STAFF,
        "staves/incomplete/edified", PARTAMEL_EDIFIED_STAFF,
        "staves/incomplete/jungle", PARTAMEL_JUNGLE_STAFF,
        "staves/incomplete/mangrove", PARTAMEL_MANGROVE_STAFF,
        "staves/incomplete/mindsplice", PARTAMEL_MINDSPLICE_STAFF,
        "staves/incomplete/oak", PARTAMEL_OAK_STAFF,
        "staves/incomplete/spruce", PARTAMEL_SPRUCE_STAFF,
        "staves/incomplete/warped", PARTAMEL_WARPED_STAFF,
        "staves/amel_ring", AMEL_RING,
        "staves/amel_ring2", AMEL_RING2,
        "amel_constructs/diamond_sword", DIAMOND_SWORD,
        "amel_constructs/iron_sword", IRON_SWORD,
        "amel_constructs/gold_sword", GOLD_SWORD,
        "wizard_diaries", WIZARD_DIARIES,
        "mind", MIND,
        "amel_constructs/live_jukebox", LIVE_JUKEBOX,
        "amel_constructs/jumpslate/am1", JUMP_SLATE_AM1,
        "amel_constructs/jumpslate/am2", JUMP_SLATE_AM2,
        "amel_constructs/jumpslate/ameth", JUMP_SLATE_AMETH,
        "amel_constructs/jumpslate/lapis", JUMP_SLATE_LAPIS,
        "amel_constructs/jumpslate/rebound_1", REBOUND_SLATE_1,
        "amel_constructs/jumpslate/rebound_2", REBOUND_SLATE_2,
        "amel_jar", AMEL_JAR,
        "energy_container", ENERGY_CONTAINER,
        "super_enchbook", SUPER_ENCHANTED_BOOK,
        "amel_constructs/geode_dowser", GEODE_DOWSER,
        "amel_constructs/simple_impetus", SIMPLE_IMPETUS,
        "amel_constructs/enchbrewer", ENCH_BREWER,
        "media_condenser_unit", MEDIA_CONDENSER,
        "uncrafted_condenser", UNCRAFTED_CONDENSER,
        "chalk", CHALK,
        "tuneable_amethyst", TUNEABLE_AMETHYST,
        "amethyst_stamp", STAMP,
        "ritus", RITUS,

        "collar", COLLAR,
        "amel_constructs/focus_necklace/1", FOCUS_NECKLACE,
        "amel_constructs/focus_necklace/2", FOCUS_NECKLACE2,
        "totem_necklace", TOTEM_NECKLACE,

        // register these misc ones at the end to organize
        "collar_with_model", COLLAR_WITH_MODEL,
        "collar_bell", COLLAR_BELL,
        "totem_necklace_floaty_display", TOTEM_NECKLACE_FLOATY_DISPLAY,
        "totem_necklace_worn", TOTEM_NECKLACE_WORN,
        "amel_constructs/focus_necklace/1_worn", FOCUS_NECKLACE_WORN,
        "amel_constructs/focus_necklace/2_worn", FOCUS_NECKLACE2_WORN
    );

    public static ItemGroup LapisMagicShitGroup;

    public static void init_shit() {
        LapisMagicShitGroup = FabricItemGroup.builder()
            .icon(() -> new ItemStack(AMEL_ITEM))
            .displayName(LAPISMAGICSHITGROUPTEXT)
            .entries((context, entries) -> {
                ITEMS.forEach(pair -> entries.add(pair.getRight()));
            })
            .build();
        Registry.register(
            Registries.ITEM_GROUP,
            LAPIS_MAGIC_SHIT_GROUP,
            LapisMagicShitGroup
        );
        ITEMS.forEach(ModItems::register);
    }

    private static void register(Pair<String, Item> item) {
        Registry.register(Registries.ITEM, id(item.getLeft()), item.getRight());
    }

    public static <ITEM extends Item> ITEM registerItem(String name, ITEM item) {
        ITEMS.add(new Pair<>(name, item));
        return item;
    }
}
