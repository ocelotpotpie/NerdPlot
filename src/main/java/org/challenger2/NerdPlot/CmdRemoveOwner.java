package org.challenger2.NerdPlot;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class CmdRemoveOwner extends NerdPlotCommand {

	private final String permission = "nerdplot.removeowner";

	public CmdRemoveOwner(NerdPlotPlugin plugin) {
		super(plugin, "removeowner");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {

		if (!sender.hasPermission(permission)) {
			plugin.printUsage(sender);
			return;
		}

		if (args.length != 1) {
			printUsage(sender);
			return;
		}

		String playerName = args[0];
		
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

		//remove the player from the plot
		String owner = plugin.getPlotOwner(worldName, plot.getId());

		plugin.logInfo(String.format("Owner = %s, playerName = %s", owner, playerName));
		
		if (playerName.equalsIgnoreCase(owner)) {
			plugin.removePlotOwner(worldName, plot.getId());
			plot.getOwners().removePlayer(playerName);
			player.sendMessage(ChatColor.GREEN + playerName + " removed");
			plugin.saveMyConfig();
		} else {
			player.sendMessage(ChatColor.RED + playerName + " is not an owner of " + plot.getId());
		}
	}

	@Override
	public void printUsage(CommandSender sender) {
		if(sender.hasPermission(permission)) {
			sender.sendMessage(ChatColor.GREEN + "/" + plugin.getName() + " " + this.name);
		}
	}

}
