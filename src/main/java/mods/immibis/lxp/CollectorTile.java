package mods.immibis.lxp;

import java.util.Iterator;
import java.util.List;

import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.util.AxisAlignedBB;

public class CollectorTile extends LXPEmittingTile {
	
	private static final double RANGE = 6.5;
	
	private static final int CAPACITY = 16000;
	private static final int ATTRACT_LIMIT = 8000; // if storedLiquid >= ATTRACT_LIMIT, it stops working (extra space is left as a buffer.
	
	{
		capacity = CAPACITY;
	}
	
	private int timer = 0;
	private List<EntityXPOrb> entities = null;

	@SuppressWarnings("unchecked")
	@Override
	public void updateEntity() {
		super.updateEntity();
		
		if(worldObj.isRemote)
			return;
		
		timer++;
		
		if(storedLiquid >= ATTRACT_LIMIT && !LiquidXPMod.DESTROY_XP_OVERFLOW)
			return;
		
		if(timer >= 20 || storedLiquid < CAPACITY/4) {
			timer = 0;
			
			entities = (List<EntityXPOrb>)worldObj.getEntitiesWithinAABB(EntityXPOrb.class, AxisAlignedBB.getAABBPool().getAABB(xCoord+0.5-RANGE, yCoord+0.5-RANGE, zCoord+0.5-RANGE, xCoord+0.5+RANGE, yCoord+0.5+RANGE, zCoord+0.5+RANGE));
		}
		
		if(entities == null)
			return;
		
		Iterator<EntityXPOrb> it = entities.iterator();
		while(it.hasNext()) {
			EntityXPOrb orb = it.next();
			
			double dx = xCoord + 0.5 - orb.posX;
			double dy = yCoord + 0.5 - orb.posY;
			double dz = zCoord + 0.5 - orb.posZ;
			double dist = Math.sqrt(dx*dx + dy*dy + dz*dz);
			
			if(dist < 1) {
				orb.setDead();
				it.remove();
				
				storedLiquid = Math.min(CAPACITY, storedLiquid + LiquidXPMod.convertXPToMB(orb.getXpValue()));

			} else {
				
				double scale = (1/dist) * (8 - dist)/8 * 0.1;
				orb.motionX += dx * scale;
				orb.motionY += dy * scale;
				orb.motionZ += dz * scale;
			}
		}
	}
	
	/*
	@Override
	public boolean onBlockActivated(EntityPlayer player) {
		if(worldObj.isRemote)
			return true;
		
		worldObj.setBlockWithNotify(xCoord, yCoord + 1, zCoord, Block.mobSpawner.blockID);
		TileEntityMobSpawner s = (TileEntityMobSpawner)worldObj.getBlockTileEntity(xCoord, yCoord + 1, zCoord);
		
		s.setMobID("Skeleton");
		
		return true;
	}*/

}
