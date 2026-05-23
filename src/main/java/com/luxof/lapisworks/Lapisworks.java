package com.luxof.lapisworks;

import at.petrak.hexcasting.api.casting.ActionRegistryEntry;
import at.petrak.hexcasting.api.casting.PatternShapeMatch;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.CastingEnvironment.HeldItemInfo;
import at.petrak.hexcasting.api.casting.eval.vm.CastingImage;
import at.petrak.hexcasting.api.casting.eval.vm.ContinuationFrame;
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation;
import at.petrak.hexcasting.api.casting.eval.vm.SpellContinuation.NotDone;
import at.petrak.hexcasting.api.casting.iota.Iota;
import at.petrak.hexcasting.api.casting.math.HexCoord;
import at.petrak.hexcasting.api.casting.math.HexDir;
import at.petrak.hexcasting.api.casting.math.HexPattern;
import at.petrak.hexcasting.api.pigment.FrozenPigment;
import at.petrak.hexcasting.api.utils.HexUtils;
import at.petrak.hexcasting.api.utils.NBTHelper;
import at.petrak.hexcasting.common.lib.HexItems;
import at.petrak.hexcasting.common.particles.ConjureParticleOptions;
import at.petrak.hexcasting.xplat.IXplatAbstractions;

import com.google.gson.JsonPrimitive;

import com.luxof.lapisworks.init.LapisConfig;
import com.luxof.lapisworks.init.LapisParticles;
import com.luxof.lapisworks.init.LapisResourceCons;
import com.luxof.lapisworks.init.LapisSounds;
import com.luxof.lapisworks.init.LapisTrinkets;
import com.luxof.lapisworks.init.LapisworksLoot;
import com.luxof.lapisworks.init.ModBlocks;
import com.luxof.lapisworks.init.ModEntities;
import com.luxof.lapisworks.init.ModEvents;
import com.luxof.lapisworks.init.ModItems;
import com.luxof.lapisworks.init.ModPOIs;
import com.luxof.lapisworks.init.ModRecipes;
import com.luxof.lapisworks.init.ModScreens;
import com.luxof.lapisworks.init.Patterns;
import com.luxof.lapisworks.init.ThemConfigFlags;
import com.luxof.lapisworks.init.Mutables.Mutables;
import com.luxof.lapisworks.items.shit.ITotem;
import com.luxof.lapisworks.interop.hexal.Lapisal;
import com.luxof.lapisworks.interop.hexical.Lapixical;
import com.luxof.lapisworks.interop.hextended.Lapixtended;
import com.luxof.lapisworks.interop.hierophantics.Chariot;
import com.luxof.lapisworks.media.LinkableMediaBlock;
import com.luxof.lapisworks.mixinsupport.GetStacks;
import com.mojang.datafixers.util.Either;

import static com.luxof.lapisworks.LapisworksIDs.CANNOT_MODIFY_COST_TAG;
import static com.luxof.lapisworks.LapisworksIDs.GRAND_RITUAL_BLACKLIST_TAG;
import static com.luxof.lapisworks.LapisworksIDs.INFUSED_AMEL;
import static com.luxof.lapisworks.LapisworksIDs.IS_IN_CRADLE;
import static com.luxof.lapisworks.LapisworksIDs.MAINHAND;
import static com.luxof.lapisworks.LapisworksIDs.OFFHAND;
import static com.luxof.lapisworks.LapisworksIDs.TOTEM_TAG;
import static com.luxof.lapisworks.init.ThemConfigFlags.allPerWorldShapePatterns;
import static com.luxof.lapisworks.init.ThemConfigFlags.chosenFlags;

import dev.emi.emi.api.stack.EmiIngredient;
import dev.emi.emi.api.stack.EmiStack;
import dev.emi.trinkets.api.SlotReference;
import dev.emi.trinkets.api.TrinketComponent;
import dev.emi.trinkets.api.TrinketInventory;
import dev.emi.trinkets.api.TrinketsApi;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.Stack;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import org.jetbrains.annotations.Nullable;
import org.joml.Quaternionf;
import org.joml.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.Version;

import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.text.Text;
import net.minecraft.util.DyeColor;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Pair;
import net.minecraft.util.Util;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.RotationAxis;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;

