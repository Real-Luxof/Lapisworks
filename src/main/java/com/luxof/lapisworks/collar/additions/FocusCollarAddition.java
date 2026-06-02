package com.luxof.lapisworks.collar.additions;

import at.petrak.hexcasting.api.casting.iota.IotaType;
import at.petrak.hexcasting.api.casting.iota.NullIota;
import at.petrak.hexcasting.api.utils.NBTHelper;
import at.petrak.hexcasting.common.lib.HexItems;

import com.luxof.lapisworks.collar.LapisCollarAddition;

import static com.luxof.lapisworks.Lapisworks.id;
import static com.luxof.lapisworks.init.ModItems.COLLAR;

import java.util.List;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;

import org.jetbrains.annotations.Nullable;

public class FocusCollarAddition implements LapisCollarAddition {
    public static final Identifier ID = id("collar/focus");

    @Override
    public Text getName() {
        return Text.translatable(
            "tooltips.lapisworks.collar.added_items.focus",
            new NullIota().display()
        ).formatted(Formatting.DARK_PURPLE);
    }

    @Override
    public Text getName(ItemStack collarStack) {
        return Text.translatable(
            "tooltips.lapisworks.collar.added_items.focus",
            IotaType.getDisplay(NBTHelper.getCompound(collarStack, "stored_iota"))
        ).formatted(Formatting.DARK_PURPLE);
    }

    @Override
    public boolean testItem(Item item) {
        return item == HexItems.FOCUS;
    }

    @Override
    public boolean canAdd(ItemStack collarStack, List<Identifier> existingAdditions, Identifier yourId) {
        return true;
    }

    @Override
    public ItemStack craft(ItemStack collarStack, List<Identifier> existingAdditions, ItemStack yourStack,
            Identifier yourId) {
        ItemStack newCollar = collarStack.copy();
        COLLAR.addAddition(newCollar, yourId);
        NBTHelper.putCompound(newCollar, "stored_iota", IotaType.serialize(new NullIota()));
        return newCollar;
    }

    @Override
    @Environment(EnvType.CLIENT)
    public void render(ItemStack collarStack, Identifier yourId, @Nullable LivingEntity entity,
            ModelTransformationMode mode, MatrixStack matrices, VertexConsumerProvider vertexConsumers, int light,
            int overlay) {
        return;
    }
}
