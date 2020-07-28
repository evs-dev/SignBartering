package me.EvsDev.SignBartering;

import java.util.Arrays;
import java.util.List;

import org.apache.commons.lang.WordUtils;
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
	
	public static String messagePrefix = ChatColor.GOLD + "[SignBartering] " + ChatColor.RESET;
	public static String requiredFirstLine = "[SELLING]";
	public static String alternativeRequiredFirstLine = "[SELL]";
	public static String formattedItemQuantitySeparator = "x";
	public static String itemQuantitySeparator = ":";
	
	public static String error_NoMoney = "You have insufficient funds";
	
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
	
	private static List<Material> signMaterials = Arrays.asList(signMaterialsArr);
	private static List<Material> wallSignMaterials = Arrays.asList(wallSignMaterialsArr);
	
	public static boolean isSign(Material material) {
		return signMaterials.contains(material);
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
	
	public static boolean isChest(Block block) {
		return block.getType() == Material.CHEST;
	}
	
	public static boolean playerIsSignOwner(Sign sign, Player player) {
		String signName = ChatColor.stripColor(sign.getLine(3).replace("(", "").replace(")", ""));
		return signName.equals(player.getDisplayName());
	}

}
