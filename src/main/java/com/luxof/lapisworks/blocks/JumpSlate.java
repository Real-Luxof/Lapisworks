package com.luxof.lapisworks.blocks;

import at.petrak.hexcasting.api.block.circle.BlockCircleComponent;
import at.petrak.hexcasting.api.casting.eval.env.CircleCastEnv;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import at.petrak.hexcasting.api.casting.iota.DoubleIota;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.misc.MediaConstants;

import com.luxof.lapisworks.init.ModPOIs;

import com.mojang.datafixers.util.Pair;

import static com.luxof.lapisworks.Lapisworks.log;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Waterloggable;
import net.minecraft.block.enums.WallMountLocation;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.EnumProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.BlockMirror;
import net.minecraft.util.BlockRotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import net.minecraft.world.WorldView;
import net.minecraft.world.poi.PointOfInterestType;

import org.jetbrains.annotations.Nullable;

public class JumpSlate extends BlockCircleComponent implements Waterloggable {
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;
    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final EnumProperty<WallMountLocation> ATTACH_FACE = Properties.WALL_MOUNT_LOCATION;
    public static final double THICKNESS = 1;
    public static final VoxelShape AABB_FLOOR = Block.createCuboidShape(0, 0, 0, 16, THICKNESS, 16);
    public static final VoxelShape AABB_CEILING = Block.createCuboidShape(0, 16 - THICKNESS, 0, 16, 16, 16);
    public static final VoxelShape AABB_EAST_WALL = Block.createCuboidShape(0, 0, 0, THICKNESS, 16, 16);
    public static final VoxelShape AABB_WEST_WALL = Block.createCuboidShape(16 - THICKNESS, 0, 0, 16, 16, 16);
    public static final VoxelShape AABB_SOUTH_WALL = Block.createCuboidShape(0, 0, 0, 16, 16, THICKNESS);
    public static final VoxelShape AABB_NORTH_WALL = Block.createCuboidShape(0, 0, 16 - THICKNESS, 16, 16, 16);
    public JumpSlate() {
        super(Settings.copy(Blocks.DEEPSLATE_TILES).strength(4f, 4f));
        this.setDefaultState(
            this.stateManager.getDefaultState()
                .with(ENERGIZED, false)
                .with(FACING, Direction.NORTH)
                .with(WATERLOGGED, false));
    }

    public int SEARCH_LIMIT = 100;
    // used in CircleExecutionStateMixin.
    @Nullable
    public Pair<Direction, BlockPos> getProbableExitPlace(
        Direction enterDir,
        BlockPos herePos,
        ServerWorld world
    ) {
        log("We were called! here position: %s and direction: %s", herePos.toString(), enterDir.asString());
        BlockPos currPos = new BlockPos(herePos);
        Pair<Direction, BlockPos> exit = null;
        for (int i = 0; i < this.SEARCH_LIMIT; i++) {

            currPos = currPos.offset(enterDir);

            if (
                !world.getChunkManager()
                    .getPointOfInterestStorage()
                    .getType(currPos)
                    .map(poi -> poi.matchesKey(ModPOIs.SLATES_KEY))
                    .orElse(false)
            )
                continue;

            BlockState currState = world.getBlockState(currPos);
            if (!((BlockCircleComponent)currState.getBlock()).canEnterFromDirection(
                enterDir, currPos, currState, world
            ))
                continue;

            exit = new Pair<>(enterDir, currPos);
            break;
        }
        return exit;
    }

    @Override
    public boolean isTransparent(BlockState state, BlockView world, BlockPos pos) {
        return !state.get(WATERLOGGED);
    }

