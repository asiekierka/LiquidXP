package mods.immibis.lxp;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mods.immibis.core.BasicInventory;
import mods.immibis.core.ImmibisCore;
import mods.immibis.core.api.util.Dir;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;

public class ImprinterTile extends LXPAcceptingTile implements ISidedInventory {
	
	private BasicInventory inv = new BasicInventory(2);
	
	@Override
	public List<ItemStack> getInventoryDrops() {
		ArrayList<ItemStack> rv = new ArrayList<ItemStack>(inv.getSizeInventory());
		for(ItemStack is : inv.contents)
			if(is != null)
				rv.add(is);
		Arrays.fill(inv.contents, null);
		return rv;
	}
	
	public ImprinterTile() {
		setLevelTarget(1);
	}
	
	private static int levelToMB[] = new int[31];
	
	static void initLevelTable() {
		for(int k = 1; k <= 30; k++)
			levelToMB[k] = (int)LiquidXPMod.convertXPToMB(LiquidXPMod.levelToXP(k)) * LiquidXPMod.enchantingCostScale / 100;
	}

	public int levelTarget;
	
	public void setLevelTarget(int i) {
		levelTarget = i;
		capacity = levelToMB[i];
	}
	
	@Override
	public void writeToNBT(NBTTagCompound tag) {
		super.writeToNBT(tag);
		tag.setInteger("level", levelTarget);
        tag.setTag("Items", inv.writeToNBT());
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		levelTarget = tag.getInteger("level");
		
		if(levelTarget < 1 || levelTarget > 30) setLevelTarget(1);
		inv.readFromNBT(tag.getTagList("Items", 10));
	}

	private boolean canRun() {
		return inv.contents[0] != null && (inv.contents[0].getItem() instanceof MedallionItem) && inv.contents[0].getItemDamage() == 0;
	}
	
	@Override
	public void updateEntity() {
		super.updateEntity();
		
		acceptingLXP = canRun();
		
		if(acceptingLXP && capacity == 0)
			setLevelTarget(levelTarget);
		
		if(acceptingLXP && storedLiquid >= capacity) {
			ItemStack output = new ItemStack(LiquidXPMod.medallion, 1, levelTarget);
			
			if(inv.contents[1] == null || (inv.contents[1].stackSize < MedallionItem.MAX_STACK && ImmibisCore.areItemsEqual(output, inv.contents[1]))) {
				if(inv.contents[1] == null)
					setInventorySlotContents(1, output);
				else {
					inv.contents[1].stackSize++;
					setInventorySlotContents(1, inv.contents[1]);
				}
				
				storedLiquid -= capacity;
				
				if(--inv.contents[0].stackSize == 0) {
					inv.contents[0] = null;
					acceptingLXP = false;
				}
			}
		}
		
		
	}
	
	@Override
	public boolean onBlockActivated(EntityPlayer player) {
		if(!worldObj.isRemote)
			player.openGui(LiquidXPMod.instance, LiquidXPMod.GUI_IMPRINTER, worldObj, xCoord, yCoord, zCoord);
		return true;
	}

	
	
	@Override
	public int getSizeInventory() {
		return inv.contents.length;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return inv.contents[i];
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		return inv.decrStackSize(i, j);
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i) {
		return null;
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		inv.contents[i] = itemstack;
	}

	@Override
	public String getInventoryName() {
		return "Imprinter";
	}

	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		return entityplayer.getDistanceSq(xCoord+0.5, yCoord+0.5, zCoord+0.5) <= 64 && entityplayer.worldObj == worldObj && !tileEntityInvalid;
	}

	@Override
	public void openInventory() {
	}

	@Override
	public void closeInventory() {
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return true;
	}

	
	// top 0 bottom 1
	private int[] slots_0 = {0};
	private int[] slots_1 = {1};
	private int[] slots_none = {};
	@Override
	public int[] getAccessibleSlotsFromSide(int var1) {
		if(var1 == Dir.PY)
			return slots_0;
		if(var1 == Dir.NY)
			return slots_1;
		return slots_none;
	}

	@Override
	public boolean canInsertItem(int i, ItemStack itemstack, int j) {
		return true;
	}

	@Override
	public boolean canExtractItem(int i, ItemStack itemstack, int j) {
		return true;
	}

	@Override
	public boolean hasCustomInventoryName() {
		return true;
	}
}
