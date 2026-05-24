package com.luxof.lapisworks.init;

import at.petrak.hexcasting.api.casting.math.HexDir;
import at.petrak.hexcasting.api.casting.castables.Action;
import at.petrak.hexcasting.api.casting.castables.SpellAction;
import at.petrak.hexcasting.api.casting.math.HexPattern;
import at.petrak.hexcasting.api.casting.ActionRegistryEntry;
import at.petrak.hexcasting.common.lib.HexRegistries;
import at.petrak.hexcasting.common.lib.hex.HexActions;

import static at.petrak.hexcasting.api.misc.MediaConstants.CRYSTAL_UNIT;

import com.luxof.lapisworks.actions.*;
import com.luxof.lapisworks.actions.great.GenericEnchant;
import com.luxof.lapisworks.actions.interact.*;
import com.luxof.lapisworks.actions.misc.*;
import com.luxof.lapisworks.actions.misc.thoth.*;
import com.luxof.lapisworks.actions.ritual.*;
import com.luxof.lapisworks.actions.scry.*;
import com.luxof.lapisworks.actions.wizard.*;
import com.luxof.lapisworks.actions.wizard.enchsent.*;

import static com.luxof.lapisworks.Lapisworks.id;
import static com.luxof.lapisworks.init.ThemConfigFlags.registerPWShapePattern;

import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;

public class Patterns {
    // used for Simple Minds.
    // it is this way for a mixin.
    public static RegistryKey<ActionRegistryEntry> ARCHON_OF_MEANINGLESSNESS = null;

