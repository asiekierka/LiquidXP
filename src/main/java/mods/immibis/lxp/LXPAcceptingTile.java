package mods.immibis.lxp;

import mods.immibis.core.TileCombined;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagDouble;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class LXPAcceptingTile extends TileCombined implements IFluidHandler {
	protected double storedLiquid = 0; // can be negative
	protected int capacity;
	protected boolean acceptingLXP = true;
	
	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setDouble("stored", storedLiquid);
		tag.setInteger("capacity", capacity);
		tag.setBoolean("accept", acceptingLXP);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		
		NBTBase b = tag.getTag("stored");
		if(b instanceof NBTTagDouble)
			storedLiquid = tag.getDouble("stored");
		else
			storedLiquid = tag.getInteger("stored");
		
		capacity = tag.getInteger("capacity");
		acceptingLXP = tag.getBoolean("accept") || !tag.hasKey("accept");
	}

	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		if(!acceptingLXP)
			return 0;
		
		if(LXP_MFR_Compat.mobEssence != null && LXP_MFR_Compat.mobEssence == resource.getFluid()) {
			
			double maxME = LXP_MFR_Compat.convertLiquidXpToMobEssence(capacity - storedLiquid);
			
			int addedME = Math.min(resource.amount, (int)maxME);
			if(doFill)
				storedLiquid += LXP_MFR_Compat.convertMobEssenceToLiquidXp(addedME);
			
			return addedME;
		}
		
		if(LXP_OpenBlocks_Compat.xpJuice != null && LXP_OpenBlocks_Compat.xpJuice == resource.getFluid()) {
			
			double maxXJ = LXP_OpenBlocks_Compat.convertLiquidXpToXpJuice(capacity - storedLiquid);
			
			int addedXJ = Math.min(resource.amount, (int)maxXJ);
			if(doFill)
				storedLiquid += LXP_OpenBlocks_Compat.convertXpJuiceToLiquidXp(addedXJ);
			
			return addedXJ;
		}

		if(resource.getFluid() != LiquidXPMod.fluid)
			return 0;
		
		int amount = Math.min(resource.amount, (int)(capacity - storedLiquid));
		if(doFill)
			storedLiquid += amount;
		return amount;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		return null;
	}
	
	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		return null;
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return (fluid == LiquidXPMod.fluid || fluid == LXP_MFR_Compat.mobEssence);
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return false;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		FluidStack contents = storedLiquid < 1 ? null : new FluidStack(LiquidXPMod.fluid, (int)storedLiquid);
		return new FluidTankInfo[] {new FluidTankInfo(contents, capacity)};
	}
}
