package gtPlusPlus.xmod.gregtech.api.util;

import static gregtech.api.enums.GT_Values.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;

import gregtech.api.GregTech_API;
import gregtech.api.enums.*;
import gregtech.api.objects.GT_HashSet;
import gregtech.api.objects.GT_ItemStack;
import gregtech.api.util.*;
import gregtech.common.GT_Proxy.OreDictEventContainer;
import gtPlusPlus.xmod.gregtech.api.enums.GregtechOrePrefixes;
import gtPlusPlus.xmod.gregtech.api.enums.GregtechOrePrefixes.GT_Materials;
import gtPlusPlus.xmod.gregtech.api.objects.GregtechItemData;
import gtPlusPlus.xmod.gregtech.api.objects.GregtechMaterialStack;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

/**
 * NEVER INCLUDE THIS FILE IN YOUR MOD!!!
 * <p/>
 * This is the Core of my OreDict Unification Code
 * <p/>
 * If you just want to use this to unificate your Items, then use the Function in the GregTech_API File
 * <p/>
 * P.S. It is intended to be named "Unificator" and not "Unifier", because that sounds more awesome.
 */
public class GregtechOreDictUnificator {
	private static final HashMap<String, ItemStack> sName2StackMap = new HashMap<>();
	private static final HashMap<GT_ItemStack, GregtechItemData> sItemStack2DataMap = new HashMap<>();
	private static final GT_HashSet<GT_ItemStack> sNoUnificationList = new GT_HashSet<>();
	public static volatile int VERSION = 508;
	private static int isRegisteringOre = 0, isAddingOre = 0;
	private static boolean mRunThroughTheList = true;

	static {
		GregTech_API.sItemStackMappings.add(sItemStack2DataMap);
	}

	/**
	 * The Blacklist just prevents the Item from being unificated into something else.
	 * Useful if you have things like the Industrial Diamond, which is better than regular Diamond, but also usable in absolutely all Diamond Recipes.
	 */
	public static void addToBlacklist(final ItemStack aStack) {
		if (GT_Utility.isStackValid(aStack) && !GT_Utility.isStackInList(aStack, sNoUnificationList)) {
			sNoUnificationList.add(aStack);
		}
	}

	public static boolean isBlacklisted(final ItemStack aStack) {
		return GT_Utility.isStackInList(aStack, sNoUnificationList);
	}

	public static void add(final GregtechOrePrefixes aPrefix, final GT_Materials aMaterial, final ItemStack aStack) {
		set(aPrefix, aMaterial, aStack, false, false);
	}

	public static void set(final GregtechOrePrefixes aPrefix, final GT_Materials aMaterial, final ItemStack aStack) {
		set(aPrefix, aMaterial, aStack, true, false);
	}

	public static void set(final GregtechOrePrefixes aPrefix, final GT_Materials aMaterial, ItemStack aStack, final boolean aOverwrite, final boolean aAlreadyRegistered) {
		if ((aMaterial == null) || (aPrefix == null) || GT_Utility.isStackInvalid(aStack) || (Items.feather.getDamage(aStack) == W)) {
			return;
		}
		isAddingOre++;
		aStack = GT_Utility.copyAmount(1, aStack);
		if (!aAlreadyRegistered) {
			registerOre(aPrefix.get(aMaterial), aStack);
		}
		addAssociation(aPrefix, aMaterial, aStack, isBlacklisted(aStack));
		if (aOverwrite || GT_Utility.isStackInvalid(sName2StackMap.get(aPrefix.get(aMaterial).toString()))) {
			sName2StackMap.put(aPrefix.get(aMaterial).toString(), aStack);
		}
		isAddingOre--;
	}

	public static ItemStack getFirstOre(final Object aName, final long aAmount) {
		if (GT_Utility.isStringInvalid(aName)) {
			return null;
		}
		final ItemStack tStack = sName2StackMap.get(aName.toString());
		if (GT_Utility.isStackValid(tStack)) {
			return GT_Utility.copyAmount(aAmount, tStack);
		}
		return GT_Utility.copyAmount(aAmount, getOres(aName).toArray());
	}

