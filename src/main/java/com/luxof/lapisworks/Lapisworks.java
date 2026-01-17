package com.luxof.lapisworks;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment.HeldItemInfo;
import at.petrak.hexcasting.api.casting.math.HexCoord;
import at.petrak.hexcasting.api.casting.math.HexDir;
import at.petrak.hexcasting.api.casting.math.HexPattern;
import at.petrak.hexcasting.api.pigment.FrozenPigment;
import at.petrak.hexcasting.api.utils.NBTHelper;
import at.petrak.hexcasting.common.lib.HexItems;

import com.luxof.lapisworks.blocks.stuff.LinkableMediaBlock;
import com.luxof.lapisworks.init.*;
import com.luxof.lapisworks.init.Mutables.Mutables;
import com.luxof.lapisworks.mixinsupport.EnchSentInterface;
import com.luxof.lapisworks.mixinsupport.GetStacks;

import static com.luxof.lapisworks.LapisworksIDs.INFUSED_AMEL;
import static com.luxof.lapisworks.LapisworksIDs.MAINHAND;
import static com.luxof.lapisworks.LapisworksIDs.OFFHAND;
import static com.luxof.lapisworks.init.ThemConfigFlags.allPerWorldShapePatterns;
import static com.luxof.lapisworks.init.ThemConfigFlags.chosenFlags;

import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketsApi;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.Stack;
import java.util.function.Function;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.Version;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.Util;

import org.jetbrains.annotations.Nullable;
import org.joml.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import vazkii.patchouli.api.PatchouliAPI;

// why is this project actually big?
public class Lapisworks implements ModInitializer {
	private static FrozenPigment BLACK_FP = new FrozenPigment(new ItemStack(HexItems.DYE_PIGMENTS.get(DyeColor.BLACK)), Util.NIL_UUID);
	private static FrozenPigment BROWN_FP = new FrozenPigment(new ItemStack(HexItems.DYE_PIGMENTS.get(DyeColor.BROWN)), Util.NIL_UUID);
	private static FrozenPigment BLUE_FP = new FrozenPigment(new ItemStack(HexItems.DYE_PIGMENTS.get(DyeColor.BLUE)), Util.NIL_UUID);
	private static FrozenPigment CYAN_FP = new FrozenPigment(new ItemStack(HexItems.DYE_PIGMENTS.get(DyeColor.CYAN)), Util.NIL_UUID);
	private static FrozenPigment GRAY_FP = new FrozenPigment(new ItemStack(HexItems.DYE_PIGMENTS.get(DyeColor.GRAY)), Util.NIL_UUID);
	private static FrozenPigment GREEN_FP = new FrozenPigment(new ItemStack(HexItems.DYE_PIGMENTS.get(DyeColor.GREEN)), Util.NIL_UUID);
	private static FrozenPigment LIGHT_BLUE_FP = new FrozenPigment(new ItemStack(HexItems.DYE_PIGMENTS.get(DyeColor.LIGHT_BLUE)), Util.NIL_UUID);
	private static FrozenPigment LIGHT_GRAY_FP = new FrozenPigment(new ItemStack(HexItems.DYE_PIGMENTS.get(DyeColor.LIGHT_GRAY)), Util.NIL_UUID);
	private static FrozenPigment LIME_FP = new FrozenPigment(new ItemStack(HexItems.DYE_PIGMENTS.get(DyeColor.LIME)), Util.NIL_UUID);
	private static FrozenPigment MAGENTA_FP = new FrozenPigment(new ItemStack(HexItems.DYE_PIGMENTS.get(DyeColor.MAGENTA)), Util.NIL_UUID);
	private static FrozenPigment ORANGE_FP = new FrozenPigment(new ItemStack(HexItems.DYE_PIGMENTS.get(DyeColor.ORANGE)), Util.NIL_UUID);
	private static FrozenPigment PINK_FP = new FrozenPigment(new ItemStack(HexItems.DYE_PIGMENTS.get(DyeColor.PINK)), Util.NIL_UUID);
	private static FrozenPigment PURPLE_FP = new FrozenPigment(new ItemStack(HexItems.DYE_PIGMENTS.get(DyeColor.PURPLE)), Util.NIL_UUID);
	private static FrozenPigment RED_FP = new FrozenPigment(new ItemStack(HexItems.DYE_PIGMENTS.get(DyeColor.RED)), Util.NIL_UUID);
	private static FrozenPigment WHITE_FP = new FrozenPigment(new ItemStack(HexItems.DYE_PIGMENTS.get(DyeColor.WHITE)), Util.NIL_UUID);
	private static FrozenPigment YELLOW_FP = new FrozenPigment(new ItemStack(HexItems.DYE_PIGMENTS.get(DyeColor.YELLOW)), Util.NIL_UUID);

