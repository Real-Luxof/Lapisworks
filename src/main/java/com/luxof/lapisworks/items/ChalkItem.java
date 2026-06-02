package com.luxof.lapisworks.items;

import static com.luxof.lapisworks.LapisworksIDs.CANT_PLACE_CHALK_ON_TAG;

import com.luxof.lapisworks.blocks.entities.ChalkEntity;
import com.luxof.lapisworks.blocks.entities.ChalkWithPatternEntity;
import com.luxof.lapisworks.init.ModBlocks;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;
import net.minecraft.world.event.GameEvent;
import net.minecraft.world.event.GameEvent.Emitter;

public class ChalkItem extends BlockItem {
    public Block blockWithPattern = ModBlocks.CHALK_WITH_PATTERN;

    public ChalkItem() {
        super(
            ModBlocks.CHALK,
            new FabricItemSettings().maxCount(1).maxDamage(1024)
        );
    }

    private void playPlaceSound(World world, BlockPos pos) {
        world.playSoundAtBlockCenter(
            pos,
            SoundEvents.BLOCK_STONE_PLACE,
            SoundCategory.BLOCKS,
            1f,
            7f,
            false
        );
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        World world = context.getWorld();
        BlockPos chalkPos = context.getBlockPos();
        ItemStack chalkStack = context.getStack();
        BlockState chalkState = world.getBlockState(chalkPos);

        if (chalkState.isOf(this.getBlock())) {

            Direction attachedTo = ((ChalkEntity)world.getBlockEntity(chalkPos)).attachedTo;

            chalkStack.damage(1, context.getPlayer(), whatever -> {});
            world.setBlockState(chalkPos, blockWithPattern.getDefaultState());
            BlockState stateNow = world.getBlockState(chalkPos);

            ((ChalkWithPatternEntity)world.getBlockEntity(chalkPos)).attachedTo = attachedTo;
            blockWithPattern.onPlaced(world, chalkPos, chalkState, context.getPlayer(), chalkStack);
            world.updateListeners(
                context.getBlockPos(),
                stateNow,
                stateNow,
                Block.NOTIFY_ALL
            );
            doChalkUpdatesDude(world, chalkPos);
            playPlaceSound(world, chalkPos);

            return ActionResult.success(world.isClient);

        }

        return super.useOnBlock(context);
    }

    @Override
    protected boolean canPlace(ItemPlacementContext ctx, BlockState state) {
        boolean og = super.canPlace(ctx, state);

        World world = ctx.getWorld();
        BlockPos pos = ctx.getBlockPos();
        Direction dir = ctx.getSide();
        BlockState worryAboutState = world.getBlockState(pos.offset(dir.getOpposite()));

        return og
            && worryAboutState.isSideSolidFullSquare(world, pos, dir)
            && !worryAboutState.isIn(CANT_PLACE_CHALK_ON_TAG);
    }

    @SuppressWarnings("deprecation")
    private void doChalkUpdatesDude(
        World world,
        BlockPos pos
    ) {
        Block block = world.getBlockState(pos).getBlock();
        for (Direction dir : Direction.values()) {
            BlockPos thisPos = pos.offset(dir);
            BlockState thisState = world.getBlockState(thisPos);
            thisState.neighborUpdate(
                world,
                thisPos,
                block,
                pos,
                false
            );
        }
    }

    @Override
    public ActionResult place(ItemPlacementContext context) {

        World world = context.getWorld();
        BlockPos pos = context.getBlockPos();
        BlockState state = world.getBlockState(pos);

        try {
            return super.place(context);
        } catch (EvilException evil) {
            // getPlaceSound is only called when place is about to succeed.
            // basically i just want the checks and bells and whistles from super.place to have occured.
            PlayerEntity player = context.getPlayer();
            world.emitGameEvent(GameEvent.BLOCK_PLACE, pos, Emitter.of(player, evil.bs));
            if (player == null || !player.getAbilities().creativeMode) {
                context.getStack().damage(1, player, dontCare -> {});
            }

            // Sinytra, is that what you're doing, you rat bastard?
            ChalkEntity chalk = (ChalkEntity)world.getBlockEntity(pos);

            chalk.attachedTo = context.getSide().getOpposite();
            context.getWorld().updateListeners(
                context.getBlockPos(),
                state,
                state,
                Block.NOTIFY_ALL
            );
            ModBlocks.CHALK.updateAttachments(world, pos);
            doChalkUpdatesDude(context.getWorld(), pos);
            playPlaceSound(world, pos);
            return ActionResult.success(world.isClient);
        }
    }

    @Override
    public SoundEvent getPlaceSound(BlockState state) {
        throw new EvilException("this is w luxof coding /sarcasm", state);
    }

    public static final class EvilException extends RuntimeException {
        // i find it funny that blockstate = bs = bullshit, so this is EvilException.bs
        public final BlockState bs;
        public EvilException(String s, BlockState state) {
            super(s);
            bs = state;
        }
    }
}