	public static ItemStack get(final Object aName, final long aAmount) {
		return get(aName, null, aAmount, true, true);
	}

	public static ItemStack get(final Object aName, final ItemStack aReplacement, final long aAmount) {
		return get(aName, aReplacement, aAmount, true, true);
	}

	public static ItemStack get(final GregtechOrePrefixes aPrefix, final Object aMaterial, final long aAmount) {
		return get(aPrefix, aMaterial, null, aAmount);
	}

	public static ItemStack get(final GregtechOrePrefixes aPrefix, final Object aMaterial, final ItemStack aReplacement, final long aAmount) {
		return get(aPrefix.get(aMaterial), aReplacement, aAmount, false, true);
	}

	public static ItemStack get(final Object aName, final ItemStack aReplacement, final long aAmount, final boolean aMentionPossibleTypos, final boolean aNoInvalidAmounts) {
		if (aNoInvalidAmounts && (aAmount < 1)) {
			return null;
		}
		if (!sName2StackMap.containsKey(aName.toString()) && aMentionPossibleTypos) {
			GT_Log.err.println("Unknown Key for Unification, Typo? " + aName);
		}
		return GT_Utility.copyAmount(aAmount, sName2StackMap.get(aName.toString()), getFirstOre(aName, aAmount), aReplacement);
	}

	public static ItemStack[] setStackArray(final boolean aUseBlackList, final ItemStack... aStacks) {
		for (int i = 0; i < aStacks.length; i++) {
			aStacks[i] = get(aUseBlackList, GT_Utility.copy(aStacks[i]));
		}
		return aStacks;
	}

	public static ItemStack[] getStackArray(final boolean aUseBlackList, final Object... aStacks) {
		final ItemStack[] rStacks = new ItemStack[aStacks.length];
		for (int i = 0; i < aStacks.length; i++) {
			rStacks[i] = get(aUseBlackList, GT_Utility.copy(aStacks[i]));
		}
		return rStacks;
	}

	public static ItemStack setStack(final ItemStack aStack) {
		return setStack(true, aStack);
	}

	public static ItemStack setStack(final boolean aUseBlackList, final ItemStack aStack) {
		if (GT_Utility.isStackInvalid(aStack)) {
			return aStack;
		}
		final ItemStack tStack = get(aUseBlackList, aStack);
		if (GT_Utility.areStacksEqual(aStack, tStack)) {
			return aStack;
		}
		aStack.func_150996_a(tStack.getItem());
		Items.feather.setDamage(aStack, Items.feather.getDamage(tStack));
		return aStack;
	}

	public static ItemStack get(final ItemStack aStack) {
		return get(true, aStack);
	}

	public static ItemStack get(final boolean aUseBlackList, final ItemStack aStack) {
		if (GT_Utility.isStackInvalid(aStack)) {
			return null;
		}
		final GregtechItemData tPrefixMaterial = getAssociation(aStack);
		ItemStack rStack = null;
		if ((tPrefixMaterial == null) || !tPrefixMaterial.hasValidPrefixMaterialData() || (aUseBlackList && tPrefixMaterial.mBlackListed)) {
			return GT_Utility.copy(aStack);
		}
		if (aUseBlackList && !GregTech_API.sUnificationEntriesRegistered && isBlacklisted(aStack)) {
			tPrefixMaterial.mBlackListed = true;
			return GT_Utility.copy(aStack);
		}
		if (tPrefixMaterial.mUnificationTarget == null) {
			tPrefixMaterial.mUnificationTarget = sName2StackMap.get(tPrefixMaterial.toString());
		}
		rStack = tPrefixMaterial.mUnificationTarget;
		if (GT_Utility.isStackInvalid(rStack)) {
			return GT_Utility.copy(aStack);
		}
		assert rStack != null;
		rStack.setTagCompound(aStack.getTagCompound());
		return GT_Utility.copyAmount(aStack.stackSize, rStack);
	}

