package mods.immibis.lxp;

import mods.immibis.core.api.util.BaseContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class ImprinterContainer extends BaseContainer<ImprinterTile> {

	public int level;
	public int progress;

	public ImprinterContainer(EntityPlayer player, ImprinterTile inv) {
		super(player, inv);
		
		addSlotToContainer(new Slot(inv, 0, 20, 36));
		addSlotToContainer(new Slot(inv, 1, 150, 36));
		
		for(int x = 0; x < 9; x++)
			addSlotToContainer(new Slot(player.inventory, x, 13 + x*18, 167));
		
		for(int y = 0; y < 3; y++)
			for(int x = 0; x < 9; x++)
				addSlotToContainer(new Slot(player.inventory, x+y*9+9, 13 + x*18, 109 + y*18));
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int par1, int par2) {
		if(par1 == 0)
			level = par2;
		else if(par1 == 1)
			progress = par2;
	}
	
	@Override
	public void detectAndSendChanges() {
		// TODO Auto-generated method stub
		super.detectAndSendChanges();
		
		setProgressBar(0, inv.levelTarget);
		setProgressBar(1, getProgress());
	}
	
	private int getProgress() {
		return inv.capacity == 0 ? 0 : (int)Math.round((inv.storedLiquid * 97) / inv.capacity);
	}
	
	@Override
	public void addCraftingToCrafters(ICrafting par1iCrafting) {
		super.addCraftingToCrafters(par1iCrafting);
		
		setProgressBar(0, inv.levelTarget);
		setProgressBar(1, getProgress());
	}
	
	@Override
	public void onButtonPressed(int id) {
		if(id == 0 && inv.levelTarget > 1) inv.setLevelTarget(inv.levelTarget - 1);
		if(id == 1 && inv.levelTarget < 30) inv.setLevelTarget(inv.levelTarget + 1);
	}

}
