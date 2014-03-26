package mods.immibis.lxp;


import mods.immibis.core.BlockCombined;
import mods.immibis.core.api.util.Dir;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.texture.IIconRegister;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.IIcon;
import net.minecraft.world.IBlockAccess;
import cpw.mods.fml.relauncher.Side;
import cpw.mods.fml.relauncher.SideOnly;

public class LXPMachineBlock extends BlockCombined {

	public LXPMachineBlock() {
		super(Material.iron);
		setCreativeTab(LiquidXPMod.tab);
	}
	
	private IIcon iCatl, iColl, iGenericGrille, iCombFront, iCombSide;
	private IIcon iEnchBottom, iEnchFront, iEnchSide, iEnchTop, iGenericSide, iImprFront, iAbsoTop;
	private IIcon iDivgFront, iDivgSide;
	
	@Override
	@SideOnly(Side.CLIENT)
	public void registerBlockIcons(IIconRegister reg) {
		iCatl			= reg.registerIcon(R.block.catalyzer);
		iColl			= reg.registerIcon(R.block.collector);
		iGenericGrille	= reg.registerIcon(R.block.grille);
		iCombFront		= reg.registerIcon(R.block.combFront);
		iCombSide		= reg.registerIcon(R.block.combSide);
		iEnchBottom		= reg.registerIcon(R.block.enchBottom);
		iEnchFront		= reg.registerIcon(R.block.enchFront);
		iEnchSide		= reg.registerIcon(R.block.enchSide);
		iEnchTop		= reg.registerIcon(R.block.enchTop);
		iGenericSide	= reg.registerIcon(R.block.side);
		iImprFront		= reg.registerIcon(R.block.imprFront);
		iAbsoTop		= reg.registerIcon(R.block.absorberTop);
		iDivgFront		= reg.registerIcon(R.block.divgFront);
		iDivgSide		= reg.registerIcon(R.block.divgSide);
	}

	@Override
	public IIcon getIcon(int side, int data) {
		switch(data) {
		case LXPMachineItem.META_COLLECTOR: return iColl;
		case LXPMachineItem.META_ABSORBER: return side == Dir.PY ? iAbsoTop : iGenericSide;
		case LXPMachineItem.META_CATALYZER: return iCatl;
		case LXPMachineItem.META_IMPRINTER: return side == Dir.PY || side == Dir.NY ? iGenericGrille : iImprFront;
		case LXPMachineItem.META_ENCHANTER:
			if(side == Dir.PZ)
				return iEnchFront;
			if(side == Dir.PY)
				return iEnchTop;
			if(side == Dir.NY)
				return iEnchBottom;
			return iEnchSide;
		case LXPMachineItem.META_COMBINER:
			if(side == Dir.PY || side == Dir.NY)
				return iGenericSide;
			if(side == Dir.NZ)
				return iGenericGrille;
			if(side == Dir.PZ)
				return iCombFront;
			return iCombSide;
		case LXPMachineItem.META_ISOLATOR:
			if(side == Dir.PY || side == Dir.NY)
				return iGenericSide;
			if(side == Dir.NZ)
				return iGenericGrille;
			if(side == Dir.PZ)
				return iDivgFront;
			return iDivgSide;
		}
		return null;
	}
	
	@Override
	@SideOnly(Side.CLIENT)
	public IIcon getIcon(IBlockAccess w, int x, int y, int z, int side) {
		TileEntity te = w.getTileEntity(x, y, z);
		if(te instanceof EnchanterTile) {
			int front = ((EnchanterTile)te).front;
			if(side == front)
				return iEnchFront;
			if(side == Dir.PY)
				return iEnchTop;
			if(side == Dir.NY)
				return iEnchBottom;
			return iEnchSide;
		}
		if(te instanceof CombinerTile) {
			int front = ((CombinerTile)te).front;
			if(side == front)
				return iCombFront;
			if(side == (front ^ 1))
				return iGenericGrille;
			if(side == Dir.PY || side == Dir.NY)
				return iGenericSide;
			return iCombSide;
		}
		if(te instanceof IsolatorTile) {
			int front = ((IsolatorTile)te).front;
			if(side == front)
				return iDivgFront;
			if(side == (front ^ 1))
				return iGenericGrille;
			if(side == Dir.PY || side == Dir.NY)
				return iGenericSide;
			return iDivgSide;
		}
		
		return super.getIcon(w, x, y, z, side);
	}

	@Override
	public TileEntity getBlockEntity(int data) {
		switch(data) {
		case LXPMachineItem.META_COLLECTOR: return new CollectorTile();
		case LXPMachineItem.META_ABSORBER: return new AbsorberTile();
		case LXPMachineItem.META_CATALYZER: return new CatalyzerTile();
		case LXPMachineItem.META_IMPRINTER: return new ImprinterTile();
		case LXPMachineItem.META_ENCHANTER: return new EnchanterTile();
		case LXPMachineItem.META_COMBINER: return new CombinerTile();
		case LXPMachineItem.META_ISOLATOR: return new IsolatorTile();
		}
		return null;
	}

	@Override
	public void getCreativeItems(java.util.List<ItemStack> arraylist) {
		arraylist.add(new ItemStack(this, 1, LXPMachineItem.META_COLLECTOR));
		arraylist.add(new ItemStack(this, 1, LXPMachineItem.META_ABSORBER));
		arraylist.add(new ItemStack(this, 1, LXPMachineItem.META_CATALYZER));
		arraylist.add(new ItemStack(this, 1, LXPMachineItem.META_IMPRINTER));
		arraylist.add(new ItemStack(this, 1, LXPMachineItem.META_ENCHANTER));
		arraylist.add(new ItemStack(this, 1, LXPMachineItem.META_COMBINER));
		arraylist.add(new ItemStack(this, 1, LXPMachineItem.META_ISOLATOR));
	}

}
