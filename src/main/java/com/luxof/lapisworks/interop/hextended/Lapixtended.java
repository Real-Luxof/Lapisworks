package com.luxof.lapisworks.interop.hextended;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironmentComponent;

import com.luxof.lapisworks.init.LapisConfig;
import com.luxof.lapisworks.interop.hextended.items.AmelOrb;
import com.luxof.lapisworks.interop.hextended.items.AmelWand;
import com.luxof.lapisworks.interop.hextended.items.PartAmelWand;
import com.luxof.lapisworks.items.PartiallyAmelStaff;
import com.luxof.lapisworks.mixinsupport.GetStacks;

import static com.luxof.lapisworks.init.ModItems.registerItem;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.Vec3d;

/* Lapixtended? Lapistended? Hextended Lapis?
 * maybe Lapix/Lapistended for Hextended Gear stuff added by Lapisworks
 * and Hextended Lapis for Lapisworks stuff added by Hextended Gear
 * that makes sense i think (at least in my head) */
public class Lapixtended {
    public static final Item PARTAMEL_ACACIA_WAND = registerItem("staves/incomplete/acacia_wand", new PartAmelWand());
    public static final Item PARTAMEL_BAMBOO_WAND = registerItem("staves/incomplete/bamboo_wand", new PartAmelWand());
    public static final Item PARTAMEL_BIRCH_WAND = registerItem("staves/incomplete/birch_wand", new PartAmelWand());
    public static final Item PARTAMEL_CHERRY_WAND = registerItem("staves/incomplete/cherry_wand", new PartAmelWand());
    public static final Item PARTAMEL_CRIMSON_WAND = registerItem("staves/incomplete/crimson_wand", new PartAmelWand());
    public static final Item PARTAMEL_DARK_OAK_WAND = registerItem("staves/incomplete/dark_oak_wand", new PartAmelWand());
    public static final Item PARTAMEL_EDIFIED_WAND = registerItem("staves/incomplete/edified_wand", new PartAmelWand());
    public static final Item PARTAMEL_JUNGLE_WAND = registerItem("staves/incomplete/jungle_wand", new PartAmelWand());
    public static final Item PARTAMEL_MANGROVE_WAND = registerItem("staves/incomplete/mangrove_wand", new PartAmelWand());
    public static final Item PARTAMEL_MINDSPLICE_WAND = registerItem("staves/incomplete/mindsplice_wand", new PartAmelWand());
    public static final Item PARTAMEL_OAK_WAND = registerItem("staves/incomplete/oak_wand", new PartAmelWand());
    public static final Item PARTAMEL_SPRUCE_WAND = registerItem("staves/incomplete/spruce_wand", new PartAmelWand());
    public static final Item PARTAMEL_WARPED_WAND = registerItem("staves/incomplete/warped_wand", new PartAmelWand());
    public static final Item PARTAMEL_MOSS = registerItem("staves/incomplete/moss", new PartiallyAmelStaff());
    public static final Item PARTAMEL_MOSS_WAND = registerItem("staves/incomplete/moss_wand", new PartAmelWand());
    public static final Item PARTAMEL_FLOWERED_MOSS = registerItem("staves/incomplete/flowered_moss", new PartiallyAmelStaff());
    public static final Item PARTAMEL_FLOWERED_MOSS_WAND = registerItem("staves/incomplete/flowered_moss_wand", new PartAmelWand());
    public static final Item PARTAMEL_OBSIDIAN = registerItem("staves/incomplete/obsidian", new PartiallyAmelStaff());
    public static final Item PARTAMEL_OBSIDIAN_WAND = registerItem("staves/incomplete/obsidian_wand", new PartAmelWand());
    public static final Item PARTAMEL_PRISMARINE = registerItem("staves/incomplete/prismarine", new PartiallyAmelStaff());
    public static final Item PARTAMEL_PRISMARINE_WAND = registerItem("staves/incomplete/prismarine_wand", new PartAmelWand());
    public static final Item PARTAMEL_DARK_PRISMARINE = registerItem("staves/incomplete/dark_prismarine", new PartiallyAmelStaff());
    public static final Item PARTAMEL_DARK_PRISMARINE_WAND = registerItem("staves/incomplete/dark_prismarine_wand", new PartAmelWand());
    public static final Item PARTAMEL_PURPUR = registerItem("staves/incomplete/purpur", new PartiallyAmelStaff());
    public static final Item PARTAMEL_PURPUR_WAND = registerItem("staves/incomplete/purpur_wand", new PartAmelWand());
    public static final Item PARTAMEL_WAND = registerItem("staves/incomplete/generic_wand", new PartAmelWand());
    public static final Item AMEL_WAND = registerItem("staves/amel_wand", new AmelWand());
    public static final Item AMEL_ORB = registerItem("amel_constructs/amel_orb", new AmelOrb());

    public static void initHextendedInterop() {
        CastingEnvironment.addCreateEventListener((env, userData) -> {
            env.addExtension(new CastingEnvironmentComponent.IsVecInRange() {

                @Override
                public boolean onIsVecInRange(Vec3d vec, boolean current) {
                    if (current) return current;
                    for (ItemStack stack : ((GetStacks)env).getHeldItemStacks()) {
                        if (!(stack.getItem() instanceof AmelOrb orb)) continue;

                        Vec3d ambitOrigin = orb.getPlaceInAmbit(stack);
                        if (ambitOrigin == null) continue;

                        if (
                            vec.squaredDistanceTo(ambitOrigin)
                            <= Math.pow(LapisConfig.hextended_interop.amel_orb_ambit, 2)
                        )
                            return true;
                    }
                    return current;
                }

                public Key<?> key = new Key<CastingEnvironmentComponent>() {};
                @Override public Key<?> getKey() { return key; }

            });
        });
    }
}