    @SuppressWarnings("deprecation")
    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
    }

    private Pair<Integer, CastingImage> pair(Integer num, CastingImage img) {
        return new Pair<Integer, CastingImage>(num, img);
    }
    private Pair<Integer, CastingImage> getStrength(CastingImage img) {
        List<Iota> stack = new ArrayList<>(img.getStack());
        int strength = 1;

        Pair<Integer, CastingImage> fail = pair(strength, img);
        if (stack.size() == 0) { return fail; }
        int lastIdx = stack.size() - 1; // -1 doesn't work
        Iota topIota = stack.get(lastIdx);
        if (!(topIota instanceof DoubleIota top)) { return fail; }
        // must have if not else if or else ^^^ doesn't exist
        // i'll let you interpret that
        if (!DoubleIota.tolerates(top.getDouble(), Math.round(top.getDouble()))) { return fail; }
        stack.remove(lastIdx);
        CastingImage newImg = img.copy(
            List.copyOf(stack),
            img.getParenCount(),
            img.getParenthesized(),
            img.getEscapeNext(),
            img.getOpsConsumed(),
            img.getUserData()
        );
        return pair((int)top.getDouble(), newImg);
    }

    public long calculateCostForStrength(int strength) {
        return (long)(MediaConstants.DUST_UNIT * 2.5 * strength);
    }
    // for @Override-ing in another block
    public Direction reverseDirIfNeeded(Direction dir, int strength) {
        return strength < 0 ? dir.getOpposite() : dir;
    }

    @Override
    public ControlFlow acceptControlFlow(
        CastingImage imgIn,
        CircleCastEnv env,
        Direction enterDir,
        BlockPos pos,
        BlockState bs,
        ServerWorld world
    ) {
        Pair<Integer, CastingImage> strengthResult = getStrength(imgIn);
        int stren = strengthResult.getFirst();
        CastingImage imgNow = strengthResult.getSecond();
        // >0 = not enough media found
        if (stren != 0 && stren != 1 && stren >= -100 && stren <= 100) {
            if (env.extractMedia(calculateCostForStrength(Math.abs(stren)), true) > 0) stren = 1;
            else env.extractMedia(calculateCostForStrength(Math.abs(stren)), false);
        } else stren = 1;

        List<Pair<BlockPos, Direction>> exitDirs = List.of(
            // get opp if strength < 0, 'cause then it should reverse direction
            this.exitPositionFromDirection(pos, enterDir, reverseDirIfNeeded(enterDir, stren), stren)
        );

        return new ControlFlow.Continue(imgNow, exitDirs);
    }

    public Pair<BlockPos, Direction> exitPositionFromDirection(BlockPos pos, Direction moveDir, Direction endUpMovingDir, int strength) {
        return Pair.of(pos.add(moveDir.getVector().multiply(strength)), endUpMovingDir);
    }

    @Override
    public boolean canEnterFromDirection(Direction enterDir, BlockPos pos, BlockState bs, ServerWorld world) {
        return enterDir != this.normalDir(pos, bs, world).getOpposite();
    }

    @Override
    public EnumSet<Direction> possibleExitDirections(BlockPos pos, BlockState bs, World world) {
        EnumSet<Direction> dirs = EnumSet.allOf(Direction.class);
        dirs.remove(this.normalDir(pos, bs, world));
        return dirs;
    }

    public EnumSet<Direction> exitDirs(BlockPos pos, BlockState bs, World world, Direction ent) {
        EnumSet<Direction> dirs = possibleExitDirections(pos, bs, world);
        dirs.remove(ent.getOpposite());
        return dirs;
    }

    /*@Override
    public Direction normalDir(BlockPos pos, BlockState bs, World world, int recursionLeft) {
        switch (bs.get(ATTACH_FACE)) {
            case FLOOR: { return Direction.UP; }
            case CEILING: { return Direction.DOWN; }
            case WALL: { return bs.get(FACING); }
            default: return null; // <--- never reached, but silences VSCode
        }
    }*/
    @Override
    public Direction normalDir(BlockPos pos, BlockState bs, World world, int recursionLeft) {
        // why does the new kind of switch expression work when i'm not the one writing it :sob:
        return switch (bs.get(ATTACH_FACE)) {
            case FLOOR -> Direction.UP;
            case CEILING -> Direction.DOWN;
            case WALL -> bs.get(FACING);
        };
    }

    @Override
    public float particleHeight(BlockPos pos, BlockState bs, World world) {
        return 0.5f - 15f / 16f;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState pState, BlockView pLevel, BlockPos pPos, ShapeContext pContext) {
        return switch (pState.get(ATTACH_FACE)) {
            case FLOOR -> AABB_FLOOR;
            case CEILING -> AABB_CEILING;
            case WALL -> switch (pState.get(FACING)) {
                case NORTH -> AABB_NORTH_WALL;
                case EAST -> AABB_EAST_WALL;
                case SOUTH -> AABB_SOUTH_WALL;
                // NORTH; up and down don't happen (but i need branches for them)
                default -> AABB_WEST_WALL;
            };
        };
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        super.appendProperties(builder);
        builder.add(FACING, ATTACH_FACE, WATERLOGGED);
    }

    @Override
    @Nullable
    public BlockState getPlacementState(ItemPlacementContext pContext) {
        FluidState fluidState = pContext.getWorld().getFluidState(pContext.getBlockPos());

        for (Direction direction : pContext.getPlacementDirections()) {
            BlockState blockstate;
            if (direction.getAxis() == Direction.Axis.Y) {
                blockstate = this.getDefaultState()
                    .with(ATTACH_FACE, direction == Direction.UP ? WallMountLocation.CEILING : WallMountLocation.FLOOR)
                    .with(FACING, pContext.getHorizontalPlayerFacing().getOpposite());
            } else {
                blockstate = this.getDefaultState()
                    .with(ATTACH_FACE, WallMountLocation.WALL)
                    .with(FACING, direction.getOpposite());
            }
            blockstate = blockstate.with(WATERLOGGED,
                fluidState.isIn(FluidTags.WATER) && fluidState.getLevel() == 8);

            if (blockstate.canPlaceAt(pContext.getWorld(), pContext.getBlockPos())) {
                return blockstate;
            }
        }

        return null;
    }

    // i copy and paste as the BlockSlate.java guides
    @Override
    public boolean canPlaceAt(BlockState pState, WorldView pLevel, BlockPos pPos) {
        return canAttach(pLevel, pPos, getConnectedDirection(pState).getOpposite());
    }

    @SuppressWarnings("deprecation")
    @Override
    public BlockState getStateForNeighborUpdate(BlockState pState, Direction pFacing, BlockState pFacingState, WorldAccess pLevel,
        BlockPos pCurrentPos, BlockPos pFacingPos) {
        if (pState.get(WATERLOGGED)) {
            pLevel.scheduleFluidTick(pCurrentPos, Fluids.WATER, Fluids.WATER.getTickRate(pLevel));
        }

        return getConnectedDirection(pState).getOpposite() == pFacing
            && !pState.canPlaceAt(pLevel, pCurrentPos) ?
            pState.getFluidState().getBlockState()
            : super.getStateForNeighborUpdate(pState, pFacing, pFacingState, pLevel, pCurrentPos, pFacingPos);
    }

    public static boolean canAttach(WorldView pReader, BlockPos pPos, Direction pDirection) {
        BlockPos blockpos = pPos.offset(pDirection);
        return pReader.getBlockState(blockpos).isSideSolidFullSquare(pReader, blockpos, pDirection.getOpposite());
    }

    protected static Direction getConnectedDirection(BlockState pState) {
        return switch (pState.get(ATTACH_FACE)) {
            case CEILING -> Direction.DOWN;
            case FLOOR -> Direction.UP;
            default -> pState.get(FACING);
        };
    }

    public BlockState rotate(BlockState state, BlockRotation rot) {
        return (BlockState)state.with(FACING, rot.rotate((Direction)state.get(FACING)));
    }

    public BlockState mirror(BlockState state, BlockMirror mirror) {
        return state.rotate(mirror.getRotation((Direction)state.get(FACING)));
    }
}