	public static void addItemData(final ItemStack aStack, final GregtechItemData aData) {
		if (GT_Utility.isStackValid(aStack) && (getItemData(aStack) == null) && (aData != null)) {
			setItemData(aStack, aData);
		}
	}

	public static void setItemData(ItemStack aStack, final GregtechItemData aData) {
		if (GT_Utility.isStackInvalid(aStack) || (aData == null)) {
			return;
		}
		final GregtechItemData tData = getItemData(aStack);
		if ((tData == null) || !tData.hasValidPrefixMaterialData()) {
			if (tData != null) {
				for (final Object tObject : tData.mExtraData) {
					if (!aData.mExtraData.contains(tObject)) {
						aData.mExtraData.add(tObject);
					}
				}
			}
			if (aStack.stackSize > 1) {
				if (aData.mMaterial != null) {
					aData.mMaterial.mAmount /= aStack.stackSize;
				}
				for (final GregtechMaterialStack tMaterial : aData.mByProducts) {
					tMaterial.mAmount /= aStack.stackSize;
				}
				aStack = GT_Utility.copyAmount(1, aStack);
			}
			sItemStack2DataMap.put(new GT_ItemStack(aStack), aData);
			if (aData.hasValidMaterialData()) {
				long tValidMaterialAmount = aData.mMaterial.mMaterial.contains(SubTag.NO_RECYCLING) ? 0 : aData.mMaterial.mAmount >= 0 ? aData.mMaterial.mAmount : M;
				for (final GregtechMaterialStack tMaterial : aData.mByProducts) {
					tValidMaterialAmount += tMaterial.mMaterial.contains(SubTag.NO_RECYCLING) ? 0 : tMaterial.mAmount >= 0 ? tMaterial.mAmount : M;
				}
				if (tValidMaterialAmount < M) {
					GT_ModHandler.addToRecyclerBlackList(aStack);
				}
			}
			if (mRunThroughTheList) {
				if (GregTech_API.sLoadStarted) {
					mRunThroughTheList = false;
					for (final Entry<GT_ItemStack, GregtechItemData> tEntry : sItemStack2DataMap.entrySet()) {
						if (!tEntry.getValue().hasValidPrefixData() || tEntry.getValue().mPrefix.mAllowNormalRecycling) {
							GregtechRecipeRegistrator.registerMaterialRecycling(tEntry.getKey().toStack(), tEntry.getValue());
						}
					}
				}
			} else {
				if (!aData.hasValidPrefixData() || aData.mPrefix.mAllowNormalRecycling) {
					GregtechRecipeRegistrator.registerMaterialRecycling(aStack, aData);
				}
			}
		} else {
			for (final Object tObject : aData.mExtraData) {
				if (!tData.mExtraData.contains(tObject)) {
					tData.mExtraData.add(tObject);
				}
			}
		}
	}

	public static void addAssociation(final GregtechOrePrefixes aPrefix, final GT_Materials aMaterial, final ItemStack aStack, final boolean aBlackListed) {
		if ((aPrefix == null) || (aMaterial == null) || GT_Utility.isStackInvalid(aStack)) {
			return;
		}
		if (Items.feather.getDamage(aStack) == W) {
			for (byte i = 0; i < 16; i++) {
				setItemData(GT_Utility.copyAmountAndMetaData(1, i, aStack), new GregtechItemData(aPrefix, aMaterial, aBlackListed));
			}
		}
		setItemData(aStack, new GregtechItemData(aPrefix, aMaterial, aBlackListed));
	}

	public static GregtechItemData getItemData(final ItemStack aStack) {
		if (GT_Utility.isStackInvalid(aStack)) {
			return null;
		}
		GregtechItemData rData = sItemStack2DataMap.get(new GT_ItemStack(aStack));
		if (rData == null) {
			rData = sItemStack2DataMap.get(new GT_ItemStack(GT_Utility.copyMetaData(W, aStack)));
		}
		return rData;
	}

	public static GregtechItemData getAssociation(final ItemStack aStack) {
		final GregtechItemData rData = getItemData(aStack);
		return (rData != null) && rData.hasValidPrefixMaterialData() ? rData : null;
	}

