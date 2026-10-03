package com.luxof.lapisworks.init;

import com.luxof.lapisworks.recipes.*;
import com.luxof.lapisworks.recipes.mold.*;

import static com.luxof.lapisworks.Lapisworks.id;

import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.recipe.RecipeType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModRecipes {
    public static final Identifier IMBUEMENT_RECIPE_ID = id("amel_imbuement");
    public static final Identifier MOLD_AMEL_RECIPE_ID = id("mold_amel");
    public static final Identifier BREWING_RECIPE_ID = id("brewery");
    public static final Identifier BREWING_ITEM_RECIPE_ID = id("brewery_item");
    public static final Identifier SMINDINFUSION_RECIPE_ID = id("simple_mind_infusion");
    public static final Identifier COLLAR_COMBINATION_RECIPE_ID = id("collar_combination");
    public static final Identifier SWAP_EB_RECIPE_ID = id("mold_amel_special_enchanted_book");

    /** warcrimes will not be told */
    public static void apologizeForWarcrimes() {
        registerSerializer(IMBUEMENT_RECIPE_ID, ImbuementRecSerializer.INSTANCE);
        registerType(IMBUEMENT_RECIPE_ID, ImbuementRec.Type.INSTANCE);

        registerSerializer(MOLD_AMEL_RECIPE_ID, MoldRecSerializer.INSTANCE);
        registerType(MOLD_AMEL_RECIPE_ID, IMoldRec.Type.INSTANCE);

        registerSerializer(BREWING_RECIPE_ID, BrewingRecSerializer.INSTANCE);
        registerType(BREWING_RECIPE_ID, BreweryRecipe.Type.INSTANCE);

        registerSerializer(BREWING_ITEM_RECIPE_ID, BrewItemRecSerializer.INSTANCE);
        //registerType(BREWING_ITEM_RECIPE_ID, BreweryRecipe.Type.INSTANCE);

        registerSerializer(SMINDINFUSION_RECIPE_ID, SMindInfusionRecSerializer.INSTANCE);
        registerType(SMINDINFUSION_RECIPE_ID, SMindInfusionRec.Type.INSTANCE);

        registerSerializer(COLLAR_COMBINATION_RECIPE_ID, CollarCombinationRecipeSerializer.INSTANCE);
        //registerType(COLLAR_COMBINATION_RECIPE_ID, CollarCombinationRecipe.Type.INSTANCE);

        registerSerializer(SWAP_EB_RECIPE_ID, EnchantedBookSwapRecSerializer.INSTANCE);
    }

    public static void registerSerializer(
        Identifier ID,
        RecipeSerializer<?> serializerInstance
    ) { Registry.register(Registries.RECIPE_SERIALIZER, ID, serializerInstance); }

    public static void registerType(
        Identifier ID,
        RecipeType<?> typeInstance
    ) { Registry.register(Registries.RECIPE_TYPE, ID, typeInstance); }
}
