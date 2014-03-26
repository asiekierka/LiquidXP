package mods.immibis.lxp;


import java.util.List;

import mods.immibis.core.TileCombined;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.S1FPacketSetExperience;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraftforge.common.util.ForgeDirection;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.IFluidHandler;

public class AbsorberTile extends TileCombined {
	
	private static final int CAPACITY = 1000;
	private double storedLiquid = 0;
	
	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setDouble("stored", storedLiquid);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		storedLiquid = tag.getDouble("stored");
	}

	private void subExperience(EntityPlayer pl, double amt) {
	
		while(amt > 0) {
			double _this = Math.min(amt, pl.experience * pl.xpBarCap());
			pl.experience -= _this / pl.xpBarCap();
			amt -= _this;
			if(pl.experience < 0.001) {
				if(pl.experienceLevel == 0) {
					pl.experience = 0;
					break;
				}
				pl.experienceLevel--;
				pl.experience = 1;
				
				// this sound effect is broken sometimes
				/*if((pl.experienceLevel % 5) == 0) {
					float pitch = Math.min(pl.experienceLevel, 30) * (0.3f / 30) + 0.6f;
					//System.out.println("Pitch "+pitch);
					pl.worldObj.playSoundAtEntity(pl, "random.levelup", 0.75F, pitch);
				}*/
			}
		}
		// these are wrong as experienceTotal is not decreased when the player enchants
		//pl.experienceLevel = LiquidXPMod.xpToLevel(pl.experienceTotal); // make sure level is correct
		//pl.experience = (pl.experienceTotal - LiquidXPMod.levelToXP(pl.experienceLevel)) / (float)pl.xpBarCap(); // and XP bar percentage
		
		if(pl instanceof EntityPlayerMP)
			((EntityPlayerMP)pl).playerNetServerHandler.sendPacket(
				new S1FPacketSetExperience(pl.experience, pl.experienceTotal, pl.experienceLevel));
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void updateEntity() {
		if(worldObj.isRemote)
			return;
		
		for(EntityPlayer pl : (List<EntityPlayer>)worldObj.getEntitiesWithinAABB(EntityPlayer.class, AxisAlignedBB.getAABBPool().getAABB(xCoord, yCoord, zCoord, xCoord+1, yCoord+3, zCoord+1))) {
			int realXPTotal = LiquidXPMod.levelToXP(pl.experienceLevel) + (int)(pl.experience * pl.xpBarCap());
			int amt = (int)Math.min(LiquidXPMod.convertMBToXP(CAPACITY - storedLiquid), realXPTotal);
			if(amt > 0) {
				subExperience(pl, amt);
				storedLiquid += LiquidXPMod.convertXPToMB(amt);
			}
		}
		
		if(storedLiquid < 0)
			storedLiquid = 0; // shouldn't happen
		
		for(ForgeDirection dir : ForgeDirection.VALID_DIRECTIONS) {
			if(storedLiquid < 1)
				break;
			
			int x = xCoord + dir.offsetX;
			int y = yCoord + dir.offsetY;
			int z = zCoord + dir.offsetZ;
			
			TileEntity te = worldObj.getTileEntity(x, y, z);
			if(te instanceof IFluidHandler) {
				IFluidHandler itc = (IFluidHandler)te;
				storedLiquid -= itc.fill(dir.getOpposite(), new FluidStack(LiquidXPMod.fluid, (int)storedLiquid), true);
			}
		}
	}
}
