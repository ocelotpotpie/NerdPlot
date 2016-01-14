package org.challenger2.NerdPlot.Commands;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.challenger2.NerdPlot.NerdPlotPlugin;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class CmdClaim extends NerdPlotCommand {

	public CmdClaim(NerdPlotPlugin plugin) {
		super(plugin,
		    "claim",
			"claim (While standing in a plot)",
			"nerdplot.claim"
			);
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (args.length > 0) {
			printUsage(sender);
			return;
		}
		
		// Checkout all the prerequisites
		// Are we a player?
    	if (!(sender instanceof Player)) {
    		sender.sendMessage("Who are you? Your not a player.");
    		return;
    	}
    	Player player = (Player)sender;
    	String worldName = player.getWorld().getName();
    	
    	// Is World Guard Enabled?
    	RegionManager manager = plugin.getWG().getRegionManager(player.getWorld());
		if (manager == null) {
			sender.sendMessage(ChatColor.RED + "WorldGuard is not enabled in this world");
			return;
		}

		// Find the first plot that we can claim.
		ApplicableRegionSet regions = manager.getApplicableRegions(player.getLocation());
		ProtectedRegion plot = null;
		for (ProtectedRegion check : regions) {
			if (plugin.isPlot(worldName, check.getId())) {
				if(check.getOwners().getPlayers().size() == 0) {
					// We found what we are looking for
					plot = check;
					break;
				}
			}
		}

		if (plot == null) {
			sender.sendMessage(ChatColor.RED + "There are not any available plots where you are standing");
			return;
		}
		
		//if (plugin.getAllPlayerPlots(player.getName()).size() >= plugin.maxPlots())
		//TODO Make sure the player has max X plots

		plot.getOwners().addPlayer(player.getName());
		plugin.setPlotOwner(worldName, plot.getId(), player.getName());
		plugin.saveMyConfig();

		sender.sendMessage(ChatColor.GREEN + "Plot " + plot.getId() + " has been granted!");
	}
}
