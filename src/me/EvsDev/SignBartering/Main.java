package me.EvsDev.SignBartering;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import net.md_5.bungee.api.ChatColor;

public class Main extends JavaPlugin {

    public static final String MESSAGE_PREFIX = ChatColor.GOLD + "[SignBartering] " + ChatColor.RESET;
    public static final String ITEM_QUANTITY_SEPARATOR = ":";
    public static final String FORMATTED_ITEM_QUANTITY_SEPARATOR = "x";

    @Override
    public void onEnable() {
        PluginManager pluginManager = getServer().getPluginManager();
        pluginManager.registerEvents(new SignEditListener(), this);
        pluginManager.registerEvents(new InteractListener(), this);
    }
}
