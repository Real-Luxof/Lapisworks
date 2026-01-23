package com.luxof.lapisworks.chalk;

import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import org.jetbrains.annotations.Nullable;

/** implemented by Block Entities to participate in Amethyst Rituals. */
public interface RitualComponent {
    default List<BlockPos> getPossibleNextBlocksGeneric(
        ServerWorld world,
        @Nullable Direction forward,
        BlockPos compPos
    ) {
        List<BlockPos> possibleNextBlocks = new ArrayList<>();

        for (Direction dir : Direction.values()) {
            if (forward != null && dir == forward.getOpposite()) continue;

            BlockPos candidatePos = compPos.offset(dir);
            if (
                world.getBlockEntity(candidatePos) instanceof RitualComponent &&
                executionCanFlowTo(world, candidatePos)
            )
                possibleNextBlocks.add(candidatePos);
        }

        return possibleNextBlocks;
    }
    @Nullable
    default BlockPos getNextBlockDuringExecutionHelper(RitualCastEnv env) {
        RitualExecutionState ritual = env.ritual();
        ServerWorld world = env.getWorld();
        BlockPos compPos = ritual.currentPos;

        if (ritual.forward != null) {
            BlockPos ahead = compPos.offset(ritual.forward);
            if (
                world.getBlockEntity(ahead) instanceof RitualComponent &&
                executionCanFlowTo(world, ahead)
            )
                return ahead;
        }

        List<BlockPos> choices = getPossibleNextBlocks(world, ritual.forward);

        if (choices.size() == 0) return null;
        return choices.get(world.random.nextInt(choices.size()));
    }
    public Direction getParticleSprayDir();
    /** Returns a list of possible next blocks, which are all <code>RitualComponent</code>s.
     * <p>this method executes when the ritual is being set up and is used to check things like if
     * there really is an endpoint. May not always execute, like in one-time rituals. */
    public List<BlockPos> getPossibleNextBlocks(ServerWorld world, @Nullable Direction forward);
    /** Returns the next position to execute and the new image for the cast.
     * <p>If the return value is null, the ritual has stopped due to a mishap or other reason. */
    @Nullable public Pair<BlockPos, CastingImage> execute(RitualCastEnv env);
    /** Called to rid the component of any powered/lit state it has. */
    public void unpower();
    /** Returns whether or not a ritual's execution can flow from this component to one at
     * the specified position. Useful when (e.g.) you don't want control flowing to chalk
     * not on the same plane as the previous. */
    public boolean executionCanFlowTo(ServerWorld world, BlockPos pos);
}
