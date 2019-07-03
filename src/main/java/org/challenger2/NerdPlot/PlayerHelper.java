package org.challenger2.NerdPlot;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.Region;
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
    	rm = Util.getWorldGuardRegionManager(this.world);
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
		Location location = this.player.getLocation();
		BlockVector3 wrappedVector = BlockVector3.at(location.getBlockX(), location.getBlockY(), location.getBlockZ());
		ApplicableRegionSet regions = this.rm.getApplicableRegions(wrappedVector);

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
	    Region selection;
	    LocalSession localSession = this.we.getSession(this.player);
	    com.sk89q.worldedit.world.World wrappedWorld = BukkitAdapter.adapt(this.player.getWorld());
	    try {
	        selection = localSession.getSelection(wrappedWorld);
	        if (selection == null) {
	            throw new IncompleteRegionException();
	        }
	    } catch (IncompleteRegionException e) {
	        this.player.sendMessage(ChatColor.RED + "You must select a region first!");
	        return null;
		}
		
	    return new ProtectedCuboidRegion(regionName, selection.getMinimumPoint(), selection.getMaximumPoint());
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
