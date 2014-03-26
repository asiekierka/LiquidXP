package mods.immibis.lxp;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidRegistry.FluidRegisterEvent;

class LXP_MFR_Compat {
	public static final String MOB_ESSENCE_NAME = "mobessence";
	
	public static double convertMobEssenceToLiquidXp(double mb) {
		return LiquidXPMod.convertXPToMB(mb * LiquidXPMod.xpPerMobEssenceBucket / 1000.0);
	}
	
	public static double convertLiquidXpToMobEssence(double mb) {
		return LiquidXPMod.convertMBToXP(mb) / (LiquidXPMod.xpPerMobEssenceBucket / 1000.0);
	}
	
	public static Fluid mobEssence;
	
	public static void init() {
		mobEssence = FluidRegistry.getFluid(MOB_ESSENCE_NAME);
		
		MinecraftForge.EVENT_BUS.register(new EventListener());
	}
	
	public static class EventListener {
		@SubscribeEvent
		public void onLiquidRegister(FluidRegisterEvent evt) {
			if(evt.fluidName.equals(MOB_ESSENCE_NAME))
				mobEssence = FluidRegistry.getFluid(evt.fluidName);
		}
	}
}
