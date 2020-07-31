package me.EvsDev.SignBartering;

import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {

	@Override
	public void onEnable() {
		PluginManager pluginManager = getServer().getPluginManager();
		pluginManager.registerEvents(new SignEditListener(), this);
		pluginManager.registerEvents(new InteractListener(), this);
	}	
}
