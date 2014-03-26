package mods.immibis.lxp;

import java.util.List;

import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.client.resources.I18n;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IIcon;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class MedallionItem extends Item {
	public static final int MAX_STACK = 4;
	
	public MedallionItem() {
		super();
		
		setHasSubtypes(true);
		setCreativeTab(LiquidXPMod.tab);
		setMaxStackSize(MAX_STACK);
	}
	
	private IIcon iGlowing, iBlank;
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerIcons(IIconRegister reg) {
		iGlowing = reg.registerIcon(R.item.mglowing);
		iBlank = reg.registerIcon(R.item.mblank);
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	@SideOnly(Side.CLIENT)
	public void getSubItems(Item par1, CreativeTabs par2CreativeTabs, List par3List) {
		par3List.add(new ItemStack(this, 1, 0));
		par3List.add(new ItemStack(this, 1, 1));
		par3List.add(new ItemStack(this, 1, 5));
		par3List.add(new ItemStack(this, 1, 10));
		par3List.add(new ItemStack(this, 1, 20));
		par3List.add(new ItemStack(this, 1, 30));
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIconFromDamage(int par1) {
		return (par1 == 0 ? iBlank : iGlowing);
	}
	
	@Override
	public String getUnlocalizedName(ItemStack par1ItemStack) {
		return "item.liquidxp.medallion."+(par1ItemStack.getItemDamage() != 0 ? "charged" : "empty");
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	@SideOnly(Side.CLIENT)
	public void addInformation(ItemStack par1ItemStack, EntityPlayer par2EntityPlayer, List par3List, boolean par4) {
		super.addInformation(par1ItemStack, par2EntityPlayer, par3List, par4);
		
		if(par1ItemStack.getItemDamage() != 0)
			par3List.add(I18n.format("item.liquidxp.medallion.level", par1ItemStack.getItemDamage()));
	}
}
