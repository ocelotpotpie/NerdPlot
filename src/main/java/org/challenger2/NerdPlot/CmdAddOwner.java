package org.challenger2.NerdPlot;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class CmdAddOwner extends NerdPlotCommand {

	private final String permission = "nerdplot.addowner";

	public CmdAddOwner(NerdPlotPlugin plugin) {
		super(plugin, "addowner");
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
			sender.sendMessage(ChatColor.RED + "You must stand in the plot you want to add the owner to");
			return;
		}
		
		//now just stash the player in the plot
		String owner = plugin.getPlotOwner(worldName, player.getName());
		if (owner == null) {
			plugin.setPlotOwner(worldName, plot.getId(), playerName);
			plot.getOwners().addPlayer(playerName);
			sender.sendMessage(ChatColor.GREEN + playerName + " is the new owner.");
			plugin.saveMyConfig();
		} else {
			sender.sendMessage(ChatColor.RED + owner + " already owns this plot!");
		}		
	}

	@Override
	public void printUsage(CommandSender sender) {
		if(sender.hasPermission(permission)) {
			sender.sendMessage(ChatColor.GREEN + "/" + plugin.getName() + " " + this.name);
		}
	}

}
