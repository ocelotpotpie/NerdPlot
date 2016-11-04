package org.challenger2.NerdPlot;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class CmdTP implements NerdPlotCommand {

	private final NerdPlotPlugin plugin;
	private static final String name = "tp";
	private static final String permission = "nerdplot.tp";

	
	public CmdTP(NerdPlotPlugin plugin) {
		this.plugin = plugin;
	}
	
	
	@Override
	public void execute(CommandSender sender, String[] args) {
		
		// Check arguments and permissions
		if (!sender.hasPermission(permission)) {
			plugin.printUsage(sender);
			return;
		}
		
		// Check args
		if (args.length != 1 && args.length != 2) {
			printUsage(sender);
			return;
		}
		String regionName = args[0];
		
		// Are we a player?
    	if (!(sender instanceof Player)) {
    		sender.sendMessage("Who are you? Your not a player.");
    		return;
    	}
    	Player player = (Player)sender;
    	World world;
    	
    	// Get the world the region is in
    	if (args.length == 2) {
    		String worldName = args[1];
    		world = plugin.getServer().getWorld(worldName);
    		if (world == null) {
    			player.sendMessage(ChatColor.RED + "Unknown World: " + worldName);
    			return;
    		}
    	} else {
    		world = player.getWorld();
    	}
    	
		RegionManager rm = plugin.getWG().getRegionManager(world);
		if (rm == null) {
			player.sendMessage(ChatColor.RED + "World Guard is not enabled in World: " + world.getName());
			return;
		}
		
		// Get the region
		ProtectedRegion rg = rm.getRegion(regionName);
		if (rg == null) {
			player.sendMessage(ChatColor.RED + "Unknown plot: " + regionName);
			return;
		}
		
		// Just assume all regions are "plots". We will for go the plot check
		// Find the middle of the region and teleport there
		BlockVector min = rg.getMinimumPoint();
		BlockVector max = rg.getMaximumPoint();
		
		int x = (min.getBlockX() + max.getBlockX()) / 2;
		int z = (min.getBlockZ() + max.getBlockZ()) / 2;
		int y = world.getHighestBlockYAt(x, z);
		Location dest = new Location(world, (double)x + 0.5, (double)y, (double)z + 0.5);
		player.teleport(dest);
	}

	
	@Override
	public String getName() {
		return name;
	}
	

	@Override
	public void printUsage(CommandSender sender) {
		if(sender.hasPermission(permission)) {
			sender.sendMessage(ChatColor.GREEN + "/" + plugin.getName() + " " + name + " <region> [<world>]");
		}
	}

}
