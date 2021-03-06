package me.lennartVH01.itemfinder;

import java.util.ArrayList;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import com.palmergames.bukkit.towny.Towny;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.TownyUniverse;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.StateFlag.State;

public class PermissionChecker {
	
	private final ArrayList<PluginChecker> checkerList = new ArrayList<PluginChecker>();
	
	public PermissionChecker(ItemFinder plugin){
		
		Plugin townyPlugin = plugin.getServer().getPluginManager().getPlugin("Towny");
		if(townyPlugin instanceof Towny){
			checkerList.add(new PluginChecker() {
				
				@Override public boolean canAccess(Player p, Location loc) {
					String townName = TownyUniverse.getTownName(loc);
					if(townName == null)
						//If no town found then check if they can build in the wild
						return p.hasPermission(com.palmergames.bukkit.towny.permissions.PermissionNodes.TOWNY_WILD_BLOCK_SWITCH.getNode());
					else{
						try {
							return TownyUniverse.getDataSource().getTown(townName).hasResident(p.getName());
						} catch (NotRegisteredException e) {
							return true;
						}
					}
				}
			});
		}
		
		Plugin wgPlugin = plugin.getServer().getPluginManager().getPlugin("WorldGuard");
		if(wgPlugin instanceof WorldGuardPlugin){
			final WorldGuardPlugin worldGuard = (WorldGuardPlugin) wgPlugin;
			
			checkerList.add(new PluginChecker() {
				@Override public boolean canAccess(Player p, Location loc) {
					ApplicableRegionSet regions = worldGuard.getRegionManager(loc.getWorld()).getApplicableRegions(loc);
					State state = regions.queryState(worldGuard.wrapPlayer(p), DefaultFlag.CHEST_ACCESS);
					return state != State.DENY;
				}
			});
		}
	}
	
	public boolean canAccess(Player p, Location loc){
		for(PluginChecker checker:checkerList)
			if(!checker.canAccess(p, loc))
				return false;
		
		return true;
	}
	
	private static interface PluginChecker{
		public boolean canAccess(Player p, Location loc);
	}
}
