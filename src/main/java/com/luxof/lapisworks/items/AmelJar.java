package com.luxof.lapisworks.items;

import at.petrak.hexcasting.api.utils.NBTHelper;

import com.luxof.lapisworks.init.ModItems;
import com.luxof.lapisworks.items.shit.InventoryItem;

import static com.luxof.lapisworks.Lapisworks.either;
import static com.luxof.lapisworks.Lapisworks.getAllHands;
import static com.luxof.lapisworks.init.Mutables.Mutables.isAmel;

import java.util.List;
import java.util.function.Predicate;

import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import org.jetbrains.annotations.Nullable;

public class AmelJar extends Item implements InventoryItem {
    private static final Settings defaultSettings = new Item.Settings().maxCount(1);
    public AmelJar(int maxAmel, boolean worksInHotbar) { super(defaultSettings); this.maxAmel = maxAmel; this.worksInHotbar = worksInHotbar; }
    public AmelJar(Settings settings, int maxAmel, boolean worksInHotbar) {super(settings); this.maxAmel = maxAmel; this.worksInHotbar = worksInHotbar; }

    private static final String STORED_AMEL = "amel_amount";
    public final int maxAmel;
    public final boolean worksInHotbar;

    public TypedActionResult<ItemStack> deposit(
        PlayerEntity user,
        Hand hand,
        ItemStack stack,
        Hand otherHand
    ) {
        ItemStack amelStack = user.getStackInHand(otherHand);

        if (!isAmel(amelStack) || amelStack.isEmpty())
            return TypedActionResult.fail(stack);

        int wouldBeStoredNow = getStored(stack) + amelStack.getCount();
        int shouldBeStoredNow = Math.min(maxAmel, wouldBeStoredNow);

        int difference = wouldBeStoredNow - shouldBeStoredNow;
        if (difference == 0) amelStack.copyAndEmpty();
        else amelStack.setCount(difference);

        setStored(
            stack,
            shouldBeStoredNow
        );

        return TypedActionResult.success(stack, true);
    }

    public TypedActionResult<ItemStack> withdraw(
        PlayerEntity user,
        Hand hand,
        ItemStack stack,
        Hand otherHand
    ) {
        ItemStack amelStack = user.getStackInHand(otherHand);

        if (!isAmel(amelStack) && !amelStack.isEmpty())
            return TypedActionResult.fail(stack);

        int wouldBeWithdrawn = amelStack.getMaxCount() - amelStack.getCount();
        int shouldBeWithdrawn = Math.min(getStored(amelStack), wouldBeWithdrawn);

        user.setStackInHand(otherHand, new ItemStack(
            amelStack.isEmpty() ? ModItems.AMEL_ITEM : amelStack.getItem(),
            amelStack.isEmpty() ? 0 : amelStack.getCount() + shouldBeWithdrawn
        ));

        setStored(
            stack,
            getStored(stack) - shouldBeWithdrawn
        );

        return TypedActionResult.success(stack, true);
    }

    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand) {
        List<Hand> hands = getAllHands();
        hands.remove(hand);
        Hand otherHand = hands.get(0);
        ItemStack stack = user.getStackInHand(hand);
        return user.isSneaking() ? withdraw(user, hand, stack, otherHand) : deposit(user, hand, stack, otherHand);
    }

    public int getStored(ItemStack stack) { return NBTHelper.getInt(stack, STORED_AMEL); }
    public void setStored(ItemStack stack, int count) { NBTHelper.putInt(stack, STORED_AMEL, count); }

    @Override
    public void appendTooltip(
        ItemStack stack,
        @Nullable World world,
        List<Text> components,
        TooltipContext flag
    ) {
        components.add(
            Text.translatable("tooltips.lapisworks.amel_jar.pre")
                .formatted(Formatting.DARK_PURPLE)
                .append(Text.literal(String.valueOf(this.getStored(stack))).formatted(Formatting.BLUE))
        );
    }

    @Override
    public int drain(ItemStack stack, Predicate<ItemStack> predicate, int amount, boolean simulate) {
        if (!this.predicateMatchesAmel(predicate)) return 0;

        int stored = this.getStored(stack);
        int taken = amount < 0 ? stored : Math.min(stored, amount);

        int remainingInStore = this.getStored(stack) - taken;
        if (!simulate) this.setStored(stack, remainingInStore);

        return taken;
    }
    @Override
    public int give(ItemStack stack, Predicate<ItemStack> predicate, int amount, boolean simulate) {
        if (!this.predicateMatchesAmel(predicate)) return 0;

        int leftUntilMax = this.maxAmel - this.getStored(stack);
        int given = amount < 0 ? leftUntilMax : Math.min(leftUntilMax, amount);

        int hasNow = this.getStored(stack) + given;
        if (!simulate) this.setStored(stack, hasNow);

        return given;
    }

    private boolean predicateMatchesAmel(Predicate<ItemStack> predicate) {
        return either(
            predicate,
            new ItemStack(ModItems.AMEL_ITEM),
            new ItemStack(ModItems.AMEL2_ITEM),
            new ItemStack(ModItems.AMEL3_ITEM),
            new ItemStack(ModItems.AMEL4_ITEM)
        );
    }
}
