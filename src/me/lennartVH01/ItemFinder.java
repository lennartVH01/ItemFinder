package me.lennartVH01;

import org.bukkit.plugin.java.JavaPlugin;


public class ItemFinder extends JavaPlugin{
	private BlockMarker blockMarker;
	private PermissionChecker checker;
	
	@Override public void onEnable(){
		saveDefaultConfig();
		
		blockMarker = new BlockMarker(this);
		checker = new PermissionChecker(this);
		
		FindCommand findcmd = new FindCommand(this, blockMarker, checker);
		
		getCommand("find").setExecutor(findcmd);
		getCommand("find").setTabCompleter(findcmd);
	}
	
	@Override public void onDisable(){
		blockMarker.onDisable();
	}
}
