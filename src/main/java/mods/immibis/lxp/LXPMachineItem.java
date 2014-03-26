package mods.immibis.lxp;

import net.minecraft.block.Block;
import mods.immibis.core.ItemCombined;

public class LXPMachineItem extends ItemCombined {
	
	public static final int META_COLLECTOR = 0;
	public static final int META_ABSORBER = 1;
	public static final int META_CATALYZER = 2;
	public static final int META_IMPRINTER = 3;
	public static final int META_ENCHANTER = 4;
	public static final int META_COMBINER = 5;
	public static final int META_RENAMER = 6;
	public static final int META_ISOLATOR = 7;

	public LXPMachineItem(Block block) {
		super(block, "liquidxp", new String[] {
			"collector",
			"absorber",
			"catalyzer",
			"imprinter",
			"enchanter",
			"combiner",
			"renamer",
			"isolator",
		});
	}

}
