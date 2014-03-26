package mods.immibis.lxp;

import mods.immibis.core.api.util.BaseContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class CombinerContainer extends BaseContainer<CombinerTile> {

	public CombinerContainer(EntityPlayer player, CombinerTile inv) {
		super(player, inv);
		
		addSlotToContainer(new Slot(inv, 0, 18, 10));
		addSlotToContainer(new Slot(inv, 1, 49, 10));
		addSlotToContainer(new Slot(inv, 2, 34, 75));
		
		for(int x = 0; x < 9; x++)
			addSlotToContainer(new Slot(player.inventory, x, 13 + x*18, 167));
		
		for(int y = 0; y < 3; y++)
			for(int x = 0; x < 9; x++)
				addSlotToContainer(new Slot(player.inventory, x+y*9+9, 13 + x*18, 109 + y*18));
	}
	
	@Override
	public void detectAndSendChanges() {
		super.detectAndSendChanges();
		
		setProgressBar(0, inv.getProgress());
		setProgressBar(1, inv.requiredXP);
		setProgressBar(2, inv.requiredLevels);
	}
	
	@Override
	public void addCraftingToCrafters(ICrafting par1iCrafting) {
		super.addCraftingToCrafters(par1iCrafting);
		
		setProgressBar(0, inv.getProgress());
		setProgressBar(1, inv.requiredXP);
		setProgressBar(2, inv.requiredLevels);
	}
	
	public int progress, reqXP, reqLevels;
	
	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int par1, int par2) {
		if(par1 == 0)
			progress = par2;
		if(par1 == 1)
			reqXP = par2;
		if(par1 == 2)
			reqLevels = par2;
	}
}
