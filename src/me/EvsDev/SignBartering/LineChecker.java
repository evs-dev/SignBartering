package me.EvsDev.SignBartering;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

import net.md_5.bungee.api.ChatColor;

public class LineChecker {

    public static ItemStack parseItemAndQuantityLine(String line, String separator) {
        String[] split = ChatColor.stripColor(line).split(separator);
        if (split.length != 2) return null;

        Material item = Material.matchMaterial(ChatColor.stripColor(split[0]));
        if (item == null) return null;

        int quantity = SBUtil.tryParseInt(ChatColor.stripColor(split[1]));
        if (quantity <= 0) return null;

        return new ItemStack(item, quantity);
    }

    public static boolean perfectFirstLine(String line) {
        return (line != null) && (ChatColor.stripColor(line).equals(SBUtil.requiredFirstLine));
    }
}