	public static final String MOD_ID = "lapisworks";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static boolean HEXTENDED_INTEROP = false;
	public static boolean HEXICAL_INTEROP = false;
	public static boolean FULL_HEXICAL_INTEROP = false;
	public static boolean HEXAL_INTEROP = false;

	public static boolean isModLoaded(String modid) { return FabricLoader.getInstance().isModLoaded(modid); }
	/** assumes the mod is actually loaded and that <code>targetVersion</code> doesn't cause an error.
	 * Kurwa eksploduje if wrong? Nah, just gives <code>null</code>.
	 * If true? returns <code>current version - target version</code> */
	@Nullable
	public static Integer verDifference(String modid, String targetVersion) {
		try {
			Version currentVer = FabricLoader.getInstance().getModContainer(modid).get()
				.getMetadata().getVersion();
			Version targetVer = Version.parse(targetVersion);
			return currentVer.compareTo(targetVer);
		} catch (Exception e) {
			return null;
		}
	}

	@Override
	public void onInitialize() {
		boolean anyInterop = false;
        if (isModLoaded("hextended")) {
			HEXTENDED_INTEROP = true;
			anyInterop = true;
            com.luxof.lapisworks.interop.hextended.Lapixtended.initHextendedInterop();
        }
		if (isModLoaded("hexical")) {
			HEXICAL_INTEROP = true;
			anyInterop = true;
			com.luxof.lapisworks.interop.hexical.Lapixical.initHexicalInterop();
		}
		if (isModLoaded("hexal")) {
			HEXAL_INTEROP = true;
			anyInterop = true;
			com.luxof.lapisworks.interop.hexal.Lapisal.beCool();
		}

		ThemConfigFlags.declareEm();
		ModEntities.doSomethingFun();
		Patterns.init();
		ModItems.init_shit();
		LapisworksServer.lockIn();
		ModBlocks.wearASkirt();
		LapisworksLoot.gibLootexclamationmark();
		Mutables.innitBruv();
		ModPOIs.crawlOutOfHell();
		ModRecipes.apologizeForWarcrimes();
		ModScreens.whatWasThatTF2CommentAboutMakingBadGUICodeSoYouDontHaveToTouchItAgain();
		LapisParticles.pawtickle();

        LOGGER.info("Luxof's pet Lapisworks is getting a bit hyperactive.");
		LOGGER.info("\"Lapisworks! Lapis Lapis!\"");
		if (anyInterop) {
			// yknow, i would love to make the Interop category/entries unavailable until the mods
			// required exist but what if i keep it right there to garner curiosity and get people
			// to download other addons? prolly won't produce that big of an effect considering
			// Lapisworks isn't that popular rn but i'll make it be that way anyway, as a sign of
			// goodwill or sumn idfk i just felt like it
			//PatchouliAPI.get().setConfigFlag(
			//	"lapisworks:any_interop",
			//	true
			//)
			LOGGER.info("You have an addon that has interop with Lapisworks loaded?! Oh NOO, it's overstimulated, it's gonna throw up a bunch of content! Look what you've done!");
		} else LOGGER.info("Feed it redstone.");

		;
	}

	public static Identifier id(String string) {
		return new Identifier(MOD_ID, string);
	}

	public static boolean trinketEquipped(LivingEntity entity, Item item) {
		Optional<TrinketComponent> trinkCompOp = TrinketsApi.getTrinketComponent(entity);
		return trinkCompOp.isEmpty() ? false : trinkCompOp.get().isEquipped(item);
	}

