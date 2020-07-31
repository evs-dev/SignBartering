package me.EvsDev.SignBartering;

import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.inventory.InventoryHolder;

public class ContainerUtil {
	
	public static final boolean isBlockStateContainer(BlockState blockState) {
		return blockState instanceof InventoryHolder;
	}
	
	public static final Block findSurroundingSellingSignBlock(Block startBlock) {
		Block[] surroundingBlocks = {
			startBlock.getRelative(BlockFace.NORTH),
			startBlock.getRelative(BlockFace.EAST),
			startBlock.getRelative(BlockFace.SOUTH),
			startBlock.getRelative(BlockFace.WEST),
		};
		for (Block block: surroundingBlocks) {
			if (!SB.isWallSign(block.getType())) continue;
			
			Sign sign = (Sign) block.getState();
			if (LineChecker.perfectFirstLine(sign.getLine(0))) {
				return block;
			}
		}
		return null;
	}	
}
