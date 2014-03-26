package mods.immibis.lxp;

import mods.immibis.cobaltite.AssignedBlock;
import mods.immibis.cobaltite.AssignedItem;
import mods.immibis.cobaltite.CobaltiteMod;
import mods.immibis.cobaltite.Configurable;
import mods.immibis.cobaltite.ModBase;
import mods.immibis.cobaltite.TileGUI;
import mods.immibis.cobaltite.CobaltiteMod.RegisteredTile;
import mods.immibis.core.api.FMLModInfo;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

@Mod(modid="LiquidXP", name = "Liquid XP", version = "58.0.0", dependencies="required-after:ImmibisCore")
@CobaltiteMod(
	tiles = {
		@RegisteredTile(id="immibis.lxp.collector", tile=CollectorTile.class),
		@RegisteredTile(id="immibis.lxp.absorber", tile=AbsorberTile.class),
		@RegisteredTile(id="immibis.lxp.imprinter", tile=ImprinterTile.class),
		@RegisteredTile(id="immibis.lxp.catalyzer", tile=CatalyzerTile.class),
		@RegisteredTile(id="immibis.lxp.enchanter", tile=EnchanterTile.class),
		@RegisteredTile(id="immibis.lxp.combiner", tile=CombinerTile.class),
		@RegisteredTile(id="immibis.lxp.diverger", tile=IsolatorTile.class)
	}
)
@FMLModInfo(
	description="Liquid XP and related machines.",
	url="http://www.minecraftforum.net/topic/1001131-110-immibiss-mods-smp/",
	authors="immibis"
)
public class LiquidXPMod extends ModBase {
	public static final String FIELD_NAME_recentlyHit = "field_70718_bc";
	
	// set other fluid properties here
	private static Fluid defaultFluid = new Fluid("immibis.liquidxp");
	
	@AssignedItem(id="bucket")
	public static BucketItem bucket;
	
	@AssignedItem(id="medallion")
	public static MedallionItem medallion;
	
	@AssignedBlock(id="machine", item=LXPMachineItem.class)
	public static LXPMachineBlock machines;
	
	@TileGUI(container=ImprinterContainer.class, gui=ImprinterGUI.class)
	public static final int GUI_IMPRINTER = 0;
	
	@TileGUI(container=EnchanterContainer.class, gui=EnchanterGUI.class)
	public static final int GUI_ENCHANTER = 1;
	
	@TileGUI(container=CombinerContainer.class, gui=CombinerGUI.class)
	public static final int GUI_COMBINER = 2;
	
	@TileGUI(container=IsolatorContainer.class, gui=IsolatorGUI.class)
	public static final int GUI_ISOLATOR = 3;
	
	@Instance("LiquidXP")
	public static LiquidXPMod instance;

	@Configurable("onlyEnchantBooks")
	public static boolean ONLY_ENCHANT_BOOKS = true;
	
	@Configurable("destroyXPOverflow")
	public static boolean DESTROY_XP_OVERFLOW = false;
	
	@Configurable("millibucketsPerXPPoint")
	public static int mbPerXp = 25;
	
	@Configurable("xpPerMobEssenceBucket")
	public static int xpPerMobEssenceBucket = 15;
	
	@Configurable("xpPerXPJuiceBucket")
	public static int xpPerXPJuiceBucket = 1000 / LXP_OpenBlocks_Compat.XP_JUICE_MB_PER_XP;
	
	@Configurable("xpPerEnchantingBottle")
	public static int xpPerEnchantingBottle = 13;
	
	@Configurable("xpPerIsolatorOperation")
	public static int xpPerIsolatorOperation = 15;
	
	@Configurable("xpPerCatalyzedMob")
	public static int xpPerCatalyzedMob = 1;
	
	@Configurable("costScaleEnchanting")
	public static int enchantingCostScale = 100;
	
	@Configurable("costScaleCombining")
	public static int combiningCostScale = 100;
	
	@Configurable("reactantDynamoRFPerXP")
	public static int reactantDynamoRFPerXP = 25000;
	
	public static Fluid fluid;
	
	public static CreativeTabs tab = new CreativeTabs("liquidxp") {
		@Override
		public Item getTabIconItem() {
			return bucket;
		}
	};
	
	public static double convertXPToMB(double xp) {
		return xp * mbPerXp;
	}
	
	public static double convertMBToXP(double mb) {
		return mb / mbPerXp;
	}
	
