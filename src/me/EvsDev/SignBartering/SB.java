package me.EvsDev.SignBartering;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.Directional;
import org.bukkit.entity.Player;
import org.bukkit.event.block.SignChangeEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import net.md_5.bungee.api.ChatColor;

public class SB {

    public static final String messagePrefix = ChatColor.GOLD + "[SignBartering] " + ChatColor.RESET;
    public static final String requiredFirstLine = "[SELLING]";
    public static final String alternativeRequiredFirstLine = "[SELL]";
    public static final String formattedItemQuantitySeparator = "x";
    public static final String itemQuantitySeparator = ":";

    public static final String error_NoMoney = "You have insufficient funds";

    public static int tryParseInt(String string) {
        try {
            return Integer.parseInt(string);
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    public static void error(Player player, String message) {
        player.sendMessage(SB.messagePrefix + ChatColor.RED + message);
    }

    public static void error(SignChangeEvent e, String message) {
        error(e.getPlayer(), message);
    }

    public static void error(PlayerInteractEvent e, String message) {
        error(e.getPlayer(), message);
    }

    public static String cleanName(String name) {
        return WordUtils.capitalize(name.replace('_', ' ').toLowerCase());
    }

    private static final Material[] signMaterialsArr = {
            Material.ACACIA_SIGN,
            Material.ACACIA_WALL_SIGN,
            Material.BIRCH_SIGN,
            Material.BIRCH_WALL_SIGN,
            Material.CRIMSON_SIGN,
            Material.CRIMSON_WALL_SIGN,
            Material.DARK_OAK_SIGN,
            Material.DARK_OAK_WALL_SIGN,
            Material.JUNGLE_SIGN,
            Material.JUNGLE_WALL_SIGN,
            Material.OAK_SIGN,
            Material.OAK_WALL_SIGN,
            Material.SPRUCE_SIGN,
            Material.SPRUCE_WALL_SIGN,
            Material.WARPED_SIGN,
            Material.WARPED_WALL_SIGN
    };

    private static final Material[] wallSignMaterialsArr = {
            Material.ACACIA_WALL_SIGN,
            Material.BIRCH_WALL_SIGN,
            Material.CRIMSON_WALL_SIGN,
            Material.DARK_OAK_WALL_SIGN,
            Material.JUNGLE_WALL_SIGN,
            Material.OAK_WALL_SIGN,
            Material.SPRUCE_WALL_SIGN,
            Material.WARPED_WALL_SIGN
    };

    private static List<Material> wallSignMaterials = Arrays.asList(wallSignMaterialsArr);

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

    public static boolean playerIsSignOwner(Sign sign, Player player) {
        String signName = removeBracketsFromNameLine(sign.getLine(3));
        return signName.equals(player.getDisplayName());
    }

    public static String removeBracketsFromNameLine(String line) {
        return ChatColor.stripColor(line.replace("(", "").replace(")", ""));
    }

    public static Player getSignOwner(String ownerLine) {
        String ownerName = SB.removeBracketsFromNameLine(ownerLine);
        return Bukkit.getPlayer(ownerName);
    }

}
