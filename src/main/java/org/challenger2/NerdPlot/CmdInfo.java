package org.challenger2.NerdPlot;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class CmdInfo extends NerdPlotCommand {
	
	private final String permission = "nerdplot.info";

	public CmdInfo(NerdPlotPlugin plugin) {
		super(plugin, "info");
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
    	String worldName = player.getWorld().getName();
    	
    	// Is World Guard Enabled?
		//RegionContainer container = plugin.getWG().getRegionContainer();
		//RegionManager regions = container.get(player.getWorld());
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
			sender.sendMessage(ChatColor.RED + "You must stand in the plot to get plot info");
			return;
		}

		String plotOwner = "<Unclaimed plot>";
		String plotName = plot.getId();
		UUID plotOwnerUUID = plugin.getPlotOwner(worldName,  plotName);
		if (plotOwnerUUID != null) {
			Player p = plugin.getServer().getPlayer(plotOwnerUUID);
			if (p == null) {
				plotOwner = "{" + plotOwnerUUID.toString() + "}";
			} else {
				plotOwner = p.getName();
			}
		}
		player.sendMessage(ChatColor.GREEN + "Plot Name: " + plotName);
		player.sendMessage(ChatColor.GREEN + "Plot Owner: " + plotOwner);

	}

	@Override
	public void printUsage(CommandSender sender) {
		if(sender.hasPermission(permission)) {
			sender.sendMessage(ChatColor.GREEN + "/" + plugin.getName() + " " + this.name);
		}
	}

}
