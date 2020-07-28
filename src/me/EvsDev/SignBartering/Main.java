package me.EvsDev.SignBartering;

import org.bukkit.plugin.PluginManager;
//import org.bukkit.Bukkit;
//import org.bukkit.Material;
import org.bukkit.plugin.java.JavaPlugin;


// TODO: stop the chest from being opened or destroyed by non shop owners
//       --give shop owner the money
//       send message on shop creation to click to announce

public class Main extends JavaPlugin {

	@Override
	public void onEnable() {
		//startup, reload
		//this.getCommand("report").setExecutor(new CommandReport());
		PluginManager pluginManager = getServer().getPluginManager();
		pluginManager.registerEvents(new SignEditListener(), this);
		pluginManager.registerEvents(new SignInteractListener(), this);
	}
	
	@Override
	public void onDisable() {
		// shutdown, reload
	}
	
}
