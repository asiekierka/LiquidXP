package mods.immibis.lxp;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidRegistry.FluidRegisterEvent;

class LXP_OpenBlocks_Compat {
	public static final String XP_JUICE_NAME = "xpjuice";
	public static final int XP_JUICE_MB_PER_XP = 20;
	
	public static double convertXpJuiceToLiquidXp(double mb) {
		return LiquidXPMod.convertXPToMB(mb * LiquidXPMod.xpPerXPJuiceBucket / 1000.0);
	}
	
	public static double convertLiquidXpToXpJuice(double mb) {
		return LiquidXPMod.convertMBToXP(mb) / (LiquidXPMod.xpPerXPJuiceBucket / 1000.0);
	}
	
	public static Fluid xpJuice;
	
	public static void init() {
		xpJuice = FluidRegistry.getFluid(XP_JUICE_NAME);
		
		MinecraftForge.EVENT_BUS.register(new EventListener());
	}
	
	public static class EventListener {
		@SubscribeEvent
		public void onLiquidRegister(FluidRegisterEvent evt) {
			if(evt.fluidName.equals(XP_JUICE_NAME))
				xpJuice = FluidRegistry.getFluid(evt.fluidName);
		}
	}
}
