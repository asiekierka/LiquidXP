package mods.immibis.lxp;

import mods.immibis.core.api.util.BaseGuiContainer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EnchanterGUI extends BaseGuiContainer<EnchanterContainer> {

	public EnchanterGUI(EnchanterContainer container) {
		super(container, 186, 190, R.gui.enchanter);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
		super.drawGuiContainerBackgroundLayer(var1, var2, var3);
		
		int bookcases = container.getBookcases();
		
		drawString("Bookcases: "+bookcases, 103, 62, 0xFFFFFF);
		drawString("Max level: "+(bookcases/2), 103, 73, 0xFFFFFF);
	}
}
