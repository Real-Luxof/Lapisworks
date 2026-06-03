package com.luxof.lapisworks.blocks;

import com.luxof.lapisworks.blocks.entities.ChalkEntity;
import com.luxof.lapisworks.blocks.entities.ChalkWithPatternEntity;
import com.luxof.lapisworks.blocks.stuff.ChalkBlockInterface;
import com.luxof.lapisworks.blocks.stuff.AttachedBE;

import static com.luxof.lapisworks.LapisworksIDs.CANT_PLACE_CHALK_ON_TAG;
import static com.luxof.lapisworks.LapisworksIDs.CHALK_CONNECTABLE_TAG;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.util.ActionResult;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;

public class Chalk extends BlockWithEntity implements ChalkBlockInterface {
    public Chalk() {
        super(
            Settings.copy(Blocks.REDSTONE_WIRE)
                .mapColor(DyeColor.PINK)
                .dropsNothing()
                .sounds(BlockSoundGroup.SAND)
        );
    }

    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new ChalkEntity(pos, state);
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
        if (world.isClient) return;
        updateAttachmentTo(state, world, pos, fromPos, fromBlock);
    }

    public void updateAttachmentTo(
        BlockState state,
        World world,
        BlockPos pos,
        BlockPos adjBlockPos,
        Block adjBlock
    ) {
        BlockState adjState = world.getBlockState(adjBlockPos);
        Direction dir = Direction.fromVector(
            adjBlockPos.getX() - pos.getX(),
            adjBlockPos.getY() - pos.getY(),
            adjBlockPos.getZ() - pos.getZ()
        );
        ChalkEntity chalk = (ChalkEntity)world.getBlockEntity(pos);

        if (dir == chalk.attachedTo) {
            if (
                !adjState.isOf(adjBlock) ||
                !adjState.isSideSolidFullSquare(
                    world, adjBlockPos, chalk.attachedTo.getOpposite()
                ) ||
                adjState.isIn(CANT_PLACE_CHALK_ON_TAG)
            )
                world.breakBlock(pos, false);
            return;
        } else if (dir == chalk.attachedTo.getOpposite())
            return;

        BlockEntity adjBE = world.getBlockEntity(adjBlockPos);
        boolean succ = chalk.setSideIsChalk(
            dir,

            // "why specifically left bracket?" "your code is horrendous" "wtf is this"
            // fam...
            // don't look at me for answers. i don't know either.
            adjState.isIn(CHALK_CONNECTABLE_TAG) ||

            (adjBE instanceof AttachedBE attachedBE &&
            attachedBE.getAttachedTo() == chalk.attachedTo) &&

            (!(adjBE instanceof ChalkWithPatternEntity cwp) ||
            cwp.renderLeftBracket)
        );
        if (!succ) return;
        chalk.save();
    }
    public void updateAttachments(
        World world,
        BlockPos pos
    ) {
        BlockState state = world.getBlockState(pos);
        for (Direction dir : Direction.values()) {
            BlockPos otherPos = pos.offset(dir);
            Block otherBlock = world.getBlockState(otherPos).getBlock();
            updateAttachmentTo(state, world, pos, otherPos, otherBlock);
        }
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
        ChalkEntity chalk = (ChalkEntity)pLevel.getBlockEntity(pPos);
        if (chalk == null) return DOWN_SHAPE; // real possibility for some dumbshit reason (worldgen?)

        return switch (chalk.attachedTo) {
            case DOWN -> DOWN_SHAPE;
            case UP -> UP_SHAPE;
            case NORTH -> NORTH_SHAPE;
            case SOUTH -> SOUTH_SHAPE;
            case WEST -> WEST_SHAPE;
            case EAST -> EAST_SHAPE;
        };
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
        return ChalkBlockInterface.super.onUse(
            state,
            world,
            pos,
            player,
            hand,
            hit,
            ((ChalkEntity)world.getBlockEntity(pos)).attachedTo
        );
    }
}
