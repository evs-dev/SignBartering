package me.EvsDev.SignBartering;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.inventory.InventoryHolder;

public class ContainerUtil {

	@Deprecated
	public static final boolean isMaterialContainer(Material material) {
		return false;
	}
	
	public static final boolean isBlockStateContainer(BlockState blockState) {
		return blockState instanceof InventoryHolder;
	}
	
	public static final Block findSurroundingSellingSignBlock(Block startBlock) {
		Location blockLocation = startBlock.getLocation();
		Location[] surroundingLocations = {
			blockLocation.add(1, 0, 0),
			blockLocation.add(1, 0, 1),
			blockLocation.subtract(1, 0, 0),
			blockLocation.subtract(1, 0, 1)
		};
		for (Location location : surroundingLocations) {
			Block block = location.getBlock();
			
			if (SB.isWallSign(block.getType())) { 
				Sign sign = (Sign) block.getState();
				if (LineChecker.perfectFirstLine(sign.getLine(0))) {
					return block;
				}
			}
		}
		return null;
	}
	
	public static Block getInFrontBlock(Block block) {
		BlockData data = block.getBlockData();
        Directional directional = (Directional)data;
        return block.getRelative(directional.getFacing());
	}
	
}