    public static void init() {
        register(
            "archon_of_meaninglessness",
            // stupid signature that no one will use.
            // i wonder if hexcessible makes this available to all lmao.
            "eedqaqddadwddwaeaeadaeqaddwedwqdadedaqqwwqqewwwaeaedqqwwwqwawaedwqqdwwaqweeeqeeewawdwqe",
            HexDir.WEST,
            new DoNothing()
        );
        ARCHON_OF_MEANINGLESSNESS = RegistryKey.of(
            HexRegistries.ACTION,
            id("archon_of_meaninglessness")
        );

        MoarAttr MoarHealthAction = new MoarAttr(
            EntityAttributes.GENERIC_MAX_HEALTH,
            2.0,
            0.0,
            1.0,
            2,
            false
        );
        MoarAttr MoarAttackAction = new MoarAttr(
            EntityAttributes.GENERIC_ATTACK_DAMAGE,
            4.0, // the barbarian and the monk fistfighting a demon because magic is for nerds
            0.0,
            1.0,
            5,
            false
        );
        // i'd add armor but narratively it makes no sense, you can already enchant your skin
        // so what else is there to enchant to make yourself stronger?
        // man i wish i could, i had such a cool ass fucking pattern too
        // north east wwwaqeeeqawww
        // yeah just gonna use CheckAttr for that instead
        MoarAttr MoarSpeedAction = new MoarAttr(
            EntityAttributes.GENERIC_MOVEMENT_SPEED,
            3.0,
            0.0,
            10.0,
            5,
            false
        );
        register("imbue_lap", "qadwawdaqqeae", HexDir.NORTH_EAST, new ImbueLap());
        register("reclaim_ameth", "awwqqqwwa", HexDir.SOUTH_EAST, new ReclaimAmeth());
        register("swap_amel", "wqwawwqwaqeq", HexDir.EAST, new SwapAmel());
        
        register("moar_health", "wqadaqwwawwwqwwawdwawwqwwwwa", HexDir.NORTH_EAST, MoarHealthAction);
        register("moar_attack", "qaqwweaeaqwww", HexDir.EAST, MoarAttackAction);
        register("moar_speed", "ddqwaqeqa", HexDir.WEST, MoarSpeedAction);
        register("gib_reach", "edewwqewdwwewdaw", HexDir.SOUTH_WEST, new MoarReachYouBitch());
        register("check_attr", "wwwaqeeqawww", HexDir.NORTH_EAST, new CheckAttr());

        GenericEnchant fireyFists       = new GenericEnchant(1, 48, CRYSTAL_UNIT * 10, "lapisenchantments.lapisworks.fireyfists");
        GenericEnchant lightningBending = new GenericEnchant(3, 64, CRYSTAL_UNIT * 20, "lapisenchantments.lapisworks.lightningbending");
        GenericEnchant fallDmgRes       = new GenericEnchant(3, 20, CRYSTAL_UNIT * 5, "lapisenchantments.lapisworks.falldmgres");
        GenericEnchant longBreath       = new GenericEnchant(2, 10, CRYSTAL_UNIT, "lapisenchantments.lapisworks.longbreath");
        GenericEnchant fireResist       = new GenericEnchant(1, 48, CRYSTAL_UNIT * 10, "lapisenchantments.lapisworks.fireresist");
        register("fireyfists", "wwewdawdewqewedadad", HexDir.EAST, fireyFists);
        register("lightningbending", "wewdawdewqewdqqeedqe", HexDir.EAST, lightningBending);
        register("falldmgres", "qqwwqqqadwewdeq", HexDir.SOUTH_WEST, fallDmgRes);
        register("longbreath", "wewdwewewewewdwew", HexDir.SOUTH_EAST, longBreath);
        register("fireresist", "wwqwqwadwawdawqwaeqqaqqe", HexDir.EAST, fireResist);
        register("checkenchant", "aqawwqqwqqw", HexDir.SOUTH_EAST, new CheckEnchant());

        register("imbue_amel", "wqwwawwqwwaqwewaawewa", HexDir.NORTH_EAST, new ImbueAmel());
        register("disimbue_amel", "wwaqqddqqawwewwaqqddqqaww", HexDir.SOUTH_WEST, new DisenchantImbued());
        register("conjure_color", "qqaa", HexDir.NORTH_EAST, new ConjureColor());
        register("erase_color", "qqqaq", HexDir.NORTH_EAST, new EraseColor());
        register("spherical_dstl", "wqwqwqwqwqwaeaqaaeaqaa", HexDir.NORTH_WEST, new SphereDst(false));
        register("spherical_dstl_filled", "wqwqwqwqwqwaewawqwawaww", HexDir.NORTH_WEST, new SphereDst(true));
        register("cubic_exalt", "wqwawqwqqwqwq", HexDir.NORTH_WEST, new CubeExalt());
        register("empty_prfn", "qwawqwaqwweqqqq", HexDir.NORTH_WEST, new EmptyPrfn());
        register("empty_dstl", "dwewdwedwwwadwewdwedw", HexDir.NORTH_WEST, new EmptyDstl());
        register("visible_dstl", "edeewadwewdwe", HexDir.SOUTH_EAST, new VisibleDstl());
        register("read_spechand", "aqqqqa", HexDir.EAST, new ReadFromHand());
        register("readable_spechand", "qqqqadww", HexDir.NORTH_WEST, new ReadableInHand());
        register("write_spechand", "deeeed", HexDir.EAST, new WriteToHand());
        register("writable_spechand", "eeeedaww", HexDir.SOUTH_WEST, new WritableInHand());
        register("equiv_block", "qqqqqeqeeeee", HexDir.NORTH_WEST, new EquivBlock());
        register("equal_block", "qwawqwadadwewdwe", HexDir.NORTH_WEST, new EqualBlock());
        register("the_cooler_halt", "wawqwdwewew", HexDir.SOUTH_WEST, new OpTheCoolerHalt());
        register("for_n_in_range", "aqadadad", HexDir.NORTH_WEST, new OpForNInRange(false));
        register("execute_many_times", "dedadada", HexDir.SOUTH_WEST, new OpForNInRange(true));
        register("hadamard", "awddwde", HexDir.WEST, new Hadamard());
        register("thewhattropy", "eqqqq", HexDir.NORTH_WEST, new TheCoolerEntropy());
        register("min", "waqqwqddqwqqaw", HexDir.WEST, new MinMax(true));
        register("max", "wdeeweaaeweedw", HexDir.WEST, new MinMax(false));
        register("pull", "awwqqqwwaqww", HexDir.SOUTH_WEST, new Pull());
        register("robbie_exalt", "qaeaqaweaqa", HexDir.NORTH_WEST, new EstrogenExalt());
        register("get_datum", "ewe", HexDir.NORTH_EAST, new GetDatum());
        register("set_datum", "qwq", HexDir.SOUTH_EAST, new SetDatum());
        register("get_thoth_hex", "eeaee", HexDir.NORTH_EAST, new GetThothHex());
        register("set_thoth_hex", "qqdqq", HexDir.SOUTH_EAST, new SetThothHex());
        register("is_conjureable", "qqaaeqqqq", HexDir.NORTH_EAST, new IsConjureable());

        register("thought_sieve", "qadaadadqaqdadqaq", HexDir.WEST, new HexResearchYoink());
        register("absorb_mind", "aawqqwqqqaede", HexDir.WEST, new MindLiquefaction());
        register("check_mind", "aawqqwqqq", HexDir.WEST, new CognitionPrfn());
        register("teach_song", "aawwawqwwdd", HexDir.WEST, new TeachSong());
        register("song_purification", "aqawwd", HexDir.WEST, new SongPrfn());
        register("teach_simp", "deaqqeawqqwwqqq", HexDir.SOUTH_EAST, new TeachSImp());
        register("ask_simp", "eeeqwdeaqqeawqqwwqqq", HexDir.NORTH_EAST, new AskSImp());

        register("read_necklace", "waaqqqqqe", HexDir.NORTH_WEST, new ReadNecklace());
        register("write_necklace", "wadeeeeeq", HexDir.NORTH_WEST, new WriteNecklace());
        register("readable_necklace", "wwaaqqqqqew", HexDir.NORTH_WEST, new ReadableNecklace());
        register("writeable_necklace", "wwadeeeeeqw", HexDir.NORTH_WEST, new WriteableNecklace());

        register("deposit", "qaqqdwdwd", HexDir.NORTH_EAST, new Deposit());
        register("withdraw", "qaqwwdwdw", HexDir.NORTH_EAST, new Withdraw());
        register("get_mdia", "wddwqwddweqeee", HexDir.NORTH_WEST, new GetThatMedia());
        // the term Phianglement comes from Miyu. it's like quantum entanglement but for phials
        register("phiangle", "wadqaqdawewadqaqdaw", HexDir.NORTH_EAST, new LinkCondensers());
        register("dephiangle", "wwqaqwwdwawwedeww", HexDir.SOUTH_WEST, new UnlinkCondensers());
        register("get_linkable_links", "qaqdaweqaqewaqwawaw", HexDir.NORTH_EAST, new GetLinkableLinks());

        // Rituals
        register("get_amethyst_tuning", "edewwqdqawwwaqewddwaqqwdqqwqqwqq", HexDir.NORTH_WEST, new GetAmethystTuning());
        // Am I cruel?
        register("tune_amethyst", "ewwwwqdqwawwwwwwawqeadwwdwdwwdaawwqqwwewqwqqwqwwwddwqwe", HexDir.SOUTH_EAST, new TuneAmethyst());

        // One-time
        register("stop_be_me", "adaqqqwqqq", HexDir.EAST, new DisableCaster());
        register("be_me", "qqqqqwqqq", HexDir.EAST, new EnableCaster());
        register("get_ritual_tuning", "wawqwawawweaqaaweaqaaweqqqqqa", HexDir.EAST, new GetRitualTuning());
        register("tune_ritual", "wdwewdwdwwqdeddwqdeddwqeeeeewdqdqdqdqdqde", HexDir.NORTH_WEST, new TuneRitual());

        // hol up, let him cook
        // i said LET HIM COOK
        // LET. HIM. COOK :fire:
        SpellAction createEnchSent = new CreateEnchSent();
        register("create_enchsent0", "aqaeawdwwwdwqwdwwwdweqqaqwedeewqded", HexDir.NORTH_WEST, createEnchSent);
        register("create_enchsent1", "aqaeawdwwwdwqwdwwwdwewweaqa", HexDir.NORTH_WEST, createEnchSent);
        register("create_enchsent2", "wdwewdwwwdwwwdwqwdwwwdw", HexDir.NORTH_EAST, createEnchSent);
        register("create_enchsent3", "aqaeawdwwwdwqwdwwwdweqaawddeweaqa", HexDir.NORTH_WEST, createEnchSent);
        register("create_enchsent4", "wdwwwdwqwdwwwdweqaawdde", HexDir.NORTH_WEST, createEnchSent);
        register("create_enchsent5", "wdwwwdwqwdwwwdwweeeee", HexDir.NORTH_WEST, createEnchSent);
        registerPWShapePattern("lapisworks:create_enchsent");
        register("banish_my_enchsent", "wdwewdwdwqwawwwawewawwwaw", HexDir.NORTH_EAST, new BanishMySent());
        register("banish_other_enchsent", "eeeeedwqwawwwawewawwwaw", HexDir.NORTH_EAST, new BanishOtherSent());
        register("find_my_enchsent", "wdwwwdwqwdwwwdweeqqqq", HexDir.NORTH_WEST, new LocateMySent());
        
        register("flay_artmind0", "ewewedwqwqqwqwqaeqe", HexDir.SOUTH_EAST, new FlayArtMind());
        register("flay_artmind1", "ewewedwqwaqaedqdeaqdewewe", HexDir.SOUTH_EAST, new FlayArtMind());
        register("flay_artmind2", "ewewedwqwqqwqwqaeqeqaqeqeqa", HexDir.SOUTH_EAST, new FlayArtMind());
        register("flay_artmind3", "ewewedwqwaqaeweeeweaqdedaeade", HexDir.SOUTH_EAST, new FlayArtMind());
        register("flay_artmind4", "ewewedwqwaqeqwqadqwqwqdaqeqwqwq", HexDir.SOUTH_EAST, new FlayArtMind());
        registerPWShapePattern("lapisworks:flay_artmind");

        register("hastenature0", "awawwwdwdww", HexDir.NORTH_EAST, new Hastenature());
        register("hastenature1", "qwdedwqqwdedweawawwwdwdww", HexDir.WEST, new Hastenature());
        register("hastenature2", "wawqwaweawawwwdwdww", HexDir.WEST, new Hastenature());
        register("hastenature3", "awwdedwwawwdedweawawwwdwdww", HexDir.NORTH_WEST, new Hastenature());
        register("hastenature4", "aaqawawweddedwdww", HexDir.NORTH_WEST, new Hastenature());
        register("hastenature5", "aeaeaeaeaeadawawwwdwdww", HexDir.NORTH_WEST, new Hastenature());
        registerPWShapePattern("lapisworks:hastenature");

        register("quenched_indigo0", "qqqadwawdaqqwqawaawaa", HexDir.SOUTH_EAST, new QuenchedIndigo());
        register("quenched_indigo1", "wqaqwadaqqwwqqadwa", HexDir.SOUTH_WEST, new QuenchedIndigo());
        register("quenched_indigo2", "deadawdwadaedqdeaeewddwaddqeaedewwwww", HexDir.NORTH_WEST, new QuenchedIndigo());
        register("quenched_indigo3", "deeedeqdawdwadeeqdqedwwwqedqdedawwqwwqwwqwwqwwqww", HexDir.NORTH_WEST, new QuenchedIndigo());
        register("quenched_indigo4", "qawwqeaeqwwaqqqaqeqdaadqwedeeawdwa", HexDir.NORTH_WEST, new QuenchedIndigo());
        register("quenched_indigo5", "qawwqwwqwwaqaedewwwawweqaqdawdwa", HexDir.NORTH_WEST, new QuenchedIndigo());
        registerPWShapePattern("lapisworks:quenched_indigo");
    }

    public static ActionRegistryEntry register(
        String name,
        String signature,
        HexDir startDir,
        Action action
    ) {
        return Registry.register(HexActions.REGISTRY, id(name), new ActionRegistryEntry(HexPattern.fromAngles(signature, startDir), action));
    }
}
