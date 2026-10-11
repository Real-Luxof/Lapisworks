package com.luxof.lapisworks.interop.hextended.items;

import at.petrak.hexcasting.api.utils.NBTHelper;

import com.luxof.lapisworks.VAULT.Flags;
import com.luxof.lapisworks.VAULT.VAULT;
import com.luxof.lapisworks.init.Mutables.Mutables;
import com.luxof.lapisworks.mixinsupport.GetVAULT;

import static com.luxof.lapisworks.Lapisworks.prettifyVec3d;

import java.util.List;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

public class AmelOrb extends Item {
    private static final Settings defaultSettings = new Item.Settings().maxCount(1);
    public AmelOrb() { super(defaultSettings); }
    public AmelOrb(Settings settings) {super(settings); }

    private static final String PLACE_IN_AMBIT_X = "place_in_ambit_X";
    private static final String PLACE_IN_AMBIT_Y = "place_in_ambit_Y";
    private static final String PLACE_IN_AMBIT_Z = "place_in_ambit_Z";
    private static final String PLACE_IN_AMBIT_IS_NULL = "place_in_ambit_is_null";
    
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        ItemStack stack = user.getStackInHand(hand);

        if (user.getWorld().isClient) return TypedActionResult.success(stack, true);

        VAULT vault = ((GetVAULT)(ServerPlayerEntity)user).grabVAULT();
        vault.drain(Mutables::isAmel, 10, true, Flags.PRESET_UpToHands);

        this.setPlaceInAmbit(stack, user.getEyePos());
        return TypedActionResult.success(stack, true);
    }

    public @Nullable Vec3d getPlaceInAmbit(ItemStack stack) {
        Boolean isNull = NBTHelper.getBoolean(stack, PLACE_IN_AMBIT_IS_NULL);
        if (isNull == null || isNull) return null;
        double x = NBTHelper.getDouble(stack, PLACE_IN_AMBIT_X);
        double y = NBTHelper.getDouble(stack, PLACE_IN_AMBIT_Y);
        double z = NBTHelper.getDouble(stack, PLACE_IN_AMBIT_Z);
        return new Vec3d(x, y, z);
    }
    public void setPlaceInAmbit(ItemStack stack, @Nullable Vec3d vec) {
        NBTHelper.putBoolean(stack, PLACE_IN_AMBIT_IS_NULL, vec == null);
        if (vec == null) return;
        NBTHelper.putDouble(stack, PLACE_IN_AMBIT_X, vec.x);
        NBTHelper.putDouble(stack, PLACE_IN_AMBIT_Y, vec.y);
        NBTHelper.putDouble(stack, PLACE_IN_AMBIT_Z, vec.z);
    }

    @Override
    public void appendTooltip(
        ItemStack stack,
        @Nullable World world,
        List<Text> components,
        TooltipContext flag
    ) {
        Vec3d placeInAmbit = prettifyVec3d(this.getPlaceInAmbit(stack));
        components.add(
            Text.translatable("tooltips.lapisworks.amel_orb.pre")
                .formatted(Formatting.BLUE)
                .append(
                    placeInAmbit == null ?
                    Text.translatable("tooltips.lapisworks.amel_orb.none")
                        .formatted(Formatting.GRAY) :
                    Text.translatable(
                        "tooltips.lapisworks.amel_orb.set", placeInAmbit.x, placeInAmbit.y, placeInAmbit.z
                    ).formatted(Formatting.GREEN)
            )
        );
    }
}
