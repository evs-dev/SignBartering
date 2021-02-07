package me.EvsDev.SignBartering.LineFormatters;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import me.EvsDev.SignBartering.SBUtil;
import net.md_5.bungee.api.ChatColor;

public class BasicNameLineFormatter extends NameLineFormatter {

    @Override
    public String formatPlayerFormattedLine(String line, Player signMaker) {
        return ChatColor.GRAY + "(" + signMaker.getDisplayName() + ")";
    }

    @Override
    public Player interpretPluginFormattedLine(String line) {
        String ownerName = SBUtil.removeBracketsFromSignLine(line);
        return Bukkit.getPlayer(ownerName);
    }

}
