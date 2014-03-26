package mods.immibis.lxp;

import mods.immibis.core.api.util.BaseContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ICrafting;
import net.minecraft.inventory.Slot;
import net.minecraft.util.IIcon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class IsolatorContainer extends BaseContainer<IsolatorTile> {

	public IsolatorContainer(EntityPlayer player, IsolatorTile inv) {
		super(player, inv);
		
		// book slot must be first, or ghost renders in a weird colour if an item rendered before it has the enchantment glow effect 
		addSlotToContainer(new Slot(inv, 1, 29, 45) {
			@Override
			public IIcon getBackgroundIconIndex() {
				return BucketItem.bghost;
			}
		});
		addSlotToContainer(new Slot(inv, 0, 29, 22));
		addSlotToContainer(new Slot(inv, 2, 130, 22));
		
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
	}
	
	@Override
	public void addCraftingToCrafters(ICrafting par1iCrafting) {
		super.addCraftingToCrafters(par1iCrafting);
		
		setProgressBar(0, inv.getProgress());
	}
	
	public int progress;
	
	@Override
	@SideOnly(Side.CLIENT)
	public void updateProgressBar(int par1, int par2) {
		if(par1 == 0)
			progress = par2;
	}
}
