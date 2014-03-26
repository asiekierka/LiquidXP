package mods.immibis.lxp;

/*public class MedallionTextureFX extends FMLTextureFX {
	private final String texture = "/immibis/lxp/world.png";
	
	private float distanceMap[][];

	public MedallionTextureFX() {
		super(3);

		setup();
	}
	
	@Override
	protected void setup() {
		super.setup();
		
		distanceMap = new float[tileSizeBase][tileSizeBase];
		float half = (tileSizeBase - 1) / 2.0f;
		for(int y = 0; y < tileSizeBase; y++)
		for(int x = 0; x < tileSizeBase; x++) {
			float dist = (float)Math.sqrt((y-half)*(y-half) + (x-half)*(x-half));
			if(dist >= half - 0.5)
				distanceMap[y][x] = 0;
			else
				distanceMap[y][x] = dist;
		}
	}

	@Override
	public void bindImage(RenderEngine renderengine) {
		ForgeHooksClient.bindTexture(texture, 0);
	}
	
	int ticks = 0;

	@Override
	public void onTick() {
		
		float time = ticks / 2.0f;
		ticks++;
		
		float sintime = MathHelper.sin(time / 11);
		float sintime2 = MathHelper.sin(time / 5);

		int i1 = 0, r, g, b, a;
		for(int y = 0; y < tileSizeBase; y++)
		for(int x = 0; x < tileSizeBase; x++, i1++)
		{
			float dist = distanceMap[y][x];
			if(dist == 0) {
				r = 0;
				g = 0;
				b = 0;
				a = 0;
			} else {
				float time1 = dist * ((float)Math.PI * 2 / tileSizeBase) + sintime2;
				r = (int)((MathHelper.sin(time1) * sintime + 1) * 127.5);
				g = 255;
				b = (int)((MathHelper.sin(time1 + 4.1887903F) * sintime + 1) * 25.5);
				a = 255;
			}

			imageData[i1 * 4 + 0] = (byte) r;
			imageData[i1 * 4 + 1] = (byte) g;
			imageData[i1 * 4 + 2] = (byte) b;
			imageData[i1 * 4 + 3] = (byte) a;
		}

	}
}
*/