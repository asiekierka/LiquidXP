package mods.immibis.lxp;

import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;
import mods.immibis.core.api.util.BaseContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;

public class EnchanterContainer extends BaseContainer<EnchanterTile> {

	public EnchanterContainer(EntityPlayer player, EnchanterTile inv) {
		super(player, inv);
		
		addSlotToContainer(new Slot(inv, EnchanterTile.BOOK_IN, 20, 36) {{
			setBackgroundIcon(BucketItem.bghost);
		}});
		addSlotToContainer(new Slot(inv, EnchanterTile.MEDALLION_IN, 79, 9) {{
			setBackgroundIcon(BucketItem.mghost);
		}});
		
		addSlotToContainer(new Slot(inv, EnchanterTile.TEMPLATE, 20, 75) {{
			setBackgroundIcon(BucketItem.tghost);
		}});
		
		addSlotToContainer(new Slot(inv, EnchanterTile.BOOK_OUT, 150, 36));
		addSlotToContainer(new Slot(inv, EnchanterTile.MEDALLION_OUT, 79, 75));
		
		for(int x = 0; x < 9; x++)
			addSlotToContainer(new Slot(player.inventory, x, 13 + x*18, 167));
		
		for(int y = 0; y < 3; y++)
			for(int x = 0; x < 9; x++)
				addSlotToContainer(new Slot(player.inventory, x+y*9+9, 13 + x*18, 109 + y*18));
	}

	@SideOnly(Side.CLIENT)
	public int getBookcases() {
		return inv.countBookshelves();
	}
}
