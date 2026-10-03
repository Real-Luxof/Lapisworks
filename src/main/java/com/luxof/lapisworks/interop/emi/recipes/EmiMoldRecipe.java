package com.luxof.lapisworks.interop.emi.recipes;

import com.luxof.lapisworks.recipes.mold.MoldRec;

import static com.luxof.lapisworks.Lapisworks.id;
import static com.luxof.lapisworks.interop.emi.LapisworksEmiPlugin.MOLD_CATEGORY;

import dev.emi.emi.api.recipe.EmiRecipe;
import dev.emi.emi.api.recipe.EmiRecipeCategory;
import dev.emi.emi.api.render.EmiTexture;
import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.emi.api.widget.WidgetHolder;

import java.util.List;

import net.minecraft.util.Identifier;

import org.jetbrains.annotations.Nullable;

public class EmiMoldRecipe implements EmiRecipe {
    public static final EmiTexture BACKGROUND = new EmiTexture(
        id("textures/gui/emi/mold_bg.png"),
        0,
        0,
        26,
        72
    );

    private final Identifier id;
    private final EmiIngredient swapFrom;
    private final EmiStack swapTo;

    private final List<EmiIngredient> input;
    private final List<EmiStack> output;

    public EmiMoldRecipe(MoldRec recipe) {
        id = recipe.getId();
        swapFrom = EmiIngredient.of(recipe.getInput());
        swapTo = EmiStack.of(recipe.getOutput());
        input = List.of(swapFrom);
        output = List.of(swapTo);
    }

    @Override
    public void addWidgets(WidgetHolder widgets) {
        widgets.addTexture(BACKGROUND, -4, -4);
        widgets.addSlot(swapTo, 0, 0).drawBack(false);
        widgets.addSlot(swapFrom, 0, 46).drawBack(false).recipeContext(this);
    }

    @Override public EmiRecipeCategory getCategory() { return MOLD_CATEGORY; }
    @Override public @Nullable Identifier getId() { return id; }
    @Override public List<EmiIngredient> getInputs() { return input; }
    @Override public List<EmiStack> getOutputs() { return output; }
    @Override public int getDisplayWidth() { return 18; }
    @Override public int getDisplayHeight() { return 64; }
}
