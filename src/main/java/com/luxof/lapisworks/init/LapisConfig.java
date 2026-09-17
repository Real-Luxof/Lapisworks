package com.luxof.lapisworks.init;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import com.luxof.lapisworks.LapisMathEngine;

import static com.luxof.lapisworks.Lapisworks.LOGGER;
import static com.luxof.lapisworks.Lapisworks.computeIfRight;
import static com.luxof.lapisworks.Lapisworks.err;
import static com.luxof.lapisworks.Lapisworks.log;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.fabricmc.loader.api.FabricLoader;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class LapisConfig {
    public static File configFile = FabricLoader.getInstance()
        .getConfigDir()
        .resolve("lapisworks.json")
        .toFile();

    private static record Settings(Class<?> clazz, List<ConfigOption<?>> opts) {}
    private static class ConfigOption<T extends Object> {
        public String name;
        public final T defaultVal;
        public T currentVal;
        public ConfigOption(String name, T defaultVal, T currentVal) {
            this.name = name;
            this.defaultVal = defaultVal;
            this.currentVal = currentVal;
        }
        private void becomeDefault(JsonObject obj) {
            JsonPrimitive prim;
            if (defaultVal instanceof Double v) prim = new JsonPrimitive(v);
            else if (defaultVal instanceof Integer v) prim = new JsonPrimitive(v);
            else if (defaultVal instanceof Boolean v) prim = new JsonPrimitive(v);
            else if (defaultVal instanceof String v) prim = new JsonPrimitive(v);
            else prim = new JsonPrimitive("ASS");
            obj.add(name, prim);
            currentVal = defaultVal;
        }
        @SuppressWarnings("unchecked")
        public void deserializeSelfFromAndCorrect(JsonObject obj, boolean canIYell, String mainSettingsName) {
            if (!obj.has(name)) {
                if (canIYell) err("%s does not have %s!", mainSettingsName, name);
                becomeDefault(obj);
                return;
            }
            JsonElement ele = obj.get(name);
            try {
                if (defaultVal instanceof Double) currentVal = (T)(Object)ele.getAsDouble();
                else if (defaultVal instanceof Integer) currentVal = (T)(Object)ele.getAsInt();
                else if (defaultVal instanceof Boolean) currentVal = (T)(Object)ele.getAsBoolean();
                else if (defaultVal instanceof String) currentVal = (T)(Object)ele.getAsString();
            } catch (Exception e) {
                if (canIYell) err("%s does not have %s of the required type!", mainSettingsName, name);
                becomeDefault(obj);
                currentVal = defaultVal;
            }
        }
    }

    private static List<Settings> registered = new ArrayList<>();
    private static <T extends Object> void registerClass(
        Class<T> clazz
    ) {
        List<ConfigOption<?>> opts = new ArrayList<>();
        for (Field field : clazz.getFields()) {
            String fieldName = field.getName();
            Object value;
            try {
                value = field.get(null);
            } catch (IllegalAccessException e) {
                LOGGER.error("WAHHHHHHHHHHHHHHHHHHHHHHHHHHH", e);
                break;
            }
            if (value instanceof Double dub) opts.add(new ConfigOption<>(fieldName, dub, dub));
            else if (value instanceof Integer in) opts.add(new ConfigOption<>(fieldName, in, in));
            else if (value instanceof Boolean bool) opts.add(new ConfigOption<>(fieldName, bool, bool));
            else if (value instanceof String str) opts.add(new ConfigOption<>(fieldName, str, str));
        }
        registered.add(new Settings(clazz, opts));
    }
    public static class onetime_ritual {
        public static double tuneable_amethyst_ambit_multiplier = 1.0;
        public static double player_ambit_multiplier = 0.5;
        public static int powered_trail_length = 1;
    }
    public static class multiuse_ritual {
        public static double tuneable_amethyst_ambit_multiplier = 1.0;
        public static int powered_trail_length = 5;
    }
    public static class grand_ritual {
        public static boolean do_animation = true;
        public static double cost_multiplier = 0.5;
    }
    public static class hierophantics_interop {
        public static int max_fused_amalgamations = 4;
        public static double max_simple_amalgam_range = 48.0;
        public static double max_complex_amalgam_range = 96.0;
        public static double simple_amalgam_err_multiplier = 0.125;
        public static double complex_amalgam_err_multiplier = 0.25;
        public static double max_err = 32.0;
    }
    public static class spells {
        public static boolean allow_reclaim_amethyst_but_imbue_lapis_takes_items_instead_of_raw_media = true;
    }
    static {
        registerClass(onetime_ritual.class);
        registerClass(multiuse_ritual.class);
        registerClass(grand_ritual.class);
        registerClass(hierophantics_interop.class);
    }
    public static class overenchant_limit_in_imbue_amel {
        public static String docs = "";
        public static String behaviour_when_not_present = "";
        public static HashMap<String, Integer> limits = new HashMap<>();
        public static JsonObject defaultJson = JsonParser.parseString("""
      {
        "docs": "Operators are +-*/^. No implicit multiplication. x = default maximum for the enchantment (and the only other variables are π or e). Results will be rounded if non-integer, and clamped to 0 if negative. This is a mathematical expression (so something like 3*tan(max(x - 5, 5)/3.14) works). You have the trig functions and their inverse (arc-) and hyperbolic (-h) counterparts, as well as floor, round, ceil, sqrt, abs, signum, degrees, radians, random(lower bound, upper bound), root(radicand, radical) and finally log (either log(num) for the natural logarithm, or log(num, base). Also you have bit-manipulation (NOT(n), AND(a, b), OR(a, b), XOR(a, b), etcetera).",
        "behaviour_when_not_present": "3*x",
        "minecraft:channeling": 1,
        "minecraft:mending": 1,
        "minecraft:infinity": 1,
        "minecraft:binding_curse": 1,
        "minecraft:vanishing_curse": 1,
        "minecraft:flame": 1,
        "minecraft:multishot": 1,
        "minecraft:piercing": 10,
        "minecraft:silk_touch": 1
        }""").getAsJsonObject();

        public static void deserialize(JsonObject obj) {
            docs = obj.get("docs").getAsString();
            behaviour_when_not_present = obj.get("behaviour_when_not_present").getAsString();
            limits.clear();
            for (String key : obj.keySet()) {
                if (key.equals("docs") || key.equals("behaviour_when_not_present"))
                    continue;
                limits.put(key, obj.get(key).getAsInt());
            }
        }
        public static int getOverenchantLimitFor(Enchantment enchantment) {
            return getOverenchantLimitFor(Registries.ENCHANTMENT.getId(enchantment).toString());
        }
        public static int getOverenchantLimitFor(String enchId) {
            int maxLevel = Registries.ENCHANTMENT.get(new Identifier(enchId)).getMaxLevel();

            return limits.containsKey(enchId)
                ? limits.get(enchId)
                : (int)Math.round(computeIfRight(
                    LapisMathEngine.tryMath(
                        behaviour_when_not_present,
                        Map.of("x", maxLevel)
                    ),
                    msg -> {
                        LOGGER.error("ERROR WHILE COMPUTING behaviour_when_not_present MATH. DEFAULTING TO 3*x.", msg);
                        return 3.0 * (double)maxLevel;
                    }
                ));
        }
    }

    private static final String defaultConfig = """
    {
      "onetime_ritual": {
        "tuneable_amethyst_ambit_multiplier": 1.0,
        "player_ambit_multiplier": 0.5,
        "trail_of_powered_chalk_length": 1
      },

      "multiuse_ritual": {
        "tuneable_amethyst_ambit_multiplier": 1.0,
        "trail_of_powered_chalk_length": 5
      },

      "grand_ritual": {
        "do_animation": true,
        "cost_multiplier": 0.5
      },

      "hierophantics_interop": {
        "max_fused_amalgamations": 3,
        "max_simple_amalgam_range": 48.0,
        "max_complex_amalgam_range": 96.0,
        "simple_amalgam_err_multiplier": 0.125,
        "complex_amalgam_err_multiplier": 0.25,
        "max_err": 32.0
      },

      "spells": {
        "allow_reclaim_amethyst_but_imbue_lapis_takes_items_instead_of_raw_media": true
      },

    }
    """;
    private static final JsonObject defaultConfigObject = JsonParser.parseString(defaultConfig)
        .getAsJsonObject();

    public static void renewCurrentConfig() { renewCurrentConfig(false); }

    // no dumbass it's january
    public static void renewCurrentConfig(boolean canIYell) {
        JsonObject obj;
        try {
            if (!configFile.exists()) {
                Files.writeString(configFile.toPath(), "{}", StandardOpenOption.CREATE);
            }
            obj = JsonParser.parseReader(new FileReader(configFile)).getAsJsonObject();
        } catch (Exception e1) {
            if (canIYell) {
                err("Apparently, the Lapisworks config file is such horseshit it doesn't parse as valid JSON.");
                err("Trying to fix that right now...");
            }
            obj = defaultConfigObject;

            try {
                Files.writeString(configFile.toPath(), "{}", StandardOpenOption.CREATE);
            } catch (IOException e2) {
                if (!canIYell) return;
                err("Yeah no, I can't fix your Lapisworks config file.");
                err("I've defaulted your config options in-game, though.");
                err("Your first error:");
                e1.printStackTrace();
                err("And your second error:");
                e2.printStackTrace();
                err("Toodles!");
            }

            return;
        }

        if (canIYell) log("Loading config!");
        for (Settings settings : registered) {
            String thisObjName = settings.clazz().getName();
            JsonObject thisObj;
            try {
                thisObj = obj.getAsJsonObject(thisObjName);
                if (thisObj == null && canIYell)
                    err("%s does not exist in config as an object!", thisObjName);
            } catch (ClassCastException e) {
                if (canIYell)
                    err("%s does not exist in config as an object!", thisObjName);
                thisObj = null;
            }

            for (ConfigOption<?> co : settings.opts) {
                co.deserializeSelfFromAndCorrect(thisObj, canIYell, thisObjName);
                try {
                    settings.clazz().getField(co.name).set(null, co.currentVal);
                } catch (Exception e) {
                    LOGGER.error("WAHHHHHHHHHHH!!!!!!!", e);
                }
            }
        }

        try {
            Files.writeString(
                configFile.toPath(),
                new GsonBuilder()
                    .setPrettyPrinting()
                    .create()
                    .toJson(obj),
                StandardOpenOption.CREATE
            );
        } catch (IOException e) {
            err("Tried to correct bad Lapisworks config file, failed!");
            e.printStackTrace();
        }
    }
}
