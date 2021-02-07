package me.EvsDev.SignBartering.LineFormatters;

import org.bukkit.entity.Player;

import net.md_5.bungee.api.ChatColor;

public class AllowEmptyNameLineFormatter extends BasicNameLineFormatter {

    @Override
    public String formatPlayerFormattedLine(String line, Player signMaker) {
        if (signMaker == null
            || line.equalsIgnoreCase("null")
            || line.equalsIgnoreCase("none")
            || line.equalsIgnoreCase("empty")) {
            return "";
        } else if (!line.isEmpty()) {
            return ChatColor.GRAY + line;
        }
        return super.formatPlayerFormattedLine(line, signMaker);
    }

    @Override
    public Player interpretPluginFormattedLine(String line) {
        if (line.isEmpty()) return null;
        return super.interpretPluginFormattedLine(line);
    }

}
