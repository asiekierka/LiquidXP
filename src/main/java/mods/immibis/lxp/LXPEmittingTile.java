package mods.immibis.lxp;

import mods.immibis.core.TileCombined;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTankInfo;
import net.minecraftforge.fluids.IFluidHandler;

public class LXPEmittingTile extends TileCombined implements IFluidHandler {
	public double storedLiquid = 0;
	public int capacity;
	
	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setDouble("stored", storedLiquid);
		tag.setInteger("capacity", capacity);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		storedLiquid = tag.getDouble("stored");
		capacity = tag.getInteger("capacity");
	}
	
	@Override
	public void updateEntity() {
		super.updateEntity();
		
		if(!worldObj.isRemote && storedLiquid >= 1) {
			for(ForgeDirection fd : ForgeDirection.VALID_DIRECTIONS) {
				int x = xCoord + fd.offsetX, y = yCoord + fd.offsetY, z = zCoord + fd.offsetZ;
				
				TileEntity te = worldObj.getTileEntity(x, y, z);
				if(te instanceof IFluidHandler) {
					IFluidHandler fh = (IFluidHandler)te;
					storedLiquid -= fh.fill(fd.getOpposite(), new FluidStack(LiquidXPMod.fluid, (int)storedLiquid), true);
				}
			}
		}
	}
	
	@Override
	public int fill(ForgeDirection from, FluidStack resource, boolean doFill) {
		return 0;
	}

	@Override
	public FluidStack drain(ForgeDirection from, FluidStack resource, boolean doDrain) {
		if(resource.getFluid() == LiquidXPMod.fluid)
			return drain(from, resource.amount, doDrain);
		else
			return null;
	}
	
	@Override
	public FluidStack drain(ForgeDirection from, int maxDrain, boolean doDrain) {
		maxDrain = Math.min(maxDrain, (int)storedLiquid);
		
		if(doDrain)
			storedLiquid -= maxDrain;
		return maxDrain == 0 ? null : new FluidStack(LiquidXPMod.fluid, maxDrain);
	}

	@Override
	public boolean canFill(ForgeDirection from, Fluid fluid) {
		return false;
	}

	@Override
	public boolean canDrain(ForgeDirection from, Fluid fluid) {
		return fluid == LiquidXPMod.fluid;
	}

	@Override
	public FluidTankInfo[] getTankInfo(ForgeDirection from) {
		FluidStack contains = storedLiquid < 1 ? null : new FluidStack(LiquidXPMod.fluid, (int)storedLiquid);
		return new FluidTankInfo[] {new FluidTankInfo(contains, capacity)};
	}
}