import vazkii.patchouli.api.PatchouliAPI;

// why is this project actually big?
public class Lapisworks implements ModInitializer {
	private static final FrozenPigment BLACK_FP = new FrozenPigment(new ItemStack(HexItems.DYE_PIGMENTS.get(DyeColor.BLACK)), Util.NIL_UUID);
	private static final FrozenPigment BROWN_FP = new FrozenPigment(new ItemStack(HexItems.DYE_PIGMENTS.get(DyeColor.BROWN)), Util.NIL_UUID);
	private static final FrozenPigment BLUE_FP = new FrozenPigment(new ItemStack(HexItems.DYE_PIGMENTS.get(DyeColor.BLUE)), Util.NIL_UUID);
	private static final FrozenPigment CYAN_FP = new FrozenPigment(new ItemStack(HexItems.DYE_PIGMENTS.get(DyeColor.CYAN)), Util.NIL_UUID);
	private static final FrozenPigment GRAY_FP = new FrozenPigment(new ItemStack(HexItems.DYE_PIGMENTS.get(DyeColor.GRAY)), Util.NIL_UUID);
	private static final FrozenPigment GREEN_FP = new FrozenPigment(new ItemStack(HexItems.DYE_PIGMENTS.get(DyeColor.GREEN)), Util.NIL_UUID);
	private static final FrozenPigment LIGHT_BLUE_FP = new FrozenPigment(new ItemStack(HexItems.DYE_PIGMENTS.get(DyeColor.LIGHT_BLUE)), Util.NIL_UUID);
	private static final FrozenPigment LIGHT_GRAY_FP = new FrozenPigment(new ItemStack(HexItems.DYE_PIGMENTS.get(DyeColor.LIGHT_GRAY)), Util.NIL_UUID);
	private static final FrozenPigment LIME_FP = new FrozenPigment(new ItemStack(HexItems.DYE_PIGMENTS.get(DyeColor.LIME)), Util.NIL_UUID);
	private static final FrozenPigment MAGENTA_FP = new FrozenPigment(new ItemStack(HexItems.DYE_PIGMENTS.get(DyeColor.MAGENTA)), Util.NIL_UUID);
	private static final FrozenPigment ORANGE_FP = new FrozenPigment(new ItemStack(HexItems.DYE_PIGMENTS.get(DyeColor.ORANGE)), Util.NIL_UUID);
	private static final FrozenPigment PINK_FP = new FrozenPigment(new ItemStack(HexItems.DYE_PIGMENTS.get(DyeColor.PINK)), Util.NIL_UUID);
	private static final FrozenPigment PURPLE_FP = new FrozenPigment(new ItemStack(HexItems.DYE_PIGMENTS.get(DyeColor.PURPLE)), Util.NIL_UUID);
	private static final FrozenPigment RED_FP = new FrozenPigment(new ItemStack(HexItems.DYE_PIGMENTS.get(DyeColor.RED)), Util.NIL_UUID);
	private static final FrozenPigment WHITE_FP = new FrozenPigment(new ItemStack(HexItems.DYE_PIGMENTS.get(DyeColor.WHITE)), Util.NIL_UUID);
	private static final FrozenPigment YELLOW_FP = new FrozenPigment(new ItemStack(HexItems.DYE_PIGMENTS.get(DyeColor.YELLOW)), Util.NIL_UUID);

	public static final String MOD_ID = "lapisworks";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	public static boolean HEXTENDED_INTEROP = false;
	public static boolean HEXICAL_INTEROP = false;
	public static boolean FULL_HEXICAL_INTEROP = false;
	public static boolean HEXAL_INTEROP = false;
	public static boolean HIEROPHANTICS_INTEROP = false;
	public static boolean ONEIRONAUT_INTEROP = false;
	public static boolean VALKYRIEN_SKIES_INTEROP = false;
	public static boolean HEXCESSIBLE_INTEROP = false;

