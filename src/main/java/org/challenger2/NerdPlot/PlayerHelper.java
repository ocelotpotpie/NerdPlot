package org.challenger2.NerdPlot;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.bukkit.selections.Selection;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

/**
 * 
 * Helper for some of the commands that require a player. Deals with WE, WG, Regions, and plots.
 * The object will talk to the player as required as PlayerHelper is initialized.
 * PlayerHelper grabs a whole bunch of info from the player when it is constructed
 * 
 * Check isInitialized to see if everything was setup correctly.
 *
 */
public class PlayerHelper {
	
	private NerdPlotPlugin plugin;
	private WorldGuardPlugin wg;
	private WorldEditPlugin we;
	private RegionManager rm;
	private Player player;
	private String playerName;
	private UUID   playerID;
	private World  world;
	private String worldName;
	private ProtectedRegion plot;
	private String areaName;
	private boolean initialized = false;
	
	/**
	 * Get all the useful things we may need for a player command
	 */
	public PlayerHelper(NerdPlotPlugin plugin, CommandSender sender) {
		// Setup plugin
		this.plugin = plugin;
		wg = plugin.getWG();
		we = plugin.getWE();
		
		// Are we a player?
    	if (!(sender instanceof Player)) {
    		sender.sendMessage("Who are you? Your not a player.");
    		return;
    	}
    	player = (Player)sender;
    	playerName = player.getName();
    	playerID   = player.getUniqueId();
    	world = player.getWorld();
    	worldName = world.getName();
    	
    	// Is WG setup in this world?
    	rm = wg.getRegionManager(world);
    	if (rm == null) {
    		sender.sendMessage(ChatColor.RED + "WorldGuard is not enabled in this world");
    		return;
    	}

    	plot = null;
    	areaName = null;
    	initialized = true;
	}
	
	
	public Player getPlayer() {
		return player;
	}
	
	
	public String getPlayerName() {
		return playerName;
	}
	
	
	public UUID getPlayerID() {
		return playerID;
	}

	
	public World getWorld() {
		return world;
	}
	
	
	public String getWorldName() {
		return worldName;
	}
	
	
	public RegionManager getManager() {
		return rm;
	}
	
	
	/**
	 * Get the plot the player is currently standing in.
	 */
	public ProtectedRegion getPlot() {
		if (plot !=  null) {
			return plot;
		}
		ApplicableRegionSet regions = rm.getApplicableRegions(player.getLocation());
		for (ProtectedRegion check : regions) {
			if (plugin.isPlot(worldName, check.getId())) {
				plot = check;
				break;
			}
		}
		return plot;
	}
	
	
	/**
	 * Get the plot area name
	 */
	public String getAreaName() {
		if (areaName == null) {
			ProtectedRegion p = getPlot();
			PlotInfo info = plugin.getPlotInfo(worldName, p.getId());
			if (info != null) {
				areaName = info.areaName;
			}
		}
		return areaName;
	}
	
	
	/**
	 * Create a new region from the players current WE selection
	 */
	public ProtectedRegion regionFromSelection(String regionName) {
		Selection selection = we.getSelection(player);
		if (selection == null) {
			player.sendMessage(ChatColor.RED + "You must make a World Edit selection first");
			return null;
		}
		if (!(selection instanceof CuboidSelection)) {
			player.sendMessage(ChatColor.RED + "Only Cuboid selections are supported");;
			return null;
		}
		
		// Convert the WE selection into a WG BlockVector
		Location loc;
		loc = selection.getMinimumPoint();
		BlockVector min = new BlockVector(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
		loc = selection.getMaximumPoint();
		BlockVector max = new BlockVector(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
		return new ProtectedCuboidRegion(regionName, min, max);
	}
	
	
	/**
	 * Send a message to the player
	 */
	public void sendMessage(String msg) {
		player.sendMessage(msg);
	}


	/**
	 * See if the player helper was setup correctly
	 */
	public boolean isInitialized() {
		return initialized;
	}

}
