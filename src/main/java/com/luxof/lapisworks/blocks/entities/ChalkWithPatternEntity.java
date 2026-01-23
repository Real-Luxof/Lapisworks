package com.luxof.lapisworks.blocks.entities;

import at.petrak.hexcasting.api.casting.eval.ExecutionClientView;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import at.petrak.hexcasting.api.casting.eval.vm.CastingVM;
import at.petrak.hexcasting.api.casting.iota.PatternIota;
import at.petrak.hexcasting.api.casting.math.HexPattern;

import com.luxof.lapisworks.blocks.stuff.AttachedBE;
import com.luxof.lapisworks.chalk.RitualCastEnv;
import com.luxof.lapisworks.chalk.RitualComponent;
import com.luxof.lapisworks.chalk.RitualExecutionState;
import com.luxof.lapisworks.client.screens.ChalkWithPatternScreenHandler;
import com.luxof.lapisworks.init.ModBlocks;

import static com.luxof.lapisworks.Lapisworks.LOGGER;

import java.util.ArrayList;
import java.util.List;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.screen.ScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import org.jetbrains.annotations.Nullable;

public class ChalkWithPatternEntity extends BlockEntity implements ExtendedScreenHandlerFactory, AttachedBE, RitualComponent {
    public ChalkWithPatternEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.CHALK_WITH_PATTERN_ENTITY_TYPE, pos, state);
    }
    public static final int MAXIMUM_PATTERNS = 5;

    public Direction renderPatternsInDir = Direction.NORTH;
    public Direction attachedTo = Direction.DOWN;
    /** is it attached to north-south instead of east-west? */
    public boolean rotated = false;
    public boolean powered = false;
    public boolean renderLeftBracket = true;
    public boolean renderRightBracket = true;
    public List<HexPattern> pats = new ArrayList<>();

    @Override
    public void readNbt(NbtCompound nbt) {
        renderPatternsInDir = Direction.byName(nbt.getString("renderPatternsInDir"));
        attachedTo = Direction.byName(nbt.getString("attachedTo"));
        rotated = nbt.getBoolean("rotated");
        powered = nbt.getBoolean("powered");
        renderLeftBracket = nbt.getBoolean("renderLeftBracket");
        renderRightBracket = nbt.getBoolean("renderRightBracket");

        pats.clear();
        int patCount = nbt.getInt("patCount");

        for (int i = 0; i < patCount; i++) {
            String idx = String.valueOf(i);
            if (!nbt.contains(idx)) {
                LOGGER.warn("NBT says it contains " + String.valueOf(patCount) + " but we found " + String.valueOf(i + 1) + "!");
                break;
            }

            pats.add(HexPattern.fromNBT(nbt.getCompound(idx)));
        }
    }
    @Override
    public void writeNbt(NbtCompound nbt) {
        nbt.putString("renderPatternsInDir", renderPatternsInDir.toString());
        nbt.putString("attachedTo", attachedTo.toString());
        nbt.putBoolean("rotated", rotated);
        nbt.putBoolean("powered", powered);
        nbt.putBoolean("renderLeftBracket", renderLeftBracket);
        nbt.putBoolean("renderRightBracket", renderRightBracket);

        nbt.putInt("patCount", pats.size());

        for (int i = 0; i < pats.size(); i++) {
            nbt.put(String.valueOf(i), pats.get(i).serializeToNBT());
        }
    }
    @Override @Nullable
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }
    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    @Override
    public ScreenHandler createMenu(int arg0, PlayerInventory arg1, PlayerEntity arg2) {
        renderPatternsInDir = arg2.getHorizontalFacing();
        save();
        return new ChalkWithPatternScreenHandler(arg0, pos);
    }
    @Override
    public Text getDisplayName() {
        return Text.translatable("gui.lapisworks.chalk_with_pattern.name");
    }
    @Override
    public void writeScreenOpeningData(ServerPlayerEntity player, PacketByteBuf buf) {
        buf.writeBlockPos(pos);
    }

    public void save() {
        markDirty();
        BlockState state = world.getBlockState(pos);
        world.updateListeners(pos, state, state, Block.NOTIFY_LISTENERS);
    }


    @Override
    public Direction getAttachedTo() { return attachedTo; }
    @Override
    public Direction getParticleSprayDir() { return getAttachedTo().getOpposite(); }


    @Override
    public List<BlockPos> getPossibleNextBlocks(ServerWorld world, @Nullable Direction forward) {
        return getPossibleNextBlocksGeneric(world, forward, pos);
    }
    @Override
    public Pair<BlockPos, CastingImage> execute(RitualCastEnv env) {
        RitualExecutionState ritual = env.ritual();
        CastingImage img = ritual.currentImage;
        ServerWorld world = env.getWorld();
        BlockPos next = getNextBlockDuringExecutionHelper(env);

        powered = true;
        save();

        if (pats.size() == 0) return new Pair<>(next, img);

        CastingVM vm = new CastingVM(img, env);
        ExecutionClientView result = vm.queueExecuteAndWrapIotas(
            pats.stream().map(PatternIota::new).toList(),
            world
        );
        if (result.getResolutionType().getSuccess())
            return new Pair<>(next, vm.getImage());
        else
            return null;
    }
    @Override
    public void unpower() {
        powered = false;
        save();
    }
    @Override
    public boolean executionCanFlowTo(ServerWorld world, BlockPos pos) {
        if (!(world.getBlockEntity(pos) instanceof AttachedBE chalk)) return true;
        return attachedTo == chalk.getAttachedTo();
    }
}
