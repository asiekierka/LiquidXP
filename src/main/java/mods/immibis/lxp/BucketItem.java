package mods.immibis.lxp;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.util.IIcon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class BucketItem extends Item {
	public BucketItem() {
		super();
		
		setUnlocalizedName("liquidxp.bucket");
		setTextureName(R.item.bucket);
		
		setCreativeTab(LiquidXPMod.tab);
		
		setContainerItem(Items.bucket);
		setMaxStackSize(1);
	}
	
	
	
	// load some GUI icons here to get access to an IconRegister
	
	public static IIcon mghost, bghost, tghost;
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister reg) {
		super.registerIcons(reg);
		
		mghost = reg.registerIcon("liquidxp:mghost");
		bghost = reg.registerIcon("liquidxp:bghost");
		tghost = reg.registerIcon("liquidxp:tghost");
	}
}
