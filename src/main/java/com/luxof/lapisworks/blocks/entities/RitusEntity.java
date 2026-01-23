package com.luxof.lapisworks.blocks.entities;

import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.iota.IotaType;

import com.luxof.lapisworks.blocks.Ritus;
import com.luxof.lapisworks.blocks.stuff.AttachedBE;
import com.luxof.lapisworks.blocks.stuff.UnlinkableMediaBlock;
import com.luxof.lapisworks.chalk.MultiUseRitualExecutionState;
import com.luxof.lapisworks.chalk.RitualCastEnv;
import com.luxof.lapisworks.chalk.RitualComponent;
import com.luxof.lapisworks.init.ModBlocks;

import static com.luxof.lapisworks.Lapisworks.nbtListOf;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;

import org.jetbrains.annotations.Nullable;

public class RitusEntity extends BlockEntity implements AttachedBE, RitualComponent, UnlinkableMediaBlock {

    public RitusEntity(BlockPos pos, BlockState state) {
        super(ModBlocks.RITUS_ENTITY_TYPE, pos, state);
    }

    /** server no exist when call readNbt() first time :pensive: */
    private ArrayList<NbtCompound> ritualsLazy = new ArrayList<>();
    private ArrayList<MultiUseRitualExecutionState> rituals = new ArrayList<>();

    public long media = 0L;
    @Nullable private NbtCompound tunedFrequency = null;
    @Nullable private ItemStack displayItem = null;
    @Nullable private Text displayMessage = null;

    /** do not call if world is client or null. */
    public List<MultiUseRitualExecutionState> getRituals() {
        if (world == null)
            throw new IllegalStateException("Don't use this method if world is null.");
        else if (world.isClient)
            throw new IllegalStateException("Don't use this method if world is client.");

        if (rituals.size() > 0) return rituals;

        rituals = new ArrayList<>(
            ritualsLazy.stream()
                .map(
                    (NbtElement ritualNbt) -> MultiUseRitualExecutionState.load(
                        (NbtCompound)ritualNbt,
                        (ServerWorld)world
                    )
                )
                .toList()
        );
        ritualsLazy = new ArrayList<>();

        return rituals;
    }
    /** returns success. the intended method. */
    public boolean addRitual(MultiUseRitualExecutionState ritual) {
        // in the future i may wanna add multiple ritual capacity
        var rituals = getRituals();
        if (rituals.size() >= 1) return false;
        rituals.add(ritual);
        save();
        return true;
    }

    @Nullable
    public Iota getTunedFrequency(ServerWorld world) {
        if (tunedFrequency == null) return null;
        return IotaType.deserialize(tunedFrequency, world);
    }

    @Nullable
    public Text getTunedFrequencyDisplay() {
        if (tunedFrequency == null) return null;
        return IotaType.getDisplay(tunedFrequency);
    }

    public void setTunedFrequency(Iota iota) {
        tunedFrequency = IotaType.serialize(iota);
    }

    @Nullable
    public Pair<ItemStack, Text> getDisplay() {
        if (displayItem != null && displayMessage != null)
            return new Pair<>(displayItem, displayMessage);
        else
            return null;
    }

    public void postDisplay(ItemStack item, Text message) {
        this.displayItem = item;
        this.displayMessage = message;
        save();
    }

    public void postMishap(Text mishapDisplay) {
        this.postDisplay(new ItemStack(Items.MUSIC_DISC_11), mishapDisplay);
    }

    public void postPrint(Text message) {
        this.postDisplay(new ItemStack(Items.BOOK), message);
    }

    public void clearDisplay() {
        this.postDisplay(null, null);
    }

    public void save() {
        markDirty();
        world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_LISTENERS);
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.putLong("media", media);
        if (displayItem != null)
            nbt.put("displayItem", displayItem.writeNbt(new NbtCompound()));
        if (displayMessage != null)
            nbt.putString("displayMessage", Text.Serializer.toJson(displayMessage));
        if (tunedFrequency != null)
            nbt.put("tunedFrequency", tunedFrequency);

        if (rituals.size() == 0) {
            nbt.put("rituals", nbtListOf(ritualsLazy));
            return;
        }
        nbt.put(
            "rituals",
            nbtListOf(rituals.stream().map(ritual -> ritual.save()).toList())
        );
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);
        media = nbt.getLong("media");
        if (nbt.contains("displayItem"))
            displayItem = ItemStack.fromNbt(nbt.getCompound("displayItem"));
        else
            displayItem = null;
        if (nbt.contains("displayMessage"))
            displayMessage = Text.Serializer.fromJson(nbt.getString("displayMessage"));
        else
            displayMessage = null;
        if (nbt.contains("tunedFrequency"))
            tunedFrequency = nbt.getCompound("tunedFrequency");
        else
            tunedFrequency = null;

        ritualsLazy = new ArrayList<>(
            nbt.getList("rituals", NbtElement.COMPOUND_TYPE).stream()
                .map(ele -> (NbtCompound)ele)
                .toList()
        );
    }

    @Override @Nullable public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this); }
    @Override public NbtCompound toInitialChunkDataNbt() { return createNbt(); }

    private boolean isEveryOtherTick = false;
    public void tick(BlockState state) {
        if (!isEveryOtherTick) {
            isEveryOtherTick = true;
            return;
        } else {
            isEveryOtherTick = false;
        }
        // why the fuck didn't this work?
        /*LOGGER.info("Ticking ritus. " + String.valueOf(tickCountdown));
        if (tickCountdown > 0) {
            tickCountdown--;
            return;
        } else
            tickCountdown = tickInterval;*/

        if (!(world instanceof ServerWorld sw)) return;

        var rituals = getRituals();

        for (int i = rituals.size() - 1; i >= 0; i--) {
            if (!rituals.get(i).tick(sw))
                rituals.remove(i);
            save();
        }
    }

    @Override
    public Direction getAttachedTo() {
        return world.getBlockState(pos).get(Ritus.ATTACHED);
    }
    @Override
    public Direction getParticleSprayDir() { return getAttachedTo().getOpposite(); }

    @Override
    public List<BlockPos> getPossibleNextBlocks(ServerWorld world, @Nullable Direction forward) {
        return getPossibleNextBlocksGeneric(world, forward, pos);
    }

    @Override
    public @Nullable Pair<BlockPos, CastingImage> execute(RitualCastEnv env) {
        world.setBlockState(
            pos,
            world.getBlockState(pos).with(Ritus.POWERED, true)
        );

        return new Pair<>(
            getNextBlockDuringExecutionHelper(env),
            env.ritual().currentImage
        );
    }

    @Override
    public void unpower() {
        world.setBlockState(
            pos,
            world.getBlockState(pos).with(Ritus.POWERED, false)
        );
    }

    @Override
    public boolean executionCanFlowTo(ServerWorld world, BlockPos pos) {
        if (!(world.getBlockEntity(pos) instanceof AttachedBE attached)) return true;
        return attached.getAttachedTo() == getAttachedTo();
    }


    @Override public BlockPos getThisPos() { return pos; }
    @Override public void setMedia(long media) { this.media = media; save(); }
    @Override public long getMediaHere() { return media; }
}
