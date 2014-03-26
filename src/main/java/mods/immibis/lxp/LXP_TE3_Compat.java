package mods.immibis.lxp;

import cpw.mods.fml.common.event.FMLInterModComms;
import net.minecraft.nbt.NBTTagCompound;

class LXP_TE3_Compat {
	public static void init() {
		
		if(LiquidXPMod.reactantDynamoRFPerXP == 0)
			return;
		
		int rfPerBucket = (int)(LiquidXPMod.reactantDynamoRFPerXP * LiquidXPMod.convertMBToXP(1000));
		
		NBTTagCompound toSend = new NBTTagCompound();
		toSend.setString("fluidName", LiquidXPMod.fluid.getName());
		toSend.setInteger("energy", rfPerBucket);
		FMLInterModComms.sendMessage("ThermalExpansion", "ReactantFuel", toSend);
	}
}
