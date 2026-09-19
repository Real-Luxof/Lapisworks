package com.luxof.lapisworks.blocks.entities;

import at.petrak.hexcasting.api.misc.MediaConstants;
import at.petrak.hexcasting.fabric.cc.HexCardinalComponents;

import com.luxof.lapisworks.blocks.Mind;
import com.luxof.lapisworks.init.ModBlocks;
import com.luxof.lapisworks.mixinsupport.ArtMindInterface;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

public class MindEntity extends BlockEntity {
    public float mindCompletion = 0f;
    // all of this is for you to fw
    public int maxVillagers = 3;
    public int gatherRange = 5;
    // magic numbers: fills to 100 by the 4 minute mark
    public float villagerExhaustionRate = 100f / (4f * 60f * 20f);
    // magic numbers: 20 seconds
    public int villagerExhaustRegenCD = 20 * 20;
    // magic number: entire minecraft day in ticks
    public int sleepingVillagerUseAgainCD = 24000;
    // magic numbers: fills to 100 by the 20 minute mark
    public float mindCompleteRatePerVillager = 100f / (20f * 60f * 20f);

    public MindEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.MIND_ENTITY_TYPE, pos, state);
    }

    public long getMaxMediaGainFromAbsorption() {
        // here for your @Override pleasure
        return MediaConstants.CRYSTAL_UNIT;
    }

    public static int computeFilledState(float filledPercent) {
        // 7.14 is 100 / 14
        // i need to do X/(100/14) to get nums from 0-14
        return (int)Math.max(Math.min(Math.floor((double)filledPercent / 7.14), 14.0), 0.0);
    }

    public void consumeVillagerMinds(World world, BlockPos pos) {
        Vec3d topLeftBack = pos.toCenterPos().subtract(gatherRange, gatherRange, gatherRange);
        Vec3d bottomRightFront = pos.toCenterPos().add(gatherRange, gatherRange, gatherRange);
        List<VillagerEntity> villagersNear = new ArrayList<VillagerEntity>(world.getEntitiesByClass(
            VillagerEntity.class,
            new Box(topLeftBack, bottomRightFront),
            (villager) -> !HexCardinalComponents.BRAINSWEPT.get(villager).isBrainswept()
                    && ((ArtMindInterface)villager).getUsedMindPercentage() < 100.0f
                    && ((ArtMindInterface)villager).getDontUseAgainTicks() <= 0
        ));

        int usedVillagersCount = 0;

        for (int i = 0; i < Math.min(villagersNear.size(), maxVillagers); i++) {
            int idx = world.random.nextInt(villagersNear.size());
            VillagerEntity villager = villagersNear.get(idx);

            if (villager.isSleeping() && usedVillagersCount == 0) {
                this.mindCompletion += 15.0f;
                this.mindCompletion = Math.min(100.0f, this.mindCompletion);
                ((ArtMindInterface)villager).setDontUseAgainTicks(sleepingVillagerUseAgainCD);
                break;
            }

            usedVillagersCount += 1;
            villagersNear.remove(idx);

            // note: villager mined using percentage and mindCompletion can both be >100
            // i'm aware, and this doesn't matter that much
            ((ArtMindInterface)villager).incUsedMindPercentage(villagerExhaustionRate);
            ((ArtMindInterface)villager).setMindBeingUsedTicks(villagerExhaustRegenCD);
            this.mindCompletion += mindCompleteRatePerVillager;
            this.mindCompletion = Math.min(100.0f, this.mindCompletion);
        }
    }

    public static void tick(World world, BlockPos pos, BlockState state, BlockEntity bEnt) {
        if (world.isClient) { return; }

        MindEntity blockEntity = (MindEntity)bEnt; // do this or function signature doesn't match???

        if (blockEntity.mindCompletion < 100.0f) { blockEntity.consumeVillagerMinds(world, pos); }

        int shouldBeAtState = computeFilledState(blockEntity.mindCompletion);
        if (state.get(Mind.FILLED) != shouldBeAtState) {
            world.setBlockState(pos, state.with(Mind.FILLED, shouldBeAtState));
        }

        blockEntity.markDirty();
        world.updateListeners(pos, state, state, Block.NOTIFY_ALL);
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putFloat("mindCompletion", this.mindCompletion);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        this.mindCompletion = nbt.getFloat("mindCompletion");
    }

    @Override @Nullable
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }
}
