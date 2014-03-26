package mods.immibis.lxp;


import java.util.List;

import mods.immibis.core.TileBasicInventory;
import mods.immibis.core.api.util.Dir;
import net.minecraft.block.Block;
import net.minecraft.enchantment.EnchantmentData;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.util.ForgeDirection;

public class EnchanterTile extends TileBasicInventory implements ISidedInventory {
	public EnchanterTile() {
		super(5, "LXP Enchanter");
	}

	public int front;

	public int clientTicks;
	
	// slot numbers
	public static final int MEDALLION_IN = 0;
	public static final int BOOK_IN = 1;
	public static final int BOOK_OUT = 2;
	public static final int MEDALLION_OUT = 3;
	public static final int TEMPLATE = 4;
	
	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setInteger("front", front);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		front = tag.getInteger("front");
	}
	
	@Override
	public void onPlaced(EntityLivingBase player, int _) {
		Vec3 look = player.getLook(1.0f);
		
        double absx = Math.abs(look.xCoord);
        double absz = Math.abs(look.zCoord);
        
        if(absx > absz) {
        	if(look.xCoord < 0)
        		front = Dir.PX;
        	else
        		front = Dir.NX;
        } else {
        	if(look.zCoord < 0)
        		front = Dir.PZ;
        	else
        		front = Dir.NZ;
        }
	}
	
	@Override
	public Packet getDescriptionPacket() {
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, front, null);
	}
	
	@Override
	public void onDataPacket(S35PacketUpdateTileEntity packet) {
		front = packet.func_148853_f();
		worldObj.markBlockForUpdate(xCoord, yCoord, zCoord);
	}

	private int[] slots_med_out = {MEDALLION_OUT};
	private int[] slots_med_in = {MEDALLION_IN};
	private int[] slots_template = {TEMPLATE};
	private int[] slots_book_in = {BOOK_IN};
	private int[] slots_book_out = {BOOK_OUT};
	private int[] slots_in = {};
	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		if(side == Dir.NY)
			return slots_med_out;
		if(side == Dir.PY)
			return slots_med_in;
		if(side == (front ^ 1))
			return slots_template;
		int left = ForgeDirection.ROTATION_MATRIX[Dir.PY][front];
		if(side == left)
			return slots_book_in;
		if(side == (left ^ 1))
			return slots_book_out;
		return slots_in;
	}
	
	@Override
	public boolean canExtractItem(int i, ItemStack itemstack, int j) {
		return true;
	}
	
	@Override
	public boolean canInsertItem(int i, ItemStack itemstack, int j) {
		return true;
	}
	
	@Override
	public boolean onBlockActivated(EntityPlayer player) {
		if(!worldObj.isRemote)
			player.openGui(LiquidXPMod.instance, LiquidXPMod.GUI_ENCHANTER, worldObj, xCoord, yCoord, zCoord);
		return true;
	}
	
	@Override
	public void updateEntity() {
		if(worldObj.isRemote) {
			clientTicks++;
			return;
		}
		
		if(inv.contents[BOOK_OUT] != null)
			return;
		
		ItemStack mo = inv.contents[MEDALLION_OUT];
		if(mo != null && (mo.stackSize >= MedallionItem.MAX_STACK || !(mo.getItem() instanceof MedallionItem) || mo.getItemDamage() != 0))
			return;
		
		ItemStack mi = inv.contents[MEDALLION_IN];
		if(mi == null || !(mi.getItem() instanceof MedallionItem) || mi.getItemDamage() == 0)
			return;
		
		ItemStack bi = inv.contents[BOOK_IN];
		if(bi == null)
			return;
		
		if(!(bi.getItem().equals(Items.book)) && LiquidXPMod.ONLY_ENCHANT_BOOKS)
			return;
		
		int level = mi.getItemDamage();
		
		// 60 bookshelves required for max level
		if(level > countBookshelves() / 2)
			return;
		
		ItemStack template = inv.contents[TEMPLATE];
		ItemStack bo = enchant(bi.copy(), level, template);
		if(template != null && template.stackSize <= 0)
			setInventorySlotContents(TEMPLATE, null);
		
		if(bo == null)
			return;
		
		bi.stackSize--;
		mi.stackSize--;
		setInventorySlotContents(BOOK_IN, bi.stackSize == 0 ? null : bi);
		setInventorySlotContents(MEDALLION_IN, mi.stackSize == 0 ? null : mi);
		
		if(mo != null)
			mo.stackSize++;
		else
			mo = new ItemStack(LiquidXPMod.medallion, 1, 0);
		setInventorySlotContents(MEDALLION_OUT, mo);
		
		setInventorySlotContents(BOOK_OUT, bo);
	}
	
	public int countBookshelves() {
		int n = 0;
		int x = xCoord, z = zCoord;
		for(int dy = -1; dy <= 2; dy++) {
			int y = yCoord + dy;
			
			for(int dx = -3; dx <= 3; dx++)
				for(int dz = -3; dz <= 3; dz++)
					if(worldObj.getBlock(x+dx, y, z+dz).equals(Blocks.bookshelf))
						n++;
		}
		return n;
	}
	
	public ItemStack enchant(ItemStack stack, int level, ItemStack template) {
		stack.stackSize = 1;
		
		if(!stack.isItemEnchantable())
			return null;
		
		@SuppressWarnings("unchecked")
		List<EnchantmentData> enchantments = EnchantmentHelper.buildEnchantmentList(worldObj.rand, template == null ? stack : template, level);
		
		if(enchantments == null)
			return null;

		if(template != null && template.isItemStackDamageable()) {
			int newDamage = template.getItemDamage() + level * 4;
			if(newDamage > template.getMaxDamage())
				template.stackSize = 0;
			else
				template.setItemDamage(newDamage);
		}
		
		if(stack.getItem().equals(Items.book))
			stack = new ItemStack(Items.enchanted_book, 1);
        
        // normal enchanting only picks one enchantment if enchanting a book
        // but we use them all
        
        for(EnchantmentData enchantment : enchantments) {
        	if(stack.getItem().equals(Items.enchanted_book))
        		Items.enchanted_book.addEnchantment(stack, enchantment);
        	else
        		stack.addEnchantment(enchantment.enchantmentobj, enchantment.enchantmentLevel);
        }
        
        return stack;
	}
}
