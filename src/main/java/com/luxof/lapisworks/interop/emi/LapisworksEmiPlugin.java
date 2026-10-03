package com.luxof.lapisworks.interop.emi;

import com.luxof.lapisworks.init.ModItems;
import com.luxof.lapisworks.interop.emi.recipes.EmiImbuementRecipe;
import com.luxof.lapisworks.interop.emi.recipes.EmiMoldRecipe;
import com.luxof.lapisworks.interop.emi.recipes.EmiSMindInfusionRecipe;
import com.luxof.lapisworks.recipes.ImbuementRec;
import com.luxof.lapisworks.recipes.mold.IMoldRec;
import com.luxof.lapisworks.recipes.mold.MoldRec;
import com.luxof.lapisworks.recipes.SMindInfusionRec;

import static com.luxof.lapisworks.init.ModItems.PARTAMEL_STAFF;
import static com.luxof.lapisworks.Lapisworks.id;
import static com.luxof.lapisworks.LapisworksIDs.AMEL_TAG;

import dev.emi.emi.api.EmiPlugin;
import dev.emi.emi.api.EmiRegistry;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;

import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeManager;

public class LapisworksEmiPlugin implements EmiPlugin {
    // i love that they let me do all this weird shit that i'd add myself because convenience
    // ingredients with counts? and they're useable for workstations?
    // only renderables required for icons?
    // :face_holding_back_tears:
    public static final EmiRecipeCategory IMBUEMENT_CATEGORY = new EmiRecipeCategory(
        id("imbuement"),
        EmiStack.of(PARTAMEL_STAFF),
        EmiStack.of(PARTAMEL_STAFF)
    );

    private static final EmiTexture _MOLD_ICON = new EmiTexture(
        id("textures/gui/emi/amel_molding.png"),
        0, 0, 16, 16,

        16, 16, 16, 16
    );
    public static final EmiRecipeCategory MOLD_CATEGORY = new EmiRecipeCategory(
        id("mold"),
        _MOLD_ICON,
        _MOLD_ICON
    );

    private static final EmiTexture _SMINDINFUSION_ICON = new EmiTexture(
        id("textures/gui/emi/brain.png"),
        0, 0, 16, 16,

        16, 16, 16, 16
    );
    public static final EmiRecipeCategory SMINDINFUSION_CATEGORY = new EmiRecipeCategory(
        id("simple_mind_infusion"),
        _SMINDINFUSION_ICON,
        _SMINDINFUSION_ICON
    );

    @Override
    public void register(EmiRegistry registry) {
        registry.addCategory(IMBUEMENT_CATEGORY);
        registry.addWorkstation(IMBUEMENT_CATEGORY, EmiIngredient.of(AMEL_TAG));
        registry.addCategory(MOLD_CATEGORY);
        registry.addWorkstation(MOLD_CATEGORY, EmiIngredient.of(AMEL_TAG));
        registry.addCategory(SMINDINFUSION_CATEGORY);
        registry.addWorkstation(
            SMINDINFUSION_CATEGORY,
            EmiIngredient.of(Ingredient.ofItems(ModItems.MIND))
        );

        RecipeManager recipeManager = registry.getRecipeManager();
        for (ImbuementRec imbuementRec : recipeManager.listAllOfType(ImbuementRec.Type.INSTANCE)) {
            registry.addRecipe(new EmiImbuementRecipe(imbuementRec));
        }
        for (IMoldRec moldRec : recipeManager.listAllOfType(IMoldRec.Type.INSTANCE)) {
            if (!(moldRec instanceof MoldRec staticMoldRec)) continue;
            registry.addRecipe(new EmiMoldRecipe(staticMoldRec));
        }
        for (SMindInfusionRec mindInfusionRec : recipeManager.listAllOfType(SMindInfusionRec.Type.INSTANCE)) {
            registry.addRecipe(new EmiSMindInfusionRecipe(mindInfusionRec));
        }
    }
}
