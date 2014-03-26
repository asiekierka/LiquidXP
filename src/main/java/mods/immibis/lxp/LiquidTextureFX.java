package mods.immibis.lxp;

import mods.immibis.core.api.util.TextureFX;
import net.minecraft.util.MathHelper;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

/*public class LiquidTextureFX extends TextureLiquidsFX {

	public LiquidTextureFX() {
		super(0, 255, 255, 255, 0, 51, 0, "/immibis/lxp/liquid.png");
	}
}*/

@SideOnly(Side.CLIENT)
public class LiquidTextureFX extends TextureFX {

	public LiquidTextureFX() {
		super(16, 16);
	}
	
	int ticks = 0;

	@Override
	public void onTick() {
		
		/**
		 *  float var26 = 255.0F;
	        float var27 = ((float)par1EntityXPOrb.xpColor + par9) / 2.0F;
	        red = (int)((MathHelper.sin(var27 + 0.0F) + 1.0F) * 0.5F * var26);
	        green = (int)var26;
	        blue = (int)((MathHelper.sin(var27 + 4.1887903F) + 1.0F) * 0.1F * var26);
		 */
		
		float time = ticks / 2.0f;
		ticks++;
		
		float sintime = MathHelper.sin(time / 11);

		int i1 = 0;
		for(int y = 0; y < height; y++)
		for(int x = 0; x < width; x++, i1++)
		{
			float time1 = (x + MathHelper.sin(time/8 + y*(float)(Math.PI/8)) * 3) * ((float)Math.PI / 4.0f);
			float time2 = y * ((float)Math.PI / 4.0f);
			int r = (int)((MathHelper.sin(time1) * MathHelper.sin(time2) * sintime + 1) * 127.5);
			int g = 255;
			int b = (int)((MathHelper.sin(time1 + 4.1887903F) * MathHelper.sin(time2 + 4.1887903F) * sintime + 1) * 25.5);

			imageData[i1 * 4 + 0] = (byte) r;
			imageData[i1 * 4 + 1] = (byte) g;
			imageData[i1 * 4 + 2] = (byte) b;
			imageData[i1 * 4 + 3] = (byte) 255;
		}
		
		
		
		

	}
}

