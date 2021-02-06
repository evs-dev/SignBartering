package me.EvsDev.SignBartering;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;

import net.md_5.bungee.api.ChatColor;

public class SB {

    public static final String requiredFirstLine = "[SELLING]";
    public static final String alternativeRequiredFirstLine = "[SELL]";
    public static final String formattedItemQuantitySeparator = "x";
    public static final String itemQuantitySeparator = ":";

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

}