	public static String fmt(String string, Object... args) {
		return String.format(string, args);
	}
	public static void log(String text, Object... args) {
		LOGGER.info(fmt(text, args));
	}
	public static void warn(String text, Object... args) {
		LOGGER.warn(fmt(text, args));
	}
	public static void err(String text, Object... args) {
		LOGGER.error(fmt(text, args));
	}
	public static boolean isModLoaded(String modid) {
		return FabricLoader.getInstance().isModLoaded(modid);
	}
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
			Lapixtended.initHextendedInterop();
        }
		if (isModLoaded("hexical")) {
			HEXICAL_INTEROP = true;
			anyInterop = true;
			Lapixical.initHexicalInterop();
		}
		if (isModLoaded("hexal")) {
			HEXAL_INTEROP = true;
			anyInterop = true;
			Lapisal.beCool();
		}
		if (isModLoaded("hierophantics")) {
			HIEROPHANTICS_INTEROP = true;
			anyInterop = true;
			Chariot.readTarotCards();
		}
		if (isModLoaded("oneironaut")) {
			ONEIRONAUT_INTEROP = true;
			//anyInterop = true;
		}
		if (isModLoaded("valkyrienskies")) {
			VALKYRIEN_SKIES_INTEROP = true;
			//anyInterop = true;
		}
		if (isModLoaded("hexcessible")) {
			HEXCESSIBLE_INTEROP = true;
			//anyInterop = true;
		}

		LapisResourceCons.doBondagePlay();
		LapisConfig.renewCurrentConfig();
		LapisSounds.imagineArfingCouldntBeMe();
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
		ModEvents.finallyBeUsed();
		LapisTrinkets.startFeelingCute();

        TheSillySayer.sayNormal();
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
			TheSillySayer.sayInterop();
		} else TheSillySayer.sayNoInterop();
	}

	public static Identifier id(String string) {
		return new Identifier(MOD_ID, string);
	}

	public static JsonPrimitive primitive(Number number) {
		return new JsonPrimitive(number);
	}
	public static JsonPrimitive primitive(Boolean bool) {
		return new JsonPrimitive(bool);
	}
	public static JsonPrimitive primitive(String string) {
		return new JsonPrimitive(string);
	}
	public static <T1 extends Object, T2 extends Object> Pair<T1, T2> pair(T1 one, T2 two) {
		return new Pair<>(one, two);
	}

	public static List<ItemStack> getEquippedTrinketsIn(
		LivingEntity entity,
		String broad,
		String specific
	) {
		TrinketComponent trinkComp = TrinketsApi.getTrinketComponent(entity).orElse(null);
		if (trinkComp == null) return null;

		TrinketInventory inv = trinkComp.getInventory().get(broad).get(specific);

		List<ItemStack> trinkets = new ArrayList<>();
		for (int i = 0; i < inv.size(); i++) { trinkets.add(inv.getStack(i)); }
		return trinkets;
	}

	public static boolean trinketEquipped(LivingEntity entity, Item item) {
		TrinketComponent trinkComp = TrinketsApi.getTrinketComponent(entity).orElse(null);
		return trinkComp != null && trinkComp.isEquipped(item);
	}

	@Nullable
	public static Pair<SlotReference, ItemStack> getFirstTrinketIfEquipped(
		LivingEntity entity,
		Item item
	) {
		TrinketComponent trinkComp = TrinketsApi.getTrinketComponent(entity).orElse(null);
		if (trinkComp == null) return null;
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
		if (pigment.equals(BLACK_FP)) return DyeColor.BLACK;
		else if (pigment.equals(BROWN_FP)) return DyeColor.BROWN;
		else if (pigment.equals(BLUE_FP)) return DyeColor.BLUE;
		else if (pigment.equals(CYAN_FP)) return DyeColor.CYAN;
		else if (pigment.equals(GRAY_FP)) return DyeColor.GRAY;
		else if (pigment.equals(GREEN_FP)) return DyeColor.GREEN;
		else if (pigment.equals(LIGHT_BLUE_FP)) return DyeColor.LIGHT_BLUE;
		else if (pigment.equals(LIGHT_GRAY_FP)) return DyeColor.LIGHT_GRAY;
		else if (pigment.equals(LIME_FP)) return DyeColor.LIME;
		else if (pigment.equals(MAGENTA_FP)) return DyeColor.MAGENTA;
		else if (pigment.equals(ORANGE_FP)) return DyeColor.ORANGE;
		else if (pigment.equals(PINK_FP)) return DyeColor.PINK;
		else if (pigment.equals(PURPLE_FP)) return DyeColor.PURPLE;
		else if (pigment.equals(RED_FP)) return DyeColor.RED;
		else if (pigment.equals(WHITE_FP)) return DyeColor.WHITE;
		else if (pigment.equals(YELLOW_FP)) return DyeColor.YELLOW;
		else return null;
	}

	public static int clamp(int num, int min, int max) { return Math.min(Math.max(num, min), max); }
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
		log("Nulling config flags.");
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
	/** turns a list to "(element1, element2, element3)". */
	public static String prettifyTuple(Collection<? extends Object> tuple) {
		String ret = "";
		for (Object ele : tuple) { ret += ", " + ele.toString(); }
		return "(" + ret.substring(2) + ")";
	}
	/** turns an array to "(element1, element2, element3)". */
	public static String prettifyTuple(Object[] tuple) {
		String ret = "";
		for (Object ele : tuple) { ret += ", " + ele.toString(); }
		return "(" + ret.substring(2) + ")";
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
		// they weren't too sure, but i pray they're right because nothing else i've done has worked
		List<HexCoord> pat2Positions = getPosesDrawnOver(pat2);
		for (HexDir dir : HexDir.values()) {
			if (equalsButUnordered(
				setTopLeftOrigin(getPosesDrawnOver(new HexPattern(dir, pat1.getAngles()))),
				setTopLeftOrigin(pat2Positions)
			)) return true;
		}
		return false;
	}

	public static List<HexCoord> getPosesDrawnOver(HexPattern pat) {
		HexCoord cursor = new HexCoord(0, 0);
		List<HexCoord> positions = new ArrayList<>();

		for (HexDir dir : pat.directions()) {
			positions.add(cursor);
			cursor = cursor.plus(dir);
			positions.add(cursor);
		}

		return positions;
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
	/** epsilon is 0.0000001. */
	public static boolean closeEnough(double a, double b) {
		return closeEnough(a, b, 0.0000001);
	}

    /** returns null if hand isn't MAIN_HAND or OFF_HAND or inaccessible (i'll add more eventually..!!) */
    @Nullable
    public static ItemStack getStackFromHand(CastingEnvironment ctx, int hand) {
        List<HeldItemInfo> stacks = ((GetStacks)ctx).getHeldStacks();
        try { return stacks.get(hand).stack(); }
		catch (IndexOutOfBoundsException e) {
			log("Someone tried to access idx " + hand + " of " + stacks.toString() + ".");
			return null;
		}
    }

	/** returns null if resulting Hand can't be MAIN_HAND or OFF_HAND (MORE WILL COME, THEE SHALL KNOW) */
	@Nullable
	public static Hand intToHand(int hand) {
		return switch(hand) {
			case 0 -> Hand.MAIN_HAND;
			case 1 -> Hand.OFF_HAND;
			default -> null;
		};
	}

	/** Returns stuff like Text.translateable("hands.lapisworks.43") (43rd hand)
	 * if it doesn't know wtf that Hand is */
	public static Text handToString(@Nullable Hand hand) {
		if (hand == null) return Text.translatable("hands.lapisworks.none");
		return switch (hand) {
			case MAIN_HAND -> MAINHAND;
			case OFF_HAND -> OFFHAND;
			default -> Text.translatable("hands.lapisworks." + hand.ordinal());
		};
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

	/** returns a list of all (mapped) positions between <code>start</code> and <code>end</code>,
	 * and a boolean which states if the raycast was interrupted suddenly instead of completing.
	 * <p>to skip a pos in the final list, return <code>null</code> for the pos.
	 * If you send a valid pos instead, it will be added to the final list.
	 * <p>to terminate the line, simply return <code>false</code> for the boolean. */
	// Amanatides-Woo is a silly name
	public static Pair<List<BlockPos>, Boolean> castRay(
		Vec3d start,
		Vec3d end,
		Function<BlockPos, Pair<BlockPos, Boolean>> atEachStep
	) {
		BlockPos ray = BlockPos.ofFloored(start);
		BlockPos endPos = BlockPos.ofFloored(end);

		Vec3d diff = end.subtract(start);

		Vec3i step = new Vec3i(
			(int)Math.signum(diff.x),
			(int)Math.signum(diff.y),
			(int)Math.signum(diff.z)
		);
		Vec3d dir = diff.normalize();
		Vec3d delta = new Vec3d(
			1.0 / Math.abs(dir.x),
			1.0 / Math.abs(dir.y),
			1.0 / Math.abs(dir.z)
		);


		BlockPos nextBoundary = new BlockPos(
			step.getX() < 0 ? 0 : step.getX(),
			step.getY() < 0 ? 0 : step.getY(),
			step.getZ() < 0 ? 0 : step.getZ()
		);
		// because Vec3d fields are final... :(
		double tMaxX = dir.x == 0 ?
			Double.POSITIVE_INFINITY : (ray.getX() + nextBoundary.getX() - start.x) / dir.x;
		double tMaxY = dir.y == 0 ?
			Double.POSITIVE_INFINITY : (ray.getY() + nextBoundary.getY() - start.y) / dir.y;
		double tMaxZ = dir.z == 0 ?
			Double.POSITIVE_INFINITY : (ray.getZ() + nextBoundary.getZ() - start.z) / dir.z;


		List<BlockPos> positions = new ArrayList<>();

		while (!ray.equals(endPos)) {

			var result = atEachStep.apply(ray);
			if (result.getLeft() != null)
				positions.add(result.getLeft());
			if (!result.getRight())
				return new Pair<>(positions, true);

			// fucking diagonals! hate these motherfuckers!
			if (closeEnough(tMaxX, tMaxY)) {
				ray = ray.add(step.getX(), step.getY(), 0);
				tMaxX += delta.x;
				tMaxY += delta.y;

				if (closeEnough(tMaxX, tMaxZ)) {
					ray = ray.add(0, 0, step.getZ());
					tMaxZ += delta.z;
				}

			} else if (closeEnough(tMaxX, tMaxZ)) {
				ray = ray.add(step.getX(), 0, step.getZ());
				tMaxX += delta.x;
				tMaxZ += delta.z;

			} else if (closeEnough(tMaxY, tMaxZ)) {
				ray = ray.add(0, step.getY(), step.getZ());
				tMaxY += delta.y;
				tMaxZ += delta.z;

			} else {
				if (tMaxX < tMaxY) {
					if (tMaxX < tMaxZ) {
						ray = ray.add(step.getX(), 0, 0);
						tMaxX += delta.x;
					} else {
						ray = ray.add(0, 0, step.getZ());
						tMaxZ += delta.z;
					}
				} else {
					if (tMaxY < tMaxZ) {
						ray = ray.add(0, step.getY(), 0);
						tMaxY += delta.y;
					} else {
						ray = ray.add(0, 0, step.getZ());
						tMaxZ += delta.z;
					}
				}
			}
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
	public static Pair<Long, Set<BlockPos>> interactWithLinkableMediaBlocks(
		World world,
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

			interactionLeft -= deposit
				? curr.depositMedia(interactionLeft, simulate)
				: curr.withdrawMedia(interactionLeft, simulate);

			if (interactionLeft == 0) return new Pair<>(amountToInteract, seen);

			for (BlockPos linked : curr.getLinks()) { if (seen.add(linked)) todo.add(linked); }
		}

		return new Pair<>(amountToInteract - interactionLeft, seen);
	}

	public static double getDistance(BlockPos pos1, BlockPos pos2) {
		return Math.sqrt(pos2.getSquaredDistance(pos1));
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

	public static NbtCompound serializeVec3d(Vec3d vec) {
		NbtCompound nbt = new NbtCompound();
		nbt.putDouble("x", vec.x);
		nbt.putDouble("y", vec.y);
		nbt.putDouble("z", vec.z);
		return nbt;
	}
	public static Vec3d deserializeVec3d(NbtCompound nbt) {
		return new Vec3d(
			nbt.getDouble("x"),
			nbt.getDouble("y"),
			nbt.getDouble("z")
		);
	}
	public static Vec3d deserializeVec3d(NbtElement ele) {
		return deserializeVec3d((NbtCompound)ele);
	}

	public static NbtList nbtListOf(List<? extends NbtElement> list) {
        NbtList nbtList = new NbtList();
        nbtList.addAll(list);
        return nbtList;
    }

	@SafeVarargs
	public static <ANY extends Object> boolean either(
		Predicate<ANY> predicate, ANY... options
	) {
		for (ANY option : options) {
			if (predicate.test(option)) return true;
		}
		return false;
	}

	public static Direction getFacingWithRespectToDown(
		Vec3d looking,
		Direction whereDownGoes
	) {
		return Direction.getFacing(
			whereDownGoes == Direction.EAST || whereDownGoes == Direction.WEST ? 0.0 : looking.x,
			whereDownGoes == Direction.UP || whereDownGoes == Direction.DOWN ? 0.0 : looking.y,
			whereDownGoes == Direction.NORTH || whereDownGoes == Direction.SOUTH ? 0.0 : looking.z
		);
	}

	public static Quaternionf getRotationForHorizontal(
		Direction horizontal,
		Direction down
	) {
		return switch (horizontal) {
            // i love me some quirky quirks
			case WEST ->
				RotationAxis.POSITIVE_Y.rotationDegrees(90 + (down == Direction.UP ? 180 : 0));
			case SOUTH ->
				RotationAxis.POSITIVE_Y.rotationDegrees(180);
			case EAST ->
				RotationAxis.POSITIVE_Y.rotationDegrees(270 - (down == Direction.UP ? 180 : 0));
			default ->
				RotationAxis.POSITIVE_Z.rotationDegrees(0);
		};
	}
	public static Quaternionf getReverseRotationForHorizontal(
		Direction horizontal,
		Direction down
	) {
		if (horizontal == down) return RotationAxis.NEGATIVE_Y.rotationDegrees(0);
		return switch (horizontal) {
			case WEST ->
				RotationAxis.NEGATIVE_Y.rotationDegrees(90 + (down == Direction.UP ? 180 : 0));
			case SOUTH ->
				RotationAxis.NEGATIVE_Y.rotationDegrees(180);
			case EAST ->
				RotationAxis.NEGATIVE_Y.rotationDegrees(270 - (down == Direction.UP ? 180 : 0));
			default ->
				RotationAxis.NEGATIVE_Y.rotationDegrees(0);
		};
	}
	public static Quaternionf rotateToBeAttachedTo(
		Direction attachedTo
	) {
		return switch (attachedTo) {
			case UP -> RotationAxis.POSITIVE_X.rotationDegrees(180);
			case DOWN -> RotationAxis.POSITIVE_X.rotationDegrees(0);
			case NORTH -> RotationAxis.POSITIVE_X.rotationDegrees(90);
			case SOUTH -> RotationAxis.NEGATIVE_X.rotationDegrees(90);
			case EAST -> RotationAxis.POSITIVE_Z.rotationDegrees(90);
			case WEST -> RotationAxis.NEGATIVE_Z.rotationDegrees(90);
		};
	}
	
	public static List<BlockPos> get3x3(
		BlockPos pos,
		Direction axis,
		boolean includeCenter
	) {
		Direction forward = axis == Direction.NORTH || axis == Direction.SOUTH ?
            Direction.UP : Direction.NORTH;
        Direction backward = forward.getOpposite();

        Vec3i _leftVec = forward.getVector().crossProduct(axis.getVector());
        Direction left = Direction.getFacing(_leftVec.getX(), _leftVec.getY(), _leftVec.getZ());
        Direction right = left.getOpposite();

		List<BlockPos> ret = new ArrayList<>(List.of(
			pos.offset(forward),
			pos.offset(forward).offset(left),
			pos.offset(forward).offset(right),
			pos.offset(left),
			pos.offset(right),
			pos.offset(backward),
			pos.offset(backward).offset(left),
			pos.offset(backward).offset(right)
		));
		if (includeCenter) ret.add(4, pos);
		return ret;
	}

	public static boolean sameAxis(Direction a, Direction b) {
		return a == b || a == b.getOpposite();
	}

	@Nullable
	public static Identifier getIdOf(PatternShapeMatch psm) {
        if (psm instanceof PatternShapeMatch.Normal nsm)
            return nsm.key.getValue();
        else if (psm instanceof PatternShapeMatch.PerWorld pwsm && pwsm.certain)
            return pwsm.key.getValue();
        else if (psm instanceof PatternShapeMatch.Special ssm)
            return ssm.key.getValue();
        else
            return null;
	}

	private static boolean actionInTag(Identifier pattern, TagKey<ActionRegistryEntry> tag) {
		return HexUtils.isOfTag(IXplatAbstractions.INSTANCE.getActionRegistry(), pattern, tag);
	}
	public static boolean exemptFromMediaConsumptionDecrease(Identifier pattern) {
		return actionInTag(pattern, CANNOT_MODIFY_COST_TAG)
			|| actionInTag(pattern, GRAND_RITUAL_BLACKLIST_TAG);
	}

	public static void makeParticlesInSpiralGoUp(
		World world,
		Vec3d centerBottom,
		Vec3d up,
		double radiusX,
		double radiusY,
		int color,
		Function<Integer, Double> speedLossPerParticleFunction,
		boolean goInReverse
	) {
		for (int i = 0; i < 1080; i += 3) {
			double rads = Math.toRadians(i);

			double x = radiusX*(goInReverse ? -Math.sin(rads) : Math.cos(rads));
			double y = radiusY*(goInReverse ? Math.cos(rads) : Math.sin(rads));
			Vec3d vel = up.multiply(speedLossPerParticleFunction.apply(i / 3));

			Vec3d pos = switch (
				Direction.getFacing(
					Math.abs(up.x),
					Math.abs(up.y),
					Math.abs(up.z)
				)
			) {
				case UP -> new Vec3d(centerBottom.x+x, centerBottom.y, centerBottom.z+y);
				case EAST -> new Vec3d(centerBottom.x, centerBottom.y+y, centerBottom.z+x);
				case SOUTH -> new Vec3d(centerBottom.x+x, centerBottom.y+y, centerBottom.z);
				default -> centerBottom;
			};

			world.addParticle(
				new ConjureParticleOptions(color),
				pos.x, pos.y, pos.z,
				vel.x, vel.y, vel.z
			);
		}
	}

	public static <Any extends Object> ArrayList<Any> mapMulti(
		Stream<? extends Object> stream,
		BiConsumer<Object, Consumer<Any>> mapper
	) {
		ArrayList<Any> buffer = new ArrayList<>();
		stream.forEach(obj -> mapper.accept(obj, buffer::add));
		return buffer;
	}

	public static CastingImage CastingImgWithStack(CastingImage img, List<Iota> stack) {
		return img.copy(
			stack,
			img.getParenCount(),
			img.getParenthesized(),
			img.getEscapeNext(),
			img.getOpsConsumed(),
			img.getUserData()
		);
	}

	public static boolean equalStacks(ItemStack a, ItemStack b) {
		return a.getItem() == b.getItem() && a.getCount() == b.getCount();
	}

	public static boolean isInCradle(ItemStack stack) {
		return NBTHelper.getBoolean(stack, IS_IN_CRADLE);
	}
	public static void removeFromCradle(ItemStack stack) {
		if (NBTHelper.contains(stack, IS_IN_CRADLE))
			NBTHelper.remove(stack, IS_IN_CRADLE);
	}
	public static void putInCradle(ItemStack stack) {
		NBTHelper.putBoolean(stack, IS_IN_CRADLE, true);
	}

	public static NbtCompound nbtCompoundOf(Stream<Pair<String, ? extends NbtElement>> stream) {
		NbtCompound nbt = new NbtCompound();
		stream.forEach(entry -> nbt.put(entry.getLeft(), entry.getRight()));
		return nbt;
	}

	public static <KEY extends Object, VALUE extends Object> HashMap<KEY, VALUE> hashMapof(
		NbtCompound nbt,
		Function<String, KEY> keyFunction,
		Function<NbtElement, VALUE> valueFunction
	) {
		HashMap<KEY, VALUE> map = new HashMap<>();
		for (String key : nbt.getKeys()) {
			map.put(keyFunction.apply(key), valueFunction.apply(nbt.get(key)));
		}
		return map;
	}

	public static boolean equalsStack(ItemStack stackA, ItemStack stackB) {
		return stackA == stackB ||
			(stackA.isOf(stackB.getItem()) && stackA.getCount() == stackB.getCount()) ||
			stackA.isEmpty() && stackB.isEmpty();
	}

	/** the slot reference is for Trinkets. it may be null. */
	@Nullable
	public static Pair<ItemStack, @Nullable SlotReference> tryGetTotem(LivingEntity entity) {
        for (Hand hand : Hand.values()) {
            ItemStack stack = entity.getStackInHand(hand);
            if (
				stack.isIn(TOTEM_TAG) &&
				(
					!(stack.getItem() instanceof ITotem totem) ||
					totem.canWork(entity, stack, null)
				)
			)
                return new Pair<>(stack, null);
        }

        var _trinketsOpt = TrinketsApi.getTrinketComponent(entity);
        if (_trinketsOpt.isEmpty()) return null;
        TrinketComponent trinkets = _trinketsOpt.get();

        for (var equipped : trinkets.getAllEquipped()) {
			ItemStack stack = equipped.getRight();
            if (
				stack.isIn(TOTEM_TAG) &&
				(
					!(stack.getItem() instanceof ITotem totem) ||
					totem.canWork(entity, stack, null)
				)
			)
                return new Pair<>(stack, equipped.getLeft());
        }

		return null;
	}

	public static int dim(int color) {
		return Math.max(
				(color & (int)(Math.pow(2, 24) - 1 - (Math.pow(2, 16) - 1))) - 0x800000,
				0
			) + Math.max(
				(color & (int)(Math.pow(2, 16) - 1 - (Math.pow(2, 8) - 1))) - 0x8000,
				0
			) + Math.max(
				(color * (int)(Math.pow(2, 8) - 1)) - 0x80,
				0
			);
	}

	public static <T extends Object> T pop(List<T> list) {
		return list.remove(list.size() - 1);
	}
	public static <T extends Object> T last(List<T> list) {
		return list.get(list.size() - 1);
	}

	public static <T1, T2 extends Object> T1 computeIfRight(
		Either<T1, T2> either,
		Function<T2, T1> computer
	) {
		Optional<T2> right = either.right();
		return right.isPresent()
			? computer.apply(right.get())
			: either.left().get();
	}
	public static <T1, T2 extends Object> T2 computeIfLeft(
		Either<T1, T2> either,
		Function<T1, T2> computer
	) {
		Optional<T1> left = either.left();
		return left.isPresent()
			? computer.apply(left.get())
			: either.right().get();
	}

	public static double truncate(double value) {
		return value - value % 1;
	}

	public static double ceil(double value) {
		return truncate(value) + (value % 1 > 0.0 ? Math.signum(value) : 0.0);
	}

	@Nullable
	public static <DesiredFrame extends ContinuationFrame> DesiredFrame pullFrameOfType(
		SpellContinuation continuation,
		Class<DesiredFrame> type
	) {
		SpellContinuation cont = continuation;
		while (cont instanceof NotDone notDone) {

			if (!type.isInstance(notDone.getFrame())) {
				cont = notDone.getNext();
				continue;
			}

			return type.cast(notDone.getFrame());
		}
		return null;
	}

	@Nullable
	public static <DesiredFrame extends ContinuationFrame> SpellContinuation setHighestFrameOfTypeTo(
		SpellContinuation continuation,
		Class<DesiredFrame> setThis,
		ContinuationFrame to
	) {
		Stack<ContinuationFrame> buffer = new Stack<>();
		SpellContinuation cont = continuation;
		while (cont instanceof NotDone notDone) {
			cont = notDone.getNext();

			if (!setThis.isInstance(notDone.getFrame())) {
				buffer.push(notDone.getFrame());
				continue;
			}

			cont = cont.pushFrame(to);

			while (!buffer.isEmpty()) {
				cont = cont.pushFrame(buffer.pop());
			}
			return cont;
		}
		return null;
	}
}