	@Nullable
	public static Pair<SlotReference, ItemStack> getFirstTrinketIfEquipped(
		LivingEntity entity,
		Item item
	) {
		Optional<TrinketComponent> trinkCompOp = TrinketsApi.getTrinketComponent(entity);
		if (trinkCompOp.isEmpty()) return null;
		TrinketComponent trinkComp = trinkCompOp.get();
		try { return trinkComp.getEquipped(stack -> stack.isOf(item)).get(0); }
		catch (IndexOutOfBoundsException e) { return null; }
	}

	@Nullable
	public static FrozenPigment getPigmentFromDye(DyeColor dye) {
		// if I can't have Map to do it I'll get a function to do it
		switch (dye) {
			case BLACK: return BLACK_FP;
			case BROWN: return BROWN_FP;
			case BLUE: return BLUE_FP;
			case CYAN: return CYAN_FP;
			case GRAY: return GRAY_FP;
			case GREEN: return GREEN_FP;
			case LIGHT_BLUE: return LIGHT_BLUE_FP;
			case LIGHT_GRAY: return LIGHT_GRAY_FP;
			case LIME: return LIME_FP;
			case MAGENTA: return MAGENTA_FP;
			case ORANGE: return ORANGE_FP;
			case PINK: return PINK_FP;
			case PURPLE: return PURPLE_FP;
			case RED: return RED_FP;
			case WHITE: return WHITE_FP;
			case YELLOW: return YELLOW_FP;
			default: return null;
		}
	}

	@Nullable
	public static DyeColor getDyeFromPigment(FrozenPigment pigment) {
		// uncommon, that's my excuse
		if (pigment == BLACK_FP) { return DyeColor.BLACK; }
		else if (pigment == BROWN_FP) { return DyeColor.BROWN; }
		else if (pigment == BLUE_FP) { return DyeColor.BLUE; }
		else if (pigment == CYAN_FP) { return DyeColor.CYAN; }
		else if (pigment == GRAY_FP) { return DyeColor.GRAY; }
		else if (pigment == GREEN_FP) { return DyeColor.GREEN; }
		else if (pigment == LIGHT_BLUE_FP) { return DyeColor.LIGHT_BLUE; }
		else if (pigment == LIGHT_GRAY_FP) { return DyeColor.LIGHT_GRAY; }
		else if (pigment == LIME_FP) { return DyeColor.LIME; }
		else if (pigment == MAGENTA_FP) { return DyeColor.MAGENTA; }
		else if (pigment == ORANGE_FP) { return DyeColor.ORANGE; }
		else if (pigment == PINK_FP) { return DyeColor.PINK; }
		else if (pigment == PURPLE_FP) { return DyeColor.PURPLE; }
		else if (pigment == RED_FP) { return DyeColor.RED; }
		else if (pigment == WHITE_FP) { return DyeColor.WHITE; }
		else if (pigment == YELLOW_FP) { return DyeColor.YELLOW; }
		else { return null; }
	}

	public static double clamp(double num, double min, double max) { return Math.min(Math.max(num, min), max); }
	public static float clamp(float num, float min, float max) { return Math.min(Math.max(num, min), max); }

	/** Computes the seed that will be used to compute per-world pattern shapes from a world seed. */
	public static int pickUsingSeed(long seed) {
		// i'm trusting that org.joml.Random won't change and that java.util.Random will across Java versions
		// (should probably homebrew my own atp)
		Random rng = new Random(seed);
		int sendThisSeed = 0;
		for (int i = -1; i < seed % 13; i++) { // so no one can predict the world seed off this
			sendThisSeed = rng.nextInt(32767);
		}
		return sendThisSeed;
	}

	/** Computes the config flags and selects them for you. */
	public static void pickConfigFlags(int seed) {
		for (String patId : allPerWorldShapePatterns.keySet()) {
			int amountOfPatterns = allPerWorldShapePatterns.get(patId).size();
			// same seed used per pattern
			Random rng = new Random(
				new Random(seed % amountOfPatterns).nextInt(32767)
			);
			int chosen = rng.nextInt(32767) % amountOfPatterns;
			PatchouliAPI.get().setConfigFlag(
				patId + String.valueOf(chosen),
				true
			);
			chosenFlags.put(patId, chosen);
		}
	}

