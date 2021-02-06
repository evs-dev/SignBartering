package me.EvsDev.SignBartering;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.inventory.InventoryHolder;

import net.md_5.bungee.api.ChatColor;

public class SBUtil {

    private final static List<Material> wallSignMaterials = Arrays.asList(
        Material.ACACIA_WALL_SIGN,
        Material.BIRCH_WALL_SIGN,
        Material.CRIMSON_WALL_SIGN,
        Material.DARK_OAK_WALL_SIGN,
        Material.JUNGLE_WALL_SIGN,
        Material.OAK_WALL_SIGN,
        Material.SPRUCE_WALL_SIGN,
        Material.WARPED_WALL_SIGN
    );

    public static int tryParseInt(String string) {
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public static String cleanName(String name) {
        return WordUtils.capitalize(name.replace('_', ' ').toLowerCase());
    }

    public static boolean isWallSign(Material material) {
        return wallSignMaterials.contains(material);
    }

    public static Block getBehindBlock(Block block) {
        BlockData data = block.getBlockData();
        Directional directional = (Directional)data;
        return block.getRelative(directional.getFacing().getOppositeFace());
    }

    public static Block getInFrontBlock(Block block) {
        BlockData data = block.getBlockData();
        Directional directional = (Directional)data;
        return block.getRelative(directional.getFacing());
    }

    public static String removeBracketsFromNameLine(String line) {
        return ChatColor.stripColor(line.replace("(", "").replace(")", ""));
    }

    public static boolean isBlockStateContainer(BlockState blockState) {
        return (blockState instanceof InventoryHolder);
    }

    public static Block findSurroundingSellingSignBlock(Block startBlock) {
        Block[] surroundingBlocks = {
            startBlock.getRelative(BlockFace.NORTH),
            startBlock.getRelative(BlockFace.EAST),
            startBlock.getRelative(BlockFace.SOUTH),
            startBlock.getRelative(BlockFace.WEST),
        };
        for (Block block : surroundingBlocks) {
            if (!isWallSign(block.getType())) continue;

            Sign sign = (Sign) block.getState();
            if (LineChecker.perfectFirstLine(sign.getLine(0))) {
                return block;
            }
        }
        return null;
    }

}