	public static boolean isItemStackInstanceOf(final ItemStack aStack, final Object aName) {
		if (GT_Utility.isStringInvalid(aName) || GT_Utility.isStackInvalid(aStack)) {
			return false;
		}
		for (final ItemStack tOreStack : getOres(aName.toString())) {
			if (GT_Utility.areStacksEqual(tOreStack, aStack, true)) {
				return true;
			}
		}
		return false;
	}

	public static boolean isItemStackDye(final ItemStack aStack) {
		if (GT_Utility.isStackInvalid(aStack)) {
			return false;
		}
		for (final Dyes tDye : Dyes.VALUES) {
			if (isItemStackInstanceOf(aStack, tDye.toString())) {
				return true;
			}
		}
		return false;
	}

	public static boolean registerOre(final GregtechOrePrefixes aPrefix, final Object aMaterial, final ItemStack aStack) {
		return registerOre(aPrefix.get(aMaterial), aStack);
	}

	public static boolean registerOre(final Object aName, final ItemStack aStack) {
		if ((aName == null) || GT_Utility.isStackInvalid(aStack)) {
			return false;
		}
		final String tName = aName.toString();
		if (GT_Utility.isStringInvalid(tName)) {
			return false;
		}
		final ArrayList<ItemStack> tList = getOres(tName);
		for (int i = 0; i < tList.size(); i++) {
			if (GT_Utility.areStacksEqual(tList.get(i), aStack, true)) {
				return false;
			}
		}
		isRegisteringOre++;
		OreDictionary.registerOre(tName, GT_Utility.copyAmount(1, aStack));
		isRegisteringOre--;
		return true;
	}

	public static boolean isRegisteringOres() {
		return isRegisteringOre > 0;
	}

	public static boolean isAddingOres() {
		return isAddingOre > 0;
	}

	public static void resetUnificationEntries() {
		for (final GregtechItemData tPrefixMaterial : sItemStack2DataMap.values()) {
			tPrefixMaterial.mUnificationTarget = null;
		}
	}

	public static ItemStack getGem(final GregtechMaterialStack aMaterial) {
		return aMaterial == null ? null : getGem(aMaterial.mMaterial, aMaterial.mAmount);
	}

	public static ItemStack getGem(final GT_Materials aMaterial, final OrePrefixes aPrefix) {
		return aMaterial == null ? null : getGem(aMaterial, aPrefix.mMaterialAmount);
	}

	public static ItemStack getGem(final GT_Materials aMaterial, final long aMaterialAmount) {
		ItemStack rStack = null;
		if (((aMaterialAmount >= M) || (aMaterialAmount >= (M * 32)))) {
			rStack = get(GregtechOrePrefixes.gem, aMaterial, aMaterialAmount / M);
		}
		if ((rStack == null) && ((((aMaterialAmount * 2) % M) == 0) || (aMaterialAmount >= (M * 16)))) {
			rStack = get(GregtechOrePrefixes.gemFlawed, aMaterial, (aMaterialAmount * 2) / M);
		}
		if ((rStack == null) && (((aMaterialAmount * 4) >= M))) {
			rStack = get(GregtechOrePrefixes.gemChipped, aMaterial, (aMaterialAmount * 4) / M);
		}
		return rStack;
	}

	public static ItemStack getDust(final GregtechMaterialStack aMaterial) {
		return aMaterial == null ? null : getDust(aMaterial.mMaterial, aMaterial.mAmount);
	}

	public static ItemStack getDust(final GT_Materials aMaterial, final OrePrefixes aPrefix) {
		return aMaterial == null ? null : getDust(aMaterial, aPrefix.mMaterialAmount);
	}

