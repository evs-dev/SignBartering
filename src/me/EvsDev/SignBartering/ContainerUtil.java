package me.EvsDev.SignBartering;

import org.bukkit.Material;
import org.bukkit.block.BlockState;
import org.bukkit.inventory.InventoryHolder;

public class ContainerUtil {

	public static final boolean isMaterialContainer(Material material) {
		return false;
	}
	
	public static final boolean isBlockStateContainer(BlockState blockState) {
		return blockState instanceof InventoryHolder;
	}
	
}
