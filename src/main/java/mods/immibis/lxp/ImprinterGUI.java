package mods.immibis.lxp;

import mods.immibis.core.api.util.BaseGuiContainer;
import net.minecraft.client.gui.GuiButton;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ImprinterGUI extends BaseGuiContainer<ImprinterContainer> {

	public ImprinterGUI(ImprinterContainer container) {
		super(container, 186, 190, R.gui.imprinter);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void initGui() {
		super.initGui();
		
		buttonList.add(new GuiButton(0, guiLeft+39, guiTop+71, 20, 20, "-"));
		buttonList.add(new GuiButton(1, guiLeft+61, guiTop+71, 20, 20, "+"));
	}
	
	@Override
	protected void actionPerformed(GuiButton par1GuiButton) {
		super.actionPerformed(par1GuiButton);
		
		container.sendButtonPressed(par1GuiButton.id);
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float var1, int var2, int var3) {
		super.drawGuiContainerBackgroundLayer(var1, var2, var3);
		
		drawTexturedModalRect(guiLeft+44, guiTop+36, 24, 202, container.progress, 16);
		
		fontRendererObj.drawStringWithShadow("Level: "+container.level, 89+guiLeft, 67+guiTop, 0xFFFFFF);
		
		int xp = LiquidXPMod.levelToXP(container.level);
		String bucketStr = String.format("(%2.2f buckets)", LiquidXPMod.convertXPToMB(xp)/1000.0f);
		fontRendererObj.drawStringWithShadow("XP required: "+xp, 89+guiLeft, 78+guiTop, 0xFFFFFF);
		fontRendererObj.drawStringWithShadow(bucketStr, 89+guiLeft, 89+guiTop, 0xFFFFFF);
	}

}
