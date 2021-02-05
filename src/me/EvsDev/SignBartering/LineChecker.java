package me.EvsDev.SignBartering;

import org.bukkit.Material;

import net.md_5.bungee.api.ChatColor;

public class LineChecker {

    public static ItemAndQuantity parseItemAndQuantityLine(String line, String separator) {
        String[] split = ChatColor.stripColor(line).split(separator);
        if (split.length != 2) return null;

        Material item = Material.matchMaterial(ChatColor.stripColor(split[0]));
        if (item == null) return null;

        int quantity = SB.tryParseInt(ChatColor.stripColor(split[1]));
        if (quantity <= 0) return null;

        return new ItemAndQuantity(item, quantity);
    }

    public static ItemAndQuantity parseItemAndQuantityLine(String line) {
        return parseItemAndQuantityLine(line, SB.itemQuantitySeparator);
    }

    public static ItemAndQuantity parseItemAndQuantityLine(String line, boolean useFormattedSeparator) {
        if (useFormattedSeparator) {
            return parseItemAndQuantityLine(line, SB.formattedItemQuantitySeparator);
        } else {
            return parseItemAndQuantityLine(line);
        }
    }

    public static boolean perfectFirstLine(String line) {
        return (line != null) && (ChatColor.stripColor(line).equals(SB.requiredFirstLine));
    }

}
