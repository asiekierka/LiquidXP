package mods.immibis.lxp;

import mods.immibis.core.api.util.BaseGuiContainer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class CombinerGUI extends BaseGuiContainer<CombinerContainer> {
	public CombinerGUI(CombinerContainer c) {
		super(c, 186, 190, R.gui.combiner);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
		super.drawGuiContainerBackgroundLayer(var1, var2, var3);
		
		drawTexturedModalRect(guiLeft+24, guiTop+31, 190, 7, 35, container.progress);
		
		if(container.reqXP > 0) {
			int mB = (int)Math.ceil(LiquidXPMod.convertXPToMB(container.reqXP));
			String bucketStr = String.format("(%2.2f buckets)", mB/1000.0f);
			
			drawString("XP needed: "+container.reqXP, 75, 11, 0xFFFFFF);
			drawString(bucketStr, 75, 26, 0xFFFFFF);
			drawString("Levels needed: "+container.reqLevels, 75, 41, 0xFFFFFF);
		}
	}
}
