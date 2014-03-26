package mods.immibis.lxp;

import mods.immibis.core.api.util.BaseGuiContainer;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class IsolatorGUI extends BaseGuiContainer<IsolatorContainer> {
	public IsolatorGUI(IsolatorContainer c) {
		super(c, 186, 190, R.gui.isolator);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
		super.drawGuiContainerBackgroundLayer(var1, var2, var3);
		
		drawTexturedModalRect(guiLeft+49, guiTop+21, 2, 191, container.progress, 18);
	}
}
