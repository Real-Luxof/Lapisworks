package com.luxof.lapisworks.recipes;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import com.luxof.lapisworks.recipes.ImbuementRec.ImbuementRecJsonFormat;

import net.minecraft.item.Item;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.Ingredient;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;

public class ImbuementRecSerializer implements RecipeSerializer<ImbuementRec> {
    private ImbuementRecSerializer() {}
    public static final ImbuementRecSerializer INSTANCE = new ImbuementRecSerializer();

    @Override
    public ImbuementRec read(Identifier id, JsonObject json) {
        ImbuementRecJsonFormat recipeJson = new Gson().fromJson(json, ImbuementRecJsonFormat.class);
        if (recipeJson.normal == null) {
            throw new JsonSyntaxException("Base ingredient unspecified. (" + id + ")");
        } else if (recipeJson.fullamel == null) {
            throw new JsonSyntaxException("End product unspecified. (" + id + ")");
        } else if (recipeJson.cost == null) {
            throw new JsonSyntaxException("Cost to transform from base to end unspecified. (" + id + ")");
        }

        Ingredient normal = Ingredient.fromJson(recipeJson.normal);
        Item partAmel = recipeJson.partamel != null
            ? Registries.ITEM.get(new Identifier(recipeJson.partamel))
            : null;

        Item fullAmel = Registries.ITEM.getOrEmpty(new Identifier(recipeJson.fullamel))
            .orElseThrow(
                () -> new JsonSyntaxException("No such item (fullamel): " + recipeJson.fullamel)
            );

        return new ImbuementRec(id, normal, partAmel, fullAmel, recipeJson.cost);
    }

    @Override
    public ImbuementRec read(Identifier id, PacketByteBuf buf) {
        return new ImbuementRec(
            id,
            Ingredient.fromPacket(buf),
            buf.readBoolean() ? Registries.ITEM.get(buf.readIdentifier()) : null,
            Registries.ITEM.get(buf.readIdentifier()),
            buf.readInt()
        );
    }

    @Override
    public void write(PacketByteBuf buf, ImbuementRec recipe) {
        recipe.getNormal().write(buf);
        buf.writeBoolean(recipe.getPartAmel() != null);
        if (recipe.getPartAmel() != null)
            buf.writeIdentifier(Registries.ITEM.getId(recipe.getPartAmel()));
        buf.writeIdentifier(Registries.ITEM.getId(recipe.getFullAmel()));
        buf.writeInt(recipe.getFullAmelsCost());
    }
}