	/** Nulls the config flags for you. */
	public static void nullConfigFlags() {
		LOGGER.info("Nulling config flags.");
		for (String patId : allPerWorldShapePatterns.keySet()) {
			for (int i = 0; i < allPerWorldShapePatterns.get(patId).size(); i++) {
				PatchouliAPI.get().setConfigFlag(
					patId + String.valueOf(i),
					false
				);
			}
			chosenFlags.put(patId, null);
		}
	}

	/** truncates to first two digits after the dot. I use this for Simple Mind Containers' scryglass info. */
	public static String prettifyFloat(float value) {
		// val % 0.01 flickers sometimes
		return String.valueOf(Math.floor((double)value * 100.0) / 100.0);
	}
	/** truncates to first two digits after the dot. */
	public static double prettifyDouble(double value) {
		return Math.floor(value * 100.0) / 100.0;
	}
	/** truncates all components to first 2 digits after the dot. */
	public static Vec3d prettifyVec3d(Vec3d vec) {
		return new Vec3d(
			prettifyDouble(vec.x),
			prettifyDouble(vec.y),
			prettifyDouble(vec.z)
		);
	}

	public static boolean matchShape(HexPattern pat1, HexPattern pat2) {
		// rat said that if you record how many times a position is drawn over then it's fine
		// they waren't too sure, but i pray they're right because nothing else i've done has worked
		List<HexCoord> pat2Positions = pat2.positions();
		for (HexDir dir : HexDir.values()) {
			if (equalsButUnordered(
				setTopLeftOrigin(new HexPattern(dir, pat1.getAngles()).positions()),
				setTopLeftOrigin(pat2Positions)
			)) return true;
		}
		return false;
	}

	public static List<HexCoord> setTopLeftOrigin(List<HexCoord> pat) {
		HexCoord runningTopLeft = new HexCoord(0, 0);
		for (HexCoord coord : pat) {

			if (coord.getQ() < runningTopLeft.getQ() && coord.getR() <= runningTopLeft.getR()) {
				runningTopLeft = new HexCoord(coord.getQ(), coord.getR());

			} else if (coord.getR() < runningTopLeft.getR()) {
				runningTopLeft = new HexCoord(coord.getQ(), coord.getR());

			}
		}
		// "must be effectively final" my fucking ass! fuck off!
		HexCoord topLeft = new HexCoord(runningTopLeft.getQ(), runningTopLeft.getR());

        return pat.stream().map((coord) -> {
            return new HexCoord(coord.getQ() - topLeft.getQ(), coord.getR() - topLeft.getR());
        }).toList();
	}

    /** Checks if two lists are equal, but does not check if their elements are ordered the same way. */
    public static <T extends Object> boolean equalsButUnordered(List<T> list1, List<T> list2) {
        if (list1.size() != list2.size()) { return false; }
        else if (list1.size() == 0) { return true; }

        List<T> workingOn = new ArrayList<>(list2);
        for (T thing : list1) {
            int idx = workingOn.indexOf(thing);
            if (idx == -1) { return false; }
            workingOn.remove(idx);
        }
        return true;
    }

	public static boolean closeEnough(float a, float b, float epsilon) {
		return Math.abs(b - a) < epsilon;
	}
	public static boolean closeEnough(double a, double b, double epsilon) {
		return Math.abs(b - a) < epsilon;
	}
	public static boolean closeEnough(Vec3d a, Vec3d b, double epsilon) {
		return closeEnough(a.x, b.x, epsilon)
			&& closeEnough(a.y, b.y, epsilon)
			&& closeEnough(a.z, b.z, epsilon);
	}

    /** returns null if hand isn't MAIN_HAND or OFF_HAND or inaccessible (i'll add more eventually..!!) */
    @Nullable
    public static ItemStack getStackFromHand(CastingEnvironment ctx, int hand) {
        List<HeldItemInfo> stacks = ((GetStacks)ctx).getHeldStacks();
        try { return stacks.get(hand).stack(); }
		catch (IndexOutOfBoundsException e) {
			LOGGER.info("Someone tried to access idx " + hand + " of " + stacks.toString() + ".");
			return null;
		}
    }