	@Override
	protected void initBlocksAndItems() throws Exception {
		fluid = defaultFluid;
		
		if(!FluidRegistry.registerFluid(fluid)) {
			fluid = FluidRegistry.getFluid(fluid.getName());
			if(fluid == null)
				throw new AssertionError("Something went wrong, failed to register or get fluid "+fluid.getName());
		}
		
		LXP_TE3_Compat.init();
		
		FluidContainerRegistry.registerFluidContainer(new FluidStack(fluid, 1000), new ItemStack(bucket), new ItemStack(Items.bucket));
		FluidContainerRegistry.registerFluidContainer(new FluidStack(fluid, (int)convertXPToMB(xpPerEnchantingBottle)), new ItemStack(Items.experience_bottle), new ItemStack(Items.glass_bottle));
		
		LXP_MFR_Compat.init();
		LXP_OpenBlocks_Compat.init();
		
		ImprinterTile.initLevelTable();
	}
	
	@Override
	protected void addRecipes() throws Exception {
		GameRegistry.addRecipe(new ItemStack(machines, 1, LXPMachineItem.META_ABSORBER),
			"S_S",
			"SUS",
			"SSS",
			'S', Blocks.stone,
			'_', Blocks.stone_pressure_plate,
			'U', Items.bucket
			);
		GameRegistry.addRecipe(new ItemStack(machines, 1, LXPMachineItem.META_CATALYZER),
			"SBS",
			"/D/",
			"D/D",
			'S', Blocks.stone,
			'D', Items.diamond,
			'/', Items.blaze_rod,
			'B', bucket
			);
		GameRegistry.addRecipe(new ItemStack(machines, 1, LXPMachineItem.META_COLLECTOR),
			"SDS",
			"SBS",
			"SSS",
			'S', Blocks.stone,
			'B', bucket,
			'D', Items.diamond
			);
		GameRegistry.addRecipe(new ItemStack(machines, 1, LXPMachineItem.META_COMBINER),
			"BSB",
			"AFA",
			"SBS",
			'S', Blocks.stone,
			'B', bucket,
			'F', Blocks.furnace,
			'A', Blocks.anvil
			);
		GameRegistry.addRecipe(new ItemStack(machines, 1, LXPMachineItem.META_ENCHANTER),
			"BDB",
			"OEO",
			"OOO",
			'B', bucket,
			'E', Blocks.enchanting_table,
			'D', Items.diamond,
			'O', Blocks.obsidian
			);
		GameRegistry.addRecipe(new ItemStack(machines, 1, LXPMachineItem.META_IMPRINTER),
			"SMS",
			"BPB",
			"SSS",
			'S', Blocks.stone,
			'B', Blocks.bookshelf,
			'P', Blocks.piston,
			'M', new ItemStack(medallion, 1, 0)
			);
		GameRegistry.addRecipe(new ItemStack(machines, 1, LXPMachineItem.META_ISOLATOR),
			"SDS",
			"BAB",
			"SSS",
			'A', Blocks.anvil,
			'B', bucket,
			'D', Items.diamond,
			'S', Blocks.stone
			);
		GameRegistry.addRecipe(new ItemStack(medallion, 1, 0),
			" I ",
			"IBI",
			" I ",
			'I', Items.iron_ingot,
			'B', bucket
			);
	}
	
	public static int xpBarCap(int level) {
		return level >= 30 ? 62 + (level - 30) * 7 : (level >= 15 ? 17 + (level - 15) * 3 : 17);
	}
	
	public static int levelToXP(int level) {
		int n = 0;
		for(int k = 0; k < level; k++) // this is not off by one
			n += xpBarCap(k);
		return n;
	}
	
	public static int xpToLevel(int experienceTotal) {
		for(int level = 0; ; level++) {
			experienceTotal -= xpBarCap(level);
			if(experienceTotal < 0)
				return level; // this is not off by one
		}
	}
	
	@EventHandler public void init(FMLInitializationEvent evt) {super._init(evt);}
	@EventHandler public void preinit(FMLPreInitializationEvent evt) {
		super._preinit(evt);
		
		//if(!SidedProxy.instance.isDedicatedServer())
		//	ObviousClassUsedForNonMaliciousTrollingOfGreg.preinit();
	}
	
	@EventHandler
	public void postinit(FMLPostInitializationEvent evt) {
		//if(!SidedProxy.instance.isDedicatedServer())
		//	ObviousClassUsedForNonMaliciousTrollingOfGreg.postinit();
	}
	
	@SideOnly(Side.CLIENT)
	@SubscribeEvent
	public void onTextureStitchPre(TextureStitchEvent.Pre evt) {
		if(evt.map.getTextureType() == 0 && fluid == defaultFluid) {
			fluid.setIcons(evt.map.registerIcon("liquidxp:liquid"));
		}
	}
}
