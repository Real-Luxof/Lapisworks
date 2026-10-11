package com.luxof.lapisworks.recipes.mold;

import com.google.gson.JsonObject;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.recipe.RecipeSerializer;
import net.minecraft.util.Identifier;

public class EnchantedBookSwapRecSerializer implements RecipeSerializer<EnchantedBookSwapRec> {

    private EnchantedBookSwapRecSerializer() {}

    public static EnchantedBookSwapRecSerializer INSTANCE = new EnchantedBookSwapRecSerializer();

    @Override
    public EnchantedBookSwapRec read(Identifier id, JsonObject json) {
        return new EnchantedBookSwapRec(id);
    }

    @Override
    public EnchantedBookSwapRec read(Identifier id, PacketByteBuf buf) {
        return new EnchantedBookSwapRec(id);
    }

    @Override
    public void write(PacketByteBuf buf, EnchantedBookSwapRec recipe) {}
}
