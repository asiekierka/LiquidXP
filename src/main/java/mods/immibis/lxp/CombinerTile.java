package mods.immibis.lxp;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import mods.immibis.core.BasicInventory;
import mods.immibis.core.api.util.Dir;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.util.Vec3;
import net.minecraftforge.common.util.ForgeDirection;

public class CombinerTile extends LXPAcceptingTile implements IInventory, net.minecraft.inventory.ISidedInventory {
	public BasicInventory inv = new BasicInventory(3);
	public int front;
	
	public int requiredXP, receivedXP;
	
	public int requiredLevels;
	
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
		tag.setInteger("req", requiredXP);
		tag.setInteger("rec", receivedXP);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound tag) {
		super.readFromNBT(tag);
		inv.readFromNBT(tag.getTagList("inv", 10));
		front = tag.getInteger("front");
		requiredXP = tag.getInteger("req");
		receivedXP = tag.getInteger("rec");
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
			player.openGui(LiquidXPMod.instance, LiquidXPMod.GUI_COMBINER, worldObj, xCoord, yCoord, zCoord);
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
		return (requiredXP == 0 ? 0 : (receivedXP * 42) / requiredXP);
	}
	
	@Override
	public void updateEntity() {
		if(worldObj.isRemote)
			return;
		
		if(requiredXP > 0 && inv.contents[2] == null) {
			requiredXP = getXPRequirement(); // they might change the input items
			
			if(requiredXP < 0) {
				reset();
			} else {
				
				int transfer = Math.min(requiredXP - receivedXP, (int)LiquidXPMod.convertMBToXP(storedLiquid));
				
				storedLiquid -= LiquidXPMod.convertXPToMB(transfer);
				receivedXP += transfer;
				
				capacity = (int)Math.ceil(LiquidXPMod.convertXPToMB(requiredXP - receivedXP));

				if(receivedXP >= requiredXP) {
					finish();
					requiredXP = receivedXP = 0;
				}
			}
		} else {
			if(requiredXP > 0) {
				reset();
			}
			if(inv.contents[2] == null)
				requiredXP = getXPRequirement();
		}
	}
	
	private void reset() {
		// stop running, dissipate XP; don't do that if you don't want to waste XP.
		requiredXP = 0;
		receivedXP = 0;
		capacity = 0;
		storedLiquid = 0;
		requiredLevels = 0;
	}
	
	private ItemStack resultingItem = null;

	private void finish() {
		if(resultingItem != null) {
			//decrStackSize(0, 1);
			//decrStackSize(1, 1);
			setInventorySlotContents(0, null);
			setInventorySlotContents(1, null);
			setInventorySlotContents(2, resultingItem);
			resultingItem = null;
		}
	}
	
	private int getEnchantmentLevelCost(Enchantment e) {
		// what vanilla does
		switch (e.getWeight())
        {
            case 1:
                return 8;
            case 2:
                return 4;
            case 5:
                return 2;
            case 10:
                return 1;
            default:
                return 0;
        }
	}

	@SuppressWarnings("unchecked")
	private int getXPRequirement() {
		ItemStack in1 = getStackInSlot(0);
        int levels = 0;
        byte var3 = 0;

        if (in1 == null)
        {
            return -1;
        }
        else
        {
        	ItemStack in2 = getStackInSlot(1);
        	
        	if(in2 == null) {
        		return -1;
        	}
            
        	if(in1.getItem().equals(Items.enchanted_book)) {
        		// swap items
        		ItemStack temp = in2;
        		in2 = in1;
        		in1 = temp;
        	}

            ItemStack out = in1.copy();
            Map<Integer, Integer> ench1 = EnchantmentHelper.getEnchantments(out);
            boolean isEnchantedBook = false;
            int levels2 = var3;// + input1_2.getRepairCost() + (in2 == null ? 0 : in2.getRepairCost());
            int var10;
            int enchantmentID;
            int enchLevel1;
            int enchLevel2;
            Iterator<Integer> ench2_it;
            Enchantment enchantment;
            
            isEnchantedBook = in2.getItem().equals(Items.enchanted_book) && Items.enchanted_book.func_92110_g(in2).tagCount() > 0;
            
            boolean isOutputEnchantedBook = out.getItem().equals(Items.enchanted_book);

            if (out.isItemStackDamageable() && out.getItem().getIsRepairable(in1, in2))
            {
            	// eg diamond tools + diamonds
            	
            	// repair 1/4 of max damage with each item
                int amountRepairedPerItem = Math.min(out.getItemDamageForDisplay(), out.getMaxDamage() / 4);

                if (amountRepairedPerItem <= 0)
                {
                    return -1;
                }

                for (var10 = 0; amountRepairedPerItem > 0 && var10 < in2.stackSize; ++var10)
                {
                    enchantmentID = out.getItemDamageForDisplay() - amountRepairedPerItem;
                    out.setItemDamage(enchantmentID);
                    levels += Math.max(1, amountRepairedPerItem / 100) + ench1.size();
                    amountRepairedPerItem = Math.min(out.getItemDamageForDisplay(), out.getMaxDamage() / 4);
                }
            }
            else
            {
                if (!isEnchantedBook && (!out.getItem().equals(in2.getItem()) || !out.isItemStackDamageable()))
                {
                    return -1;
                }

                if (out.isItemStackDamageable() && !isEnchantedBook)
                {
                	// repairing
                    int remaining1 = in1.getMaxDamage() - in1.getItemDamageForDisplay();
                    int remaining2 = in2.getMaxDamage() - in2.getItemDamageForDisplay();
                    enchantmentID = remaining2 + out.getMaxDamage() * 12 / 100; // 12% extra uses
                    int var12 = remaining1 + enchantmentID;
                    enchLevel1 = out.getMaxDamage() - var12;

                    if (enchLevel1 < 0)
                    {
                        enchLevel1 = 0;
                    }

                    if (enchLevel1 < out.getItemDamage())
                    {
                        out.setItemDamage(enchLevel1);
                        levels += Math.max(1, enchantmentID / 100); // 1 level per 100 uses repaired
                    }
                }

                Map<Integer, Integer> ench2 = EnchantmentHelper.getEnchantments(in2);
                ench2_it = ench2.keySet().iterator();

                while (ench2_it.hasNext())
                {
                    enchantmentID = ((Integer)ench2_it.next()).intValue();
                    enchantment = Enchantment.enchantmentsList[enchantmentID];
                    if(enchantment == null)
                    	return -1; // invalid enchantment
                    
                    enchLevel1 = ench1.containsKey(Integer.valueOf(enchantmentID)) ? ((Integer)ench1.get(Integer.valueOf(enchantmentID))).intValue() : 0;
                    enchLevel2 = ((Integer)ench2.get(Integer.valueOf(enchantmentID))).intValue();
                    int enchLevelOut;

                    if (enchLevel1 == enchLevel2)
                    {
                        ++enchLevel2;
                        enchLevelOut = enchLevel2;
                    }
                    else
                    {
                        enchLevelOut = Math.max(enchLevel2, enchLevel1);
                    }

                    enchLevel2 = enchLevelOut;
                    int var15 = enchLevel2 - enchLevel1;
                    boolean isTypeApplicable = enchantment.canApply(in1) || isOutputEnchantedBook;

                    Iterator<Integer> var17 = ench1.keySet().iterator();

                    while (var17.hasNext())
                    {
                        int var18 = ((Integer)var17.next()).intValue();

                        if(Enchantment.enchantmentsList[var18] == null)
                        	return -1; // invalid enchantment
                        
                        if (var18 != enchantmentID && !enchantment.canApplyTogether(Enchantment.enchantmentsList[var18]))
                        {
                            isTypeApplicable = false;
                            //levels += var15; // removed because stupid - it costs levels to NOT apply an enchantment???
                        }
                    }

                    if (isTypeApplicable)
                    {
                        if (enchLevel2 > enchantment.getMaxLevel())
                        {
                            enchLevel2 = enchantment.getMaxLevel();
                        }

                        ench1.put(Integer.valueOf(enchantmentID), Integer.valueOf(enchLevel2));
                        int var23 = 0;

                        var23 = getEnchantmentLevelCost(enchantment);

                        if (isEnchantedBook)
                        {
                        	// it costs half as much to apply an enchantment from a book
                        	// than it does to take it from an enchanted item
                            var23 = Math.max(1, var23 / 2);
                        }

                        levels += var23 * var15;
                    }
                }
            }

            for (ench2_it = ench1.keySet().iterator(); ench2_it.hasNext();)
            {
                enchantmentID = ((Integer)ench2_it.next()).intValue();
                enchantment = Enchantment.enchantmentsList[enchantmentID];
                if(enchantment == null)
                	return -1; // invalid enchantment
                
                enchLevel1 = ((Integer)ench1.get(Integer.valueOf(enchantmentID))).intValue();
                int enchCost = 0;

                enchCost = getEnchantmentLevelCost(enchantment);

                if (isEnchantedBook)
                {
                	enchCost = Math.max(1, enchCost / 2);
                }
                
                levels2 += enchLevel1 * enchCost;
            }
            
            levels2 += (ench1.size() * (ench1.size() + 1)) / 2;

            if (isEnchantedBook)
            {
                levels2 = Math.max(1, levels2 / 2);
            }
            
            levels += levels2;
            
            EnchantmentHelper.setEnchantments(ench1, out);
            
            if(isEnchantedBook && !out.getItem().isBookEnchantable(in1, in2))
            	return -1;
            
            resultingItem = out;
            
            requiredLevels = levels;

            return LiquidXPMod.levelToXP(levels) * LiquidXPMod.combiningCostScale / 100;
            
            /*if (out != null)
            {
                var10 = out.getRepairCost();

                if (in2 != null && var10 < in2.getRepairCost())
                {
                    var10 = in2.getRepairCost();
                }

                if (out.hasDisplayName())
                {
                    var10 -= 9;
                }

                if (var10 < 0)
                {
                    var10 = 0;
                }

                var10 += 2;
                out.setRepairCost(var10);
                EnchantmentHelper.setEnchantments(ench1, out);
            }

            this.outputSlot.setInventorySlotContents(0, out);
            this.updateCraftingResults();*/
        }
	}
	
	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		return true;
	}

	
	private static int[] sides_0 = new int[] {0};
	private static int[] sides_1 = new int[] {1};
	private static int[] sides_2 = new int[] {2};
	private static int[] sides_none = new int[] {};
	private static int[] sides_01 = new int[] {0, 1};
	
	@Override
	public int[] getAccessibleSlotsFromSide(int side) {
		int left = ForgeDirection.ROTATION_MATRIX[Dir.PY][front];
		if(side == left)
			return sides_0;
		if(side == (left ^ 1))
			return sides_1;
		if(side == Dir.NY)
			return sides_2;
		if(side == Dir.PY)
			return sides_01;
		return sides_none;
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
