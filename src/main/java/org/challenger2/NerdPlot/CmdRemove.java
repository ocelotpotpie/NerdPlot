package org.challenger2.NerdPlot;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class CmdRemove extends NerdPlotCommand {
	
	private final String permission = "nerdplot.remove";
	
	public CmdRemove(NerdPlotPlugin plugin) {
		super(plugin, "remove");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {

		if (!sender.hasPermission(permission)) {
			plugin.printUsage(sender);
			return;
		}
		
		// Checkout all the prerequisites
		// Are we a player?
    	if (!(sender instanceof Player)) {
    		sender.sendMessage("Who are you? Your not a player.");
    		return;
    	}

    	Player player = (Player)sender;
    	String worldName  = player.getWorld().getName();
    	
    	// Is World Guard Enabled?
		//RegionContainer container = plugin.getWG().getRegionContainer();
		//RegionManager regions = container.get(player.getWorld());
    	RegionManager manager = plugin.getWG().getRegionManager(player.getWorld());
		if (manager == null) {
			sender.sendMessage(ChatColor.RED + "WorldGuard is not enabled in this world");
			return;
		}
    	
    	if (args.length != 0) {
    		printUsage(sender);
    		return;
    	}
    
		// Find the plot we are standing in
		ApplicableRegionSet regions = manager.getApplicableRegions(player.getLocation());
		ProtectedRegion plot = null;
		for (ProtectedRegion check : regions) {
			if (plugin.isPlot(worldName, check.getId())) {
				// We found what we are looking for
				plot = check;
				break;
			}
		}
		if (plot == null) {
			sender.sendMessage(ChatColor.RED + "You must stand in the plot you want to remove the owner from");
			return;
		}
		String plotName = plot.getId();
    	
    	if (plugin.isPlot(worldName, plotName)) {
    		plugin.removePlot(worldName, plotName);
    		manager.removeRegion(plotName);
    		sender.sendMessage(ChatColor.GREEN + "Plot " + plotName + " removed");
    	} else {
    		sender.sendMessage(ChatColor.RED + "Plot " + plotName + " does not exist");
    	}

	}
	
	@Override
	public void printUsage(CommandSender sender) {
		if(sender.hasPermission(permission)) {
			sender.sendMessage(ChatColor.GREEN + "/" + plugin.getName() + " " + this.name);
		}
	}

}
