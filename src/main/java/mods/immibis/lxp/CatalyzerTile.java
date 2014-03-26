package mods.immibis.lxp;

import java.util.List;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.AxisAlignedBB;
import cpw.mods.fml.relauncher.ReflectionHelper;

public class CatalyzerTile extends LXPAcceptingTile {
	private static final double RANGE = 6.5;
	
	private final int COST_MB;
	
	{
		COST_MB = (int)LiquidXPMod.convertXPToMB(LiquidXPMod.xpPerCatalyzedMob); // mB per entity catalyzed
		capacity = COST_MB * 20;
	}
	
	// unused
	/* public class FakePlayer extends EntityPlayer {
		public FakePlayer(World worldObj) {
			super(worldObj);
			username = "[Catalyzer]";
			setPosition(xCoord+0.5, yCoord+0.5, zCoord+0.5);
		}

		@Override
		public void sendChatToPlayer(String var1) {
		}

		@Override
		public boolean canCommandSenderUseCommand(int var1, String var2) {
			return false;
		}

		@Override
		public ChunkCoordinates getPlayerCoordinates() {
			return null;
		}
	} */
	
	private int ticks;
	@SuppressWarnings("unchecked")
	@Override
	public void updateEntity() {
		if(worldObj.isRemote)
			return;
		
		if(++ticks >= 10 && storedLiquid >= capacity / 2) {
			ticks = 0;
			
			for(EntityLivingBase l : (List<EntityLivingBase>)worldObj.getEntitiesWithinAABB(EntityLivingBase.class, AxisAlignedBB.getAABBPool().getAABB(xCoord+0.5-RANGE, yCoord+0.5-RANGE, zCoord+0.5-RANGE, xCoord+0.5+RANGE, yCoord+0.5+RANGE, zCoord+0.5+RANGE))) {
				int prev = ReflectionHelper.getPrivateValue(EntityLivingBase.class, l, LiquidXPMod.FIELD_NAME_recentlyHit, "recentlyHit");
				ReflectionHelper.setPrivateValue(EntityLivingBase.class, l, 60, LiquidXPMod.FIELD_NAME_recentlyHit, "recentlyHit");
				if(prev == 0) {
					storedLiquid -= COST_MB;
				}
			}
		}
	}
}
