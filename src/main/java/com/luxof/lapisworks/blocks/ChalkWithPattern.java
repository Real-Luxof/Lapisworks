package com.luxof.lapisworks.blocks;

import com.luxof.lapisworks.blocks.entities.ChalkWithPatternEntity;
import com.luxof.lapisworks.blocks.stuff.ChalkBlockInterface;
import com.luxof.lapisworks.blocks.stuff.AttachedBE;
import com.luxof.lapisworks.init.ModBlocks;
import com.luxof.lapisworks.init.ModItems;

import static com.luxof.lapisworks.LapisworksIDs.CHALK_CONNECTABLE_TAG;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.NamedScreenHandlerFactory;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

import static net.minecraft.util.math.Direction.UP;
import static net.minecraft.util.math.Direction.DOWN;
import static net.minecraft.util.math.Direction.NORTH;
import static net.minecraft.util.math.Direction.WEST;
import static net.minecraft.util.math.Direction.SOUTH;

public class ChalkWithPattern extends BlockWithEntity implements ChalkBlockInterface {
    public ChalkWithPattern() {
        super(
            Settings.copy(Blocks.REDSTONE_WIRE)
                .mapColor(DyeColor.PINK)
                .dropsNothing()
                .sounds(BlockSoundGroup.SAND)
        );
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos arg0, BlockState arg1) {
        return new ChalkWithPatternEntity(arg0, arg1);
    }

    @Override
    public ActionResult onUse(
        BlockState state,
        World world,
        BlockPos pos,
        PlayerEntity player,
        Hand hand,
        BlockHitResult hit
    ) {
        if (!player.getStackInHand(hand).isOf(ModItems.CHALK))
            return ChalkBlockInterface.super.onUse(
                state,
                world,
                pos,
                player,
                hand,
                hit,
                ((ChalkWithPatternEntity)world.getBlockEntity(pos)).attachedTo
            );

        if (world.isClient) return ActionResult.SUCCESS;
        NamedScreenHandlerFactory screenHandlerFactory = state.createScreenHandlerFactory(world, pos);
        if (screenHandlerFactory != null) player.openHandledScreen(screenHandlerFactory);
        return ActionResult.SUCCESS;
    }

    private static VoxelShape DOWN_SHAPE = Block.createCuboidShape(0, 0, 0, 16, 1, 16);
    private static VoxelShape UP_SHAPE = Block.createCuboidShape(0, 15, 0, 16, 16, 16);
    private static VoxelShape NORTH_SHAPE = Block.createCuboidShape(0, 0, 0, 16, 16, 1);
    private static VoxelShape SOUTH_SHAPE = Block.createCuboidShape(0, 0, 15, 16, 16, 16);
    private static VoxelShape WEST_SHAPE = Block.createCuboidShape(0, 0, 0, 1, 16, 16);
    private static VoxelShape EAST_SHAPE = Block.createCuboidShape(15, 0, 0, 16, 16, 16);
    @Override
    public VoxelShape getOutlineShape(
        BlockState pState,
        BlockView pLevel,
        BlockPos pPos,
        ShapeContext pContext
    ) {
        ChalkWithPatternEntity chalk = (ChalkWithPatternEntity)pLevel.getBlockEntity(pPos);
        if (chalk == null) return DOWN_SHAPE;

        return switch (chalk.attachedTo) {
            case DOWN -> DOWN_SHAPE;
            case UP -> UP_SHAPE;
            case NORTH -> NORTH_SHAPE;
            case SOUTH -> SOUTH_SHAPE;
            case WEST -> WEST_SHAPE;
            case EAST -> EAST_SHAPE;
        };
    }

    // "left and front, according to WHAT?" i hear you ask.
    // whatever makes render gud.
    private Direction findLeftVector(Direction down) {
        return switch (down) {
            case UP -> WEST;
            case DOWN -> WEST;
            case NORTH -> WEST;
            case WEST -> UP;
            case SOUTH -> WEST;
            case EAST -> DOWN;
        };
    }
    private Direction findFrontVector(Direction down) {
        return switch (down) {
            case UP -> SOUTH;
            case DOWN -> NORTH;
            case NORTH -> UP;
            case WEST -> NORTH;
            case SOUTH -> DOWN;
            case EAST -> NORTH;
        };
    }

