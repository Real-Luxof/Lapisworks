package com.luxof.lapisworks.interop.hexical.blocks;

import at.petrak.hexcasting.common.items.magic.ItemMediaBattery;

import com.luxof.lapisworks.interop.hexical.Lapixical;
import com.luxof.lapisworks.interop.valkyrienskies.ValkyrienUtils;
import com.luxof.lapisworks.media.MediaTransferInterface;
import com.luxof.lapisworks.mixinsupport.ItemEntityMinterface;

import static com.luxof.lapisworks.Lapisworks.VALKYRIEN_SKIES_INTEROP;
import static com.luxof.lapisworks.Lapisworks.isInCradle;
import static com.luxof.lapisworks.Lapisworks.putInCradle;
import static com.luxof.lapisworks.Lapisworks.removeFromCradle;

import java.util.UUID;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

// there is so much jank here... please don't use this ever
// this shit is held together with hopes, dreams and bubblegum
public class CradleEntity extends BlockEntity implements Inventory, MediaTransferInterface {
    private ItemStack heldStack = ItemStack.EMPTY;
    public ItemStack getHeldStack() {
        if (heldEntity == null || heldEntity.isRemoved()) return ItemStack.EMPTY;
        return heldStack;
    }
    public void setHeldStack(ItemStack stack) {
        heldStack = stack;
        overrideItemEntity();
        save();
    }

    public ItemEntity heldEntity = null;
    private UUID persistentUUID = UUID.randomUUID();

    public CradleEntity(BlockPos pos, BlockState state) {
        super(Lapixical.CRADLE_ENTITY_TYPE, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, BlockEntity blockE) {
        CradleEntity bE = (CradleEntity)blockE;
        bE.heal();
		bE.updateItemStack();
		bE.configureItemEntity();
    }

    // this is such bullshit
    private void heal() {
        if (!(world instanceof ServerWorld sw)) return;
        if (heldEntity == null) {
            removeFromCradle(heldStack);
            Entity someEntity = sw.getEntity(persistentUUID);

            if (
                someEntity instanceof ItemEntity itemEnt &&
                isInCradle(itemEnt.getStack())
            ) {
                heldEntity = itemEnt;
                overrideItemEntity();
                save();
            } else if (someEntity != null) {
                someEntity.discard();
                persistentUUID = UUID.randomUUID();
                save();
            }
        }

        if (heldEntity == null || isInCradle(heldEntity.getStack())) return;
        // now i could just
        //putInCradle(heldEntity.getStack());
        // but that won't update the client
        heldStack = heldEntity.getStack();
        heldEntity.discard();
        heldEntity = null;
        overrideItemEntity();
    }

    public void updateItemStack() {
        // client never knows the item entity btw
        // i blame Mojang for not letting me use a FUCKING UUID on the client
        // like DAMN motherfucker JUST SYNC THAT SHIT
        // i feel weird about letting the client do this...
        // but eh what the fuck ever
        // if it ain't broken, don't fix it
        if (heldEntity == null || heldEntity.isRemoved()) {
            heldEntity = null;
            if (heldStack != ItemStack.EMPTY) {
                removeFromCradle(heldStack);
                heldStack = ItemStack.EMPTY;
                putInCradle(heldStack);
                save();
            }
        } else if (heldStack != heldEntity.getStack()) {
            removeFromCradle(heldStack); // because wristpocket
            heldStack = heldEntity.getStack();
            putInCradle(heldStack);
            save();
        }
    }

    public void configureItemEntity() {
        if (heldEntity == null) return;
        Vec3d pos = this.pos.toCenterPos();
        heldEntity.setPosition(pos);
        if (VALKYRIEN_SKIES_INTEROP)
            ValkyrienUtils.setEntityToShipyard(heldEntity, pos);
        heldEntity.noClip = true;
        heldEntity.setNeverDespawn();
        heldEntity.setNoGravity(true);
        heldEntity.setInvisible(false);
        heldEntity.setInvulnerable(true);
        heldEntity.setVelocity(Vec3d.ZERO);
        heldEntity.setPickupDelayInfinite();
        putInCradle(heldEntity.getStack());
        heldEntity.calculateDimensions();
    }

    public void overrideItemEntity() {
        if (heldEntity != null && !heldEntity.isRemoved()) {
            heldEntity.setStack(heldStack);
            return;
        }

        persistentUUID = UUID.randomUUID();
        Vec3d pos = Vec3d.ofCenter(this.pos);
        heldEntity = new ItemEntity(world, pos.x, pos.y, pos.z, heldStack);
        heldEntity.setUuid(persistentUUID);
        ((ItemEntityMinterface)heldEntity).setBlockPosOfCradle(this.pos);
        configureItemEntity();

        world.spawnEntity(heldEntity);
        save();
    }

    public void save() {
        markDirty();
        world.updateListeners(pos, getCachedState(), getCachedState(), Block.NOTIFY_ALL);
    }

    @Override
    public void readNbt(NbtCompound nbt) {
        super.readNbt(nbt);

        heldStack = ItemStack.fromNbt(nbt.getCompound("item"));
        this.persistentUUID = nbt.getUuid("persistent_uuid");
    }

    @Override
    public void writeNbt(NbtCompound nbt) {
        super.writeNbt(nbt);
        nbt.put("item", heldStack.writeNbt(new NbtCompound()));
        nbt.putUuid("persistent_uuid", persistentUUID);
    }

    @Override
    public BlockEntityUpdateS2CPacket toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt() {
        return createNbt();
    }

    @Override
    public void clear() {
        heldStack = ItemStack.EMPTY;
        updateItemStack();
        save();
    }

    @Override
    public boolean canPlayerUse(PlayerEntity player) { return false; }

    @Override
    public ItemStack getStack(int slot) { return slot == 0 ? getHeldStack() : ItemStack.EMPTY; }

    @Override
    public boolean isEmpty() { return heldStack.isEmpty(); }

    @Override
    public ItemStack removeStack(int slot) {
        if (slot != 0) return ItemStack.EMPTY;
        heldStack = ItemStack.EMPTY;
        updateItemStack();
        save();
        return heldStack;
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        if (slot != 0) return ItemStack.EMPTY;
        ItemStack removed = heldStack.split(amount);
        updateItemStack();
        save();
        return removed;
    }

    @Override
    public void setStack(int slot, ItemStack stack) { if (slot == 0) setHeldStack(stack); }

    @Override
    public int size() { return 1; }

    @Override public Vec3d getPosIfPossible() {
        return this.pos.toCenterPos();
    }
    @Override public void setMediaHere(long media) {
        if (getPhial() == null) return;
        phial.setMedia(heldStack, media);
    }
    @Override
    public long getMediaHere() {
        if (getPhial() == null) return 0;
        return phial.getMedia(heldStack);
    }
    @Override
    public long getMaxMedia() {
        if (getPhial() == null) return 0L;
        return phial.getMaxMedia(heldStack);
    }


    // DRY and less lines respectively
    @Nullable
    private ItemMediaBattery getPhial() {
        if (!(heldStack.getItem() instanceof ItemMediaBattery phial)) return null;
        this.phial = phial;
        return phial;
    }
    private ItemMediaBattery phial;
}