	/** returns null if resulting Hand can't be MAIN_HAND or OFF_HAND (MORE WILL COME, THEE SHALL KNOW) */
	@Nullable
	public static Hand intToHand(int hand) {
		switch (hand) {
			case 0: return Hand.MAIN_HAND;
			case 1: return Hand.OFF_HAND;
			default: return null;
		}
	}

	/** Returns stuff like Text.translateable("hands.lapisworks.43") (43rd hand)
	 * if it doesn't know wtf that Hand is */
	public static Text handToString(Hand hand) {
		if (hand == Hand.MAIN_HAND) { return MAINHAND; }
		else if (hand == Hand.OFF_HAND) { return OFFHAND; }
		return Text.translatable("hands.lapisworks." + hand.ordinal());
	}

	/** Will update when the third and fourth hands expansion comes out fr */
	public static List<Hand> getAllHands() {
		return new ArrayList<>(List.of(Hand.MAIN_HAND, Hand.OFF_HAND));
	}

	public static boolean hasInfusedAmel(ItemStack stack) {
		return NBTHelper.contains(stack, INFUSED_AMEL);
	}

	public static int getInfusedAmel(ItemStack stack) {
		return NBTHelper.getInt(stack, INFUSED_AMEL, 0);
	}

	public static void setInfusedAmel(ItemStack stack, int count) {
		NBTHelper.putInt(stack, INFUSED_AMEL, count);
	}

    public static FrozenPigment getRandomPigment(net.minecraft.util.math.random.Random rng) {
        return new FrozenPigment(
            new ItemStack(
                HexItems.DYE_PIGMENTS.values().stream().toList()
                    .get(rng.nextInt(HexItems.DYE_PIGMENTS.size()))
            ),
            Util.NIL_UUID
        );
    }

	/** why is there no native method to do this? */
	public static int dot(Vec3i a, Vec3i b) {
		return a.getX()*b.getX() + a.getY()*b.getY() + a.getZ()*b.getZ();
	}

	/** convenience. */
	public static int dot(Direction a, Direction b) {
		return dot(a.getVector(), b.getVector());
	}
    public static Vec3d getBiggestAxis(Vec3d vector){
        var dX = Math.abs(vector.x);
        var dY = Math.abs(vector.y);
        var dZ = Math.abs(vector.z);
        if (dX>dY && dX>dZ) return new Vec3d(vector.x, 0, 0);
        else if (dY>dZ)     return new Vec3d(0, vector.y, 0);
        else                return new Vec3d(0, 0, vector.z);
    }


	/** returns a list of all (mapped) positions between <code>start</code> and <code>end</code>,
	 * and a boolean which states if the raycast was interrupted suddenly instead of completing.
	 * <p>to skip a pos in the final list, return <code>null</code> for the pos.
	 * If you send a valid pos instead, it will be added to the final list.
	 * <p>to terminate the line, simply return <code>false</code> for the boolean.
	 * <p>P.S. this prioritizes accuracy and as such increments from start to end in steps of 0.1.
	 * of course, all the positions passed to the function are unique, however you may not want to
	 * use this if you value performance. */
	public static Pair<List<BlockPos>, Boolean> castRay(
		BlockPos startPos,
		BlockPos endPos,
		Function<BlockPos, Pair<BlockPos, Boolean>> atEachStep
	) {
		Vec3d start = startPos.toCenterPos();
		Vec3d end = endPos.toCenterPos();
		List<BlockPos> positions = new ArrayList<>();

        Vec3d delta = end.subtract(start).normalize();
        long steps = (long) Math.ceil(getBiggestAxis(delta).length());
        Vec3d offset = delta.multiply(1d /steps);

        Vec3d curr = start;
        for (long step = 0; step < steps; step++) { //ENSURE it does terminate, also don't spam distance checks
            curr = curr.add(offset); //Minecraft's Vec3d operations return a copy

            BlockPos currBlockPos = BlockPos.ofFloored(curr);
            Pair<BlockPos, Boolean> ret = atEachStep.apply(currBlockPos); //Some shenanigans
            if (!ret.getRight()) return new Pair<>(positions, true);
            if (ret.getLeft() != null) positions.add(ret.getLeft());
        }
		return new Pair<>(positions, false);
	}