    @Override
    public void neighborUpdate(
        BlockState state,
        World world,
        BlockPos pos,
        Block fromBlock,
        BlockPos fromPos,
        boolean notify
    ) {
        ChalkWithPatternEntity chalk = (ChalkWithPatternEntity)world.getBlockEntity(pos);
        BlockState fromState = world.getBlockState(fromPos);

        if (!fromState.isIn(CHALK_CONNECTABLE_TAG)) {
            updateBracketRendering(world, pos, chalk);
            chalk.save();
            return;
        }

        Direction comingFrom = Direction.fromVector(
            fromPos.getX() - pos.getX(),
            fromPos.getY() - pos.getY(),
            fromPos.getZ() - pos.getZ()
        );

        if (comingFrom == chalk.attachedTo) {
            world.breakBlock(pos, false);
            return;
        }

        if (!chalk.renderLeftBracket || !chalk.renderRightBracket)
            return;

        Direction leftOrRight = findLeftVector(chalk.attachedTo);

        if (comingFrom == leftOrRight || comingFrom == leftOrRight.getOpposite())
            chalk.rotated = false;
        else
            chalk.rotated = true;
        
        updateBracketRendering(world, pos, chalk);
        chalk.save();
    }

    private int assignPointsTo(World world, BlockPos pos, Direction attachedTo) {
        int ret = 0;
        
        BlockState state = world.getBlockState(pos);

        if (state.isIn(CHALK_CONNECTABLE_TAG))
            ret += 1;
        else
            return 0;

        if (
            world.getBlockEntity(pos) instanceof AttachedBE attachedConnectable &&
            attachedConnectable.getAttachedTo() == attachedTo
        )
            ret += 1;
        else
            return 0;

        if (state.isOf(ModBlocks.CHALK_WITH_PATTERN)) ret += 2;

        return ret;
    }
    @Override
    public void onPlaced(
        World world,
        BlockPos pos,
        BlockState state,
        LivingEntity placer,
        ItemStack itemStack
    ) {
        ChalkWithPatternEntity chalk = (ChalkWithPatternEntity)world.getBlockEntity(pos);

        Direction left = findLeftVector(chalk.attachedTo);
        Direction front = findFrontVector(chalk.attachedTo);
        Direction attachedTo = chalk.attachedTo;

        int onLeftAndRight = assignPointsTo(world, pos.offset(left), attachedTo) +
            assignPointsTo(world, pos.offset(left.getOpposite()), attachedTo);

        int onFrontAndBack = assignPointsTo(world, pos.offset(front), attachedTo) +
            assignPointsTo(world, pos.offset(front.getOpposite()), attachedTo);

        if (onFrontAndBack > onLeftAndRight) chalk.rotated = true;
        else if (onFrontAndBack == onLeftAndRight) chalk.rotated = Math.random() < 0.5;
        else chalk.rotated = false;

        updateBracketRendering(world, pos, chalk);
        chalk.save();
    }

    private boolean isChalkWP(World world, BlockPos pos, Direction attachedTo) {
        return world.getBlockState(pos).getBlock() == ModBlocks.CHALK_WITH_PATTERN
            && ((AttachedBE)world.getBlockEntity(pos)).getAttachedTo() == attachedTo;
    }
    public void updateBracketRendering(
        World world,
        BlockPos pos,
        ChalkWithPatternEntity chalk
    ) {
        Direction left = findLeftVector(chalk.attachedTo);
        Direction front = findFrontVector(chalk.attachedTo);
        Direction attachedTo = chalk.attachedTo;

        if (chalk.rotated) {
            chalk.renderLeftBracket = !isChalkWP(world, pos.offset(front.getOpposite()), attachedTo);
            chalk.renderRightBracket = !isChalkWP(world, pos.offset(front), attachedTo);
        } else {
            chalk.renderLeftBracket = !isChalkWP(world, pos.offset(left), attachedTo);
            chalk.renderRightBracket = !isChalkWP(world, pos.offset(left.getOpposite()), attachedTo);
        }
    }

    @Override
    public void onBreak(World world, BlockPos pos, BlockState state, PlayerEntity player) {
        super.onBreak(world, pos, state, player);

        world.playSoundAtBlockCenter(
            pos,
            SoundEvents.BLOCK_SAND_BREAK,
            SoundCategory.BLOCKS,
            1f,
            1f,
            false
        );
    }
}
