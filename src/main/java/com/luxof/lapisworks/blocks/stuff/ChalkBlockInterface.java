package com.luxof.lapisworks.blocks.stuff;

import at.petrak.hexcasting.api.HexAPI;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import at.petrak.hexcasting.api.utils.MediaHelper;
import at.petrak.hexcasting.common.lib.HexItems;
import at.petrak.hexcasting.xplat.IXplatAbstractions;

import com.luxof.lapisworks.chalk.OneTimeRitualExecutionState;
import com.luxof.lapisworks.mixinsupport.RitualsUtil;

import static com.luxof.lapisworks.Lapisworks.getFacingWithRespectToDown;

import java.util.List;

import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public interface ChalkBlockInterface {
    default ActionResult onUse(
        BlockState state,
        World world,
        BlockPos pos,
        PlayerEntity player,
        Hand hand,
        BlockHitResult hit,
        Direction attachedTo
    ) {
        ItemStack stack = player.getStackInHand(hand);
        if (
            hit.getType() == HitResult.Type.MISS ||
            !MediaHelper.isMediaItem(stack) ||
            stack.isOf(HexItems.BATTERY)
        ) return ActionResult.PASS;
        else if (world.isClient) return ActionResult.SUCCESS;

        ((RitualsUtil)world).addRitual(new OneTimeRitualExecutionState(
            pos,
            getFacingWithRespectToDown(player.getRotationVector(), attachedTo),
            new CastingImage(),
            player.getUuid(),
            HexAPI.instance().getColorizer(player),
            IXplatAbstractions.INSTANCE.findMediaHolder(stack).withdrawMedia(
                -1,
                player.isCreative()
            ),
            List.of()
        ));

        return ActionResult.SUCCESS;
    }
}
