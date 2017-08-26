package gtPlusPlus.xmod.gregtech.common.tileentities.machines.multi;

import java.util.ArrayList;

import gregtech.api.enums.*;
import gregtech.api.interfaces.ITexture;
import gregtech.api.interfaces.tileentity.IGregTechTileEntity;
import gregtech.api.metatileentity.MetaTileEntity;
import gregtech.api.metatileentity.implementations.GT_MetaTileEntity_MultiBlockBase;
import gregtech.api.objects.GT_RenderedTexture;
import gregtech.api.util.GT_Recipe;
import gregtech.api.util.GT_Utility;
import gtPlusPlus.core.block.ModBlocks;
import gtPlusPlus.core.lib.CORE;
import gtPlusPlus.core.util.Utils;
import gtPlusPlus.core.util.player.PlayerUtils;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;

public class GT4Entity_AutoCrafter
extends GT_MetaTileEntity_MultiBlockBase
{
	
	private boolean isDisassembling = false;
	private final int mTier = 6;
	private final int mHeatingCapacity = 4700;
	
	@Override
	public boolean isFacingValid(byte aFacing)
	{
		return aFacing > 1;
	}

	public void onRightclick(EntityPlayer aPlayer) {}

	public GT4Entity_AutoCrafter(int aID, String aName, String aNameRegional)
	{
		super(aID, aName, aNameRegional);
	}


	public GT4Entity_AutoCrafter(String mName) {
		super(mName);
	}

	@Override
	public MetaTileEntity newMetaEntity(IGregTechTileEntity aTileEntity)
	{
		return new GT4Entity_AutoCrafter(this.mName);
	}

	@Override
	public boolean isCorrectMachinePart(ItemStack aStack)
	{
		return true;
	}

	@Override
	public int getDamageToComponent(ItemStack aStack)
	{
		return 0;
	}

	@Override
	public boolean onRunningTick(ItemStack aStack)
	{
		return true;
	}

	@Override
	public boolean explodesOnComponentBreak(ItemStack aStack)
	{
		return false;
	}

	@Override
	public int getMaxEfficiency(ItemStack aStack)
	{
		return 10000;
	}

	@Override
	public int getPollutionPerTick(ItemStack aStack)
	{
		return 0;
	}

	public int getAmountOfOutputs()
	{
		return 1;
	}

	@Override
	public String[] getDescription()
	{
		return new String[]{
				"Highly Advanced Autocrafter",
				"Right Click with a Screwdriver to change mode",
				"This Machine Can Craft, Assemble or Disassemble",
				"--------------------------------------",
				"Insert a Memory stick into the GUI",
				"to automate a crafting table recipe",
				"Requires recipe to be scanned in a project table",
				"--------------------------------------",
				"Hatches & Busses can be placed anywhere",
				"1x Input Bus",
				"1x Input Hatch",
				"1x Output Bus",
				"1x Output Hatch",
				"1x Muffler Hatch",
				"1x Maintenance Hatch",
				"1x Energy Hatch",
				"--------------------------------------",
				CORE.GT_Tooltip
				};
	}

	@Override
	public ITexture[] getTexture(final IGregTechTileEntity aBaseMetaTileEntity, final byte aSide, final byte aFacing, final byte aColorIndex, final boolean aActive, final boolean aRedstone) {
		if (aSide == aFacing) {
			return new ITexture[]{Textures.BlockIcons.CASING_BLOCKS[TAE.GTPP_INDEX(28)],
					new GT_RenderedTexture(aActive ? Textures.BlockIcons.OVERLAY_FRONT_DISASSEMBLER_ACTIVE : Textures.BlockIcons.OVERLAY_FRONT_DISASSEMBLER)};
		}
		return new ITexture[]{Textures.BlockIcons.CASING_BLOCKS[TAE.GTPP_INDEX(28)]};
	}

	@Override
	public boolean checkMachine(IGregTechTileEntity aBaseMetaTileEntity, ItemStack p1) {		
		final int xDir = ForgeDirection.getOrientation(aBaseMetaTileEntity.getBackFacing()).offsetX;
		final int zDir = ForgeDirection.getOrientation(aBaseMetaTileEntity.getBackFacing()).offsetZ;
		if (!aBaseMetaTileEntity.getAirOffset(xDir, 0, zDir)) {
			return false;
		}
		int tAmount = 0;
		for (int i = -1; i < 2; ++i) {
			for (int j = -1; j < 2; ++j) {
				for (int h = -1; h < 2; ++h) {
					if (h != 0 || ((xDir + i != 0 || zDir + j != 0) && (i != 0 || j != 0))) {
						final IGregTechTileEntity tTileEntity = aBaseMetaTileEntity.getIGregTechTileEntityOffset(xDir + i, h, zDir + j);
						if (!this.addToMachineList(tTileEntity, TAE.GTPP_INDEX(28))) {
							if (aBaseMetaTileEntity.getBlockOffset(xDir + i, h,	zDir + j) != ModBlocks.blockCasings2Misc) {
								Utils.LOG_INFO("Bad Block. Found "+aBaseMetaTileEntity.getBlockOffset(xDir + i, h,	zDir + j) .getLocalizedName());
								Utils.LOG_INFO("Block Found at x:"+(aBaseMetaTileEntity.getXCoord()+xDir+i)+" | y:"+(aBaseMetaTileEntity.getYCoord()+h)+" | z:"+(aBaseMetaTileEntity.getZCoord()+zDir+j));
								return false;
							}
							if (aBaseMetaTileEntity.getMetaIDOffset(xDir + i, h, zDir + j) != 12) {
								Utils.LOG_INFO("Bad Meta.");
								return false;
							}
							++tAmount;
						}
					}
				}
			}
		}
		
		if ((this.mInputHatches.size() >= 1) || (this.mOutputHatches.size() >= 1) ||
				(this.mInputBusses.size() >= 1) || (this.mOutputBusses.size() >= 1) || 
				(this.mMufflerHatches.size() != 1) || (this.mMaintenanceHatches.size() != 1) ||
				(this.mEnergyHatches.size() >= 1)){
			Utils.LOG_INFO("Wrong Hatch count.");
			return false;
		}
		
		return tAmount >= 16;
		
	}
	
	@Override
	public GT_Recipe.GT_Recipe_Map getRecipeMap() {
			return GT_Recipe.GT_Recipe_Map.sAssemblerRecipes;
	}	

	@Override
	public void onScrewdriverRightClick(byte aSide, EntityPlayer aPlayer, float aX, float aY, float aZ) {
		isDisassembling = Utils.invertBoolean(isDisassembling);
		if (this.isDisassembling){
			PlayerUtils.messagePlayer(aPlayer, "You are now running the Auto-Crafter in mode: §cDisassembly");			
		}
		else {
			PlayerUtils.messagePlayer(aPlayer, "You are now running the Auto-Crafter in mode: §aAssembly");			
		}
		super.onScrewdriverRightClick(aSide, aPlayer, aX, aY, aZ);
	}
	
	@Override
	public boolean checkRecipe(final ItemStack aStack) {
		if (this.isDisassembling){
			Utils.LOG_INFO("Breaking things down.");
			return doDisassembly();
		}
		else {
			Utils.LOG_INFO("Putting the pieces together.");
			final ArrayList<ItemStack> tInputList = this.getStoredInputs();
			for (int tInputList_sS = tInputList.size(), i = 0; i < tInputList_sS - 1; ++i) {
				for (int j = i + 1; j < tInputList_sS; ++j) {
					if (GT_Utility.areStacksEqual(tInputList.get(i), tInputList.get(j))) {
						if (tInputList.get(i).stackSize < tInputList.get(j).stackSize) {
							tInputList.remove(i--);
							tInputList_sS = tInputList.size();
							break;
						}
						tInputList.remove(j--);
						tInputList_sS = tInputList.size();
					}
				}
			}
			final ItemStack[] tInputs = tInputList.toArray(new ItemStack[tInputList.size()]);
			final ArrayList<FluidStack> tFluidList = this.getStoredFluids();
			for (int tFluidList_sS = tFluidList.size(), k = 0; k < tFluidList_sS - 1; ++k) {
				for (int l = k + 1; l < tFluidList_sS; ++l) {
					if (GT_Utility.areFluidsEqual(tFluidList.get(k), tFluidList.get(l))) {
						if (tFluidList.get(k).amount < tFluidList.get(l).amount) {
							tFluidList.remove(k--);
							tFluidList_sS = tFluidList.size();
							break;
						}
						tFluidList.remove(l--);
						tFluidList_sS = tFluidList.size();
					}
				}
			}
			final FluidStack[] tFluids = tFluidList.toArray(new FluidStack[tFluidList.size()]);
			if (tInputList.size() > 0) {
				Utils.LOG_INFO("test1");
				final long tVoltage = this.getMaxInputVoltage();
				final byte tTier = (byte) Math.max(1, GT_Utility.getTier(tVoltage));
				final GT_Recipe tRecipe = this.getRecipeMap().findRecipe(this.getBaseMetaTileEntity(), false, GT_Values.V[this.mTier], tFluids,	tInputs);
				if (tRecipe == null){
					Utils.LOG_INFO("Recipe is Null.");
				}
				if (tRecipe != null && tRecipe.isRecipeInputEqual(true, tFluids, tInputs)) {
					Utils.LOG_INFO("test2");
					this.mEfficiency = 10000 - (this.getIdealStatus() - this.getRepairStatus()) * 1000;
					this.mEfficiencyIncrease = 10000;
					final int tHeatCapacityDivTiers = (this.mHeatingCapacity - tRecipe.mSpecialValue) / 900;
					if (tRecipe.mEUt <= 16) {
						Utils.LOG_INFO("test3");
						this.mEUt = tRecipe.mEUt * (1 << tTier - 1) * (1 << tTier - 1);
						this.mMaxProgresstime = tRecipe.mDuration / (1 << tTier - 1);
					} else {
						Utils.LOG_INFO("test4");
						this.mEUt = tRecipe.mEUt;
						this.mMaxProgresstime = tRecipe.mDuration;
						int m = 2;
						while (this.mEUt <= GT_Values.V[tTier - 1]) {
							this.mEUt *= 4;
							this.mMaxProgresstime /= ((tHeatCapacityDivTiers >= m) ? 4 : 2);
							m += 2;
						}
					}
					if (tHeatCapacityDivTiers > 0) {
						Utils.LOG_INFO("test5");
						this.mEUt *= (int) Math.pow(0.95, tHeatCapacityDivTiers);
					}
					if (this.mEUt > 0) {
						Utils.LOG_INFO("test6");
						this.mEUt = -this.mEUt;
					}
					this.mMaxProgresstime = Math.max(1, this.mMaxProgresstime);
					this.mOutputItems = new ItemStack[]{tRecipe.getOutput(0), tRecipe.getOutput(1)};
					this.updateSlots();
					Utils.LOG_INFO("test7");
					return true;
				}
			}
			return false;
		}
	}
	
	public boolean doDisassembly(){
		
		final ArrayList<ItemStack> tInputList = this.getStoredInputs();
		for (int tInputList_sS = tInputList.size(), i = 0; i < tInputList_sS - 1; ++i) {
			for (int j = i + 1; j < tInputList_sS; ++j) {
				if (GT_Utility.areStacksEqual(tInputList.get(i), tInputList.get(j))) {
					if (tInputList.get(i).stackSize < tInputList.get(j).stackSize) {
						tInputList.remove(i--);
						tInputList_sS = tInputList.size();
						break;
					}
					tInputList.remove(j--);
					tInputList_sS = tInputList.size();
				}
			}
		}
		final ItemStack[] tInputs = tInputList.toArray(new ItemStack[tInputList.size()]);
		
		ItemStack inputItem = tInputs[0];
		int outputSlots = this.mOutputBusses.get(0).getSizeInventory();
		this.mOutputItems = new ItemStack[outputSlots];
		if (inputItem != null) {
			Utils.LOG_INFO("test 1");
			NBTTagCompound tNBT = inputItem.getTagCompound();
			if (tNBT != null) {
				Utils.LOG_INFO("test 2");
				tNBT = tNBT.getCompoundTag("GT.CraftingComponents");
				if (tNBT != null) {
					Utils.LOG_INFO("test 3");
					this.mEUt = 16 * (1 << this.mTier - 1) * (1 << this.mTier - 1);
					this.mMaxProgresstime = 400;
					for (int i = 0; i < this.mOutputItems.length; ++i) {
						Utils.LOG_INFO("test 4 | "+i);
						if (this.getBaseMetaTileEntity().getRandomNumber(100) < 50 + 10 * this.mTier) {
							this.mOutputItems[i] = GT_Utility.loadItem(tNBT, "Ingredient." + i);
							if (this.mOutputItems[i] != null) {
								this.mMaxProgresstime *= (int) 1.7;
							}
						}
					}
					if (this.mTier > 5) {
						this.mMaxProgresstime >>= this.mTier - 5;
					}
					if (this.mMaxProgresstime == 400) {
						return false;
					}
					final ItemStack input2 = inputItem;
					inputItem.stackSize--;
					return true;
				}
			}
		}
		Utils.LOG_INFO("test - bad");
		return false;
	}
	
	
	@Override
	public String[] getInfoData() {

		final String tRunning = (this.mMaxProgresstime>0 ? "Auto-Crafter running":"Auto-Crafter stopped");
		final String tMaintainance = (this.getIdealStatus() == this.getRepairStatus() ? "No Maintainance issues" : "Needs Maintainance");
		String tMode;
		if (this.isDisassembling){
			tMode = "§cDisassembly";			
		}
		else {
			tMode = "§aAssembly";			
		}
		
		return new String[]{
				"Large Scale Auto-Asesembler v1.01c",
				tRunning,
				tMaintainance,
				"Mode: "+tMode};
	}

	@Override
	public boolean isGivingInformation() {
		return true;
	}
	
}
