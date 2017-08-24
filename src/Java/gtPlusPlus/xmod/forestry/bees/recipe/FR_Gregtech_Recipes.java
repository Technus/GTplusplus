package gtPlusPlus.xmod.forestry.bees.recipe;

import gtPlusPlus.core.lib.LoadedMods;
import gtPlusPlus.core.util.item.ItemUtils;
import gtPlusPlus.core.util.recipe.RecipeUtils;
import gtPlusPlus.xmod.forestry.bees.items.FR_ItemRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;

public class FR_Gregtech_Recipes {

	private static String rod_Electrum = "stickElectrum";
	private static String rod_LongElectrum = "stickLongElectrum";
	private static String foil_Electrum = "foilElectrum";
	private static String rod_Uranium = "stickUranium";
	private static String rod_LongUranium = "stickLongUranium";
	private static String foil_Uranium235 = "foilUranium235";
	private static ItemStack hiveFrameAccelerated = ItemUtils.getSimpleStack(FR_ItemRegistry.hiveFrameAccelerated);
	private static ItemStack hiveFrameMutagenic = ItemUtils.getSimpleStack(FR_ItemRegistry.hiveFrameMutagenic);
	private static ItemStack hiveFrameVoid = ItemUtils.getSimpleStack(FR_ItemRegistry.hiveFrameVoid);
	private static ItemStack hiveFrameBusy = ItemUtils.getSimpleStack(FR_ItemRegistry.hiveFrameBusy);


	private static ItemStack hiveFrameCocoa = ItemUtils.getSimpleStack(FR_ItemRegistry.hiveFrameCocoa);
	private static ItemStack hiveFrameCaged = ItemUtils.getSimpleStack(FR_ItemRegistry.hiveFrameCaged);
	private static ItemStack hiveFrameSoul = ItemUtils.getSimpleStack(FR_ItemRegistry.hiveFrameSoul);
	private static ItemStack hiveFrameClay = ItemUtils.getSimpleStack(FR_ItemRegistry.hiveFrameClay);
	private static ItemStack hiveFrameNova = ItemUtils.getSimpleStack(FR_ItemRegistry.hiveFrameNova);

	private static ItemStack hiveFrameImpregnated = ItemUtils.getItemStack("Forestry:frameImpregnated", 1);
	private static ItemStack blockSoulSand = new ItemStack(Blocks.soul_sand, 1);
	private static ItemStack blockIronBars = new ItemStack (Blocks.iron_bars, 1);
	private static ItemStack itemClayDust = new ItemStack(Items.clay_ball, 1);
	private static ItemStack itemCocoaBeans = new ItemStack(Items.dye, 1, 3);


	public static void registerItems(){

		//Magic Bee Like Frames
		RecipeUtils.addShapedGregtechRecipe(
				rod_LongElectrum, rod_Electrum, rod_LongElectrum,
				rod_LongElectrum, foil_Electrum, rod_LongElectrum,
				rod_Electrum, rod_Electrum, rod_Electrum,
				hiveFrameAccelerated);

		RecipeUtils.addShapedGregtechRecipe(
				rod_LongUranium, rod_Uranium, rod_LongUranium,
				rod_LongUranium, foil_Uranium235, rod_LongUranium,
				rod_Uranium, rod_Uranium, rod_Uranium,
				hiveFrameMutagenic);
		
		RecipeUtils.addShapedGregtechRecipe(
				"stickLongThaumium", "stickThaumium", "stickLongThaumium",
				"stickLongThaumium", ItemUtils.getSimpleStack(Items.ender_pearl), "stickLongThaumium",
				"stickThaumium", "stickThaumium", "stickThaumium",
				hiveFrameMutagenic);
		
		RecipeUtils.addShapedGregtechRecipe(
				"stickLongBlueSteel", "stickBlueSteel", "stickLongBlueSteel",
				"stickLongBlueSteel", ItemUtils.getSimpleStack(Items.nether_star), "stickLongBlueSteel",
				"stickBlueSteel", "stickBlueSteel", "stickBlueSteel",
				hiveFrameBusy);

		if (!LoadedMods.ExtraBees){
			//Extra Bee Like Frames
			RecipeUtils.addShapedGregtechRecipe(
					null, itemCocoaBeans, null,
					itemCocoaBeans, hiveFrameImpregnated, itemCocoaBeans,
					null, itemCocoaBeans, null,
					hiveFrameCocoa);

			RecipeUtils.addShapedGregtechRecipe(
					hiveFrameImpregnated, blockIronBars, null,
					null, null, null,
					null, null, null,
					hiveFrameCaged);

			RecipeUtils.addShapedGregtechRecipe(
					hiveFrameImpregnated, blockSoulSand, null,
					null, null, null,
					null, null, null,
					hiveFrameSoul);

			RecipeUtils.addShapedGregtechRecipe(
					null, itemClayDust, null,
					itemClayDust, hiveFrameImpregnated, itemClayDust,
					null, itemClayDust, null,
					hiveFrameClay);
		}



	}

}
