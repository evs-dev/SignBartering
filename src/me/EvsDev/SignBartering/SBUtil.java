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
import org.bukkit.inventory.ItemStack;

import net.md_5.bungee.api.ChatColor;

public final class SBUtil {

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

    private final static BlockFace[] horizontalBlockFaces = {
        BlockFace.NORTH,
        BlockFace.EAST,
        BlockFace.SOUTH,
        BlockFace.WEST
    };

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

    public static String removeBracketsFromSignLine(String line) {
        return ChatColor.stripColor(line.replace("(", "").replace(")", ""));
    }

    public static boolean isContainer(BlockState blockState) {
        return (blockState instanceof InventoryHolder);
    }

    public static Sign findAttachedBarteringSign(Block startBlock) {
        for (BlockFace face : horizontalBlockFaces) {
            final Block block = startBlock.getRelative(face);

            if (isWallSign(block.getType())) {
                final Sign sign = (Sign) block.getState();
                if (FirstLine.interpretFirstLine(sign.getLine(0), true) != null) return sign;
            }
        }
        return null;
    }

    public static String formatItemStackLine(ItemStack itemStack) {
        return ChatColor.WHITE
            + SBUtil.cleanName(itemStack.getType().toString())
            + Main.FORMATTED_ITEM_QUANTITY_SEPARATOR
            + Integer.toString(itemStack.getAmount());
    }

}