	public static ItemStack getDust(final GT_Materials aMaterial, final long aMaterialAmount) {
		if (aMaterialAmount <= 0) {
			return null;
		}
		ItemStack rStack = null;
		if ((((aMaterialAmount % M) == 0) || (aMaterialAmount >= (M * 16)))) {
			rStack = get(GregtechOrePrefixes.dust, aMaterial, aMaterialAmount / M);
		}
		if ((rStack == null) && ((((aMaterialAmount * 4) % M) == 0) || (aMaterialAmount >= (M * 8)))) {
			rStack = get(GregtechOrePrefixes.dustSmall, aMaterial, (aMaterialAmount * 4) / M);
		}
		if ((rStack == null) && (((aMaterialAmount * 9) >= M))) {
			rStack = get(GregtechOrePrefixes.dustTiny, aMaterial, (aMaterialAmount * 9) / M);
		}
		return rStack;
	}

	public static ItemStack getIngot(final GregtechMaterialStack aMaterial) {
		return aMaterial == null ? null : getIngot(aMaterial.mMaterial, aMaterial.mAmount);
	}

	public static ItemStack getIngot(final GT_Materials aMaterial, final OrePrefixes aPrefix) {
		return aMaterial == null ? null : getIngot(aMaterial, aPrefix.mMaterialAmount);
	}

	public static ItemStack getIngot(final GT_Materials aMaterial, final long aMaterialAmount) {
		if (aMaterialAmount <= 0) {
			return null;
		}
		ItemStack rStack = null;
		if (((((aMaterialAmount % (M * 9)) == 0) && ((aMaterialAmount / (M * 9)) > 1)) || (aMaterialAmount >= (M * 72)))) {
			rStack = get(GregtechOrePrefixes.block, aMaterial, aMaterialAmount / (M * 9));
		}
		if ((rStack == null) && (((aMaterialAmount % M) == 0) || (aMaterialAmount >= (M * 8)))) {
			rStack = get(GregtechOrePrefixes.ingot, aMaterial, aMaterialAmount / M);
		}
		if ((rStack == null) && (((aMaterialAmount * 9) >= M))) {
			rStack = get(GregtechOrePrefixes.nugget, aMaterial, (aMaterialAmount * 9) / M);
		}
		return rStack;
	}

	public static ItemStack getIngotOrDust(final GT_Materials aMaterial, final long aMaterialAmount) {
		if (aMaterialAmount <= 0) {
			return null;
		}
		ItemStack rStack = getIngot(aMaterial, aMaterialAmount);
		if (rStack == null) {
			rStack = getDust(aMaterial, aMaterialAmount);
		}
		return rStack;
	}

	public static ItemStack getIngotOrDust(final GregtechMaterialStack aMaterial) {
		ItemStack rStack = getIngot(aMaterial);
		if((aMaterial!=null)&&(aMaterial.mMaterial!=null)) {
			rStack = getDust(aMaterial);
		}
		if (rStack == null) {
			rStack = getDust(aMaterial);
		}
		return rStack;
	}

	public static ItemStack getDustOrIngot(final GT_Materials aMaterial, final long aMaterialAmount) {
		if (aMaterialAmount <= 0) {
			return null;
		}
		ItemStack rStack = getDust(aMaterial, aMaterialAmount);
		if (rStack == null) {
			rStack = getIngot(aMaterial, aMaterialAmount);
		}
		return rStack;
	}

	public static ItemStack getDustOrIngot(final GregtechMaterialStack aMaterial) {
		ItemStack rStack = getDust(aMaterial);
		if (rStack == null) {
			rStack = getIngot(aMaterial);
		}
		return rStack;
	}

	/**
	 * @return a Copy of the OreDictionary.getOres() List
	 */
	public static ArrayList<ItemStack> getOres(final OrePrefixes aPrefix, final Object aMaterial) {
		return getOres(aPrefix.get(aMaterial));
	}

	/**
	 * @return a Copy of the OreDictionary.getOres() List
	 */
	public static ArrayList<ItemStack> getOres(final Object aOreName) {
		final String aName = aOreName == null ? E : aOreName.toString();
		final ArrayList<ItemStack> rList = new ArrayList<>();
		if (GT_Utility.isStringValid(aName)) {
			rList.addAll(OreDictionary.getOres(aName));
		}
		return rList;
	}

	public static void registerRecipes(final OreDictEventContainer tOre) {
		// TODO Auto-generated method stub

	}
}
