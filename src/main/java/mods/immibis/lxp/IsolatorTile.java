package mods.immibis.lxp;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import mods.immibis.core.BasicInventory;
import mods.immibis.core.api.util.Dir;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.util.Vec3;

public class IsolatorTile extends LXPAcceptingTile implements IInventory, net.minecraft.inventory.ISidedInventory {
	public BasicInventory inv = new BasicInventory(3);
	public int front;
	
	private int XP_PER_SPLIT = LiquidXPMod.xpPerIsolatorOperation;
	private int MB_PER_SPLIT = (int)(LiquidXPMod.convertXPToMB(XP_PER_SPLIT));
	
	@Override
	public List<ItemStack> getInventoryDrops() {
		ArrayList<ItemStack> rv = new ArrayList<ItemStack>(inv.getSizeInventory());
		for(ItemStack is : inv.contents)
			if(is != null)
				rv.add(is);
		Arrays.fill(inv.contents, null);
		return rv;
	}
	
	@Override
	public Packet getDescriptionPacket() {
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, front, null);
	}
	
	@Override
	public void onDataPacket(S35PacketUpdateTileEntity packet) {
		front = packet.func_148853_f();
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setTag("inv", inv.writeToNBT());
		tag.setInteger("front", front);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		inv.readFromNBT(tag.getTagList("inv", 10));
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
	public int getSizeInventory() {
		return inv.contents.length;
	}

	@Override
	public ItemStack getStackInSlot(int var1) {
		return inv.contents[var1];
	}

	@Override
	public ItemStack decrStackSize(int var1, int var2) {
		return inv.decrStackSize(var1, var2);
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int var1) {
		return null;
	}

	@Override
	public void setInventorySlotContents(int var1, ItemStack var2) {
		inv.contents[var1] = var2;
	}

	@Override
	public String getInventoryName() {
		return null;
	}
	
	@Override
	public boolean onBlockActivated(EntityPlayer player) {
		if(!worldObj.isRemote)
			player.openGui(LiquidXPMod.instance, LiquidXPMod.GUI_ISOLATOR, worldObj, xCoord, yCoord, zCoord);
		return true;
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer var1) {
		return !tileEntityInvalid && var1.getDistanceSq(xCoord+0.5, yCoord+0.5, zCoord+0.5) <= 64;
	}

	@Override
	public void openInventory() {
	}

	@Override
	public void closeInventory() {
	}

	public int getProgress() {
		return capacity == 0 ? 0 : (int)((storedLiquid * 78) / capacity + 0.5);
	}
	
	@Override
	public void updateEntity() {
		if(worldObj.isRemote)
			return;
		
		acceptingLXP = false;
		capacity = MB_PER_SPLIT;
		
		final int INSLOT = 0, OUTSLOT = 2, BOOKSLOT = 1;
		
		ItemStack input = inv.contents[INSLOT];
		if(input == null || inv.contents[OUTSLOT] != null)
			return;
		
		@SuppressWarnings("unchecked")
		Map<Integer, Integer> enchantments = EnchantmentHelper.getEnchantments(input);
		
		if(input.getItem().isDamageable() && input.getItemDamage() > 0) {
			enchantments.clear();
		}
		
		if(enchantments.size() == 0 || (input.getItem().equals(Items.enchanted_book) && enchantments.size() == 1)) {
			inv.contents[OUTSLOT] = inv.contents[INSLOT];
			inv.contents[INSLOT] = null;
			return;
		}
		
		ItemStack books = inv.contents[BOOKSLOT];
		
		if(books == null || !books.getItem().equals(Items.book))
			return;
		
		if(storedLiquid < capacity) {
			acceptingLXP = true;
			
		} else {
			Map.Entry<Integer, Integer> ench = enchantments.entrySet().iterator().next();
			
			enchantments.remove(ench.getKey());
			if(input.getItem().equals(Items.enchanted_book) && input.hasTagCompound())
				input.getTagCompound().setTag("StoredEnchantments", new NBTTagList()); // clear enchantments
			EnchantmentHelper.setEnchantments(enchantments, input);
			
			ItemStack output = new ItemStack(Items.enchanted_book);
			if(--books.stackSize == 0)
				inv.contents[BOOKSLOT] = null;
			enchantments.clear();
			enchantments.put(ench.getKey(), ench.getValue());
			EnchantmentHelper.setEnchantments(enchantments, output);
			inv.contents[OUTSLOT] = output;
			
			storedLiquid = 0;
		}
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		if(i == 1) // book slot
			return itemstack == null || itemstack.getItem().equals(Items.book);
		return true;
	}

	
	private static int[] sides_in = new int[] {0};
	private static int[] sides_out = new int[] {2};
	private static int[] sides_book = new int[] {1};
	
	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		if(side == Dir.PY)
			return sides_in;
		if(side == Dir.NY)
			return sides_out;
		return sides_book;
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
	public boolean hasCustomInventoryName() {
		return false;
	}	
}