	private static String getPotion(ItemStack stack) {
		NbtCompound comp = stack.getNbt();
		if (comp == null) return "";
		return comp.getString("Potion");
	}
	/** works in case of no potion too. */
	public static boolean potionEquals(ItemStack stack1, ItemStack stack2) {
		return getPotion(stack1).equals(getPotion(stack2));
	}
	public static boolean potionEquals(ItemStack stack, String potId) {
		return getPotion(stack).equals(potId);
	}
	// convenience
	public static boolean potionEquals(String potId, ItemStack stack) {
		return potionEquals(stack, potId);
	}

	/** takes links into account.
	 * <br>returns what was deposited/withdrawn first, and a set of all involved linkables second. */
	public static Pair<Long, Set<BlockPos>> fullLinkableMediaBlocksInteraction(
		ServerWorld world,
		Set<BlockPos> first,
		long amountToInteract,
		boolean deposit,
		boolean simulate
	) {
		long interactionLeft = amountToInteract;
		Stack<BlockPos> todo = new Stack<>();
		
		HashSet<BlockPos> seen = new HashSet<>();

		seen.addAll(first);
		todo.addAll(first);

		while (!todo.isEmpty()) {
			BlockPos currPos = todo.pop();
			LinkableMediaBlock curr = (LinkableMediaBlock)world.getBlockEntity(currPos);

			if (deposit) interactionLeft -= curr.depositMedia(interactionLeft, simulate);
			else interactionLeft -= curr.withdrawMedia(interactionLeft, simulate);
			if (interactionLeft == 0) return new Pair<>(amountToInteract, seen);

			for (BlockPos linked : curr.getLinks()) { if (seen.add(linked)) todo.add(linked); }
		}

		return new Pair<>(amountToInteract - interactionLeft, seen);
	}

	/** does not simulate. */
	public static long interactWithLinkableMediaBlocks(
		ServerWorld world,
		Set<BlockPos> first,
		long amountToInteract,
		boolean deposit
	) {
		return interactWithLinkableMediaBlocks(world, first, amountToInteract, deposit, false);
	}
	/** takes into account links. returns what was deposited/withdrawn. */
	public static long interactWithLinkableMediaBlocks(
		ServerWorld world,
		Set<BlockPos> first,
		long amountToInteract,
		boolean deposit,
		boolean simulate
	) {
		return fullLinkableMediaBlocksInteraction(world, first, amountToInteract, deposit, simulate)
			.getLeft();
	}

	public static double getDistance(BlockPos pos1, BlockPos pos2) {
		return Math.sqrt(pos2.getSquaredDistance(pos1));
	}

	public static boolean _shouldBreakSent(LivingEntity plr) {
		EnchSentInterface eSentInterface = (EnchSentInterface)plr;
		return eSentInterface.getEnchantedSentinel() == null ?
			false :
			plr.getPos().squaredDistanceTo(eSentInterface.getEnchantedSentinel()) > 32.0*32.0;
	}

	public static boolean testEmiIngredient(EmiIngredient ingredient, Item item) {
		for (EmiStack stack : ingredient.getEmiStacks()) {
			if (stack.getItemStack().isOf(item)) return true;
		}
		return false;
	}

	public static NbtCompound serializeBlockPos(BlockPos pos) {
		NbtCompound nbt = new NbtCompound();
		nbt.putInt("x", pos.getX());
		nbt.putInt("y", pos.getY());
		nbt.putInt("z", pos.getZ());
		return nbt;
	}
	/** deserializes a blockpos that was serialized by <code>serializeBlockPos</code>. */
	public static BlockPos deserializeBlockPos(NbtCompound nbt) {
		return new BlockPos(
			nbt.getInt("x"),
			nbt.getInt("y"),
			nbt.getInt("z")
		);
	}
	/** deserializes a blockpos that was serialized by <code>serializeBlockPos</code>. */
	public static BlockPos deserializeBlockPos(NbtElement nbt) {
		return deserializeBlockPos((NbtCompound)nbt);
	}
    public static NbtList nbtListOf(List<? extends NbtElement> list) {
        NbtList nbtList = new NbtList();
        nbtList.addAll(list);
        return nbtList;
    }
}
