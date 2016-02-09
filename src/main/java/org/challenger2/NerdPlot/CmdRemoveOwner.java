package org.challenger2.NerdPlot;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class CmdRemoveOwner implements NerdPlotCommand {

	private final NerdPlotPlugin plugin;
	private static final String name = "removeowner";
	private static final String permission = "nerdplot.removeowner";

	
	public CmdRemoveOwner(NerdPlotPlugin plugin) {
		this.plugin = plugin;
	}

	
	@Override
	public void execute(CommandSender sender, String[] args) {

		if (!sender.hasPermission(permission)) {
			plugin.printUsage(sender);
			return;
		}

		if (args.length != 0) {
			printUsage(sender);
			return;
		}

		// Setup player helper
		PlayerHelper ph = new PlayerHelper(plugin, sender);
		if (!ph.isInitialized()) {
			return;
		}

		// Find the plot we are standing in
		ProtectedRegion plot = ph.getPlot();
		if (plot == null) {
			sender.sendMessage(ChatColor.RED + "You must stand in the plot you want to remove the owner from");
			return;
		}

		PlotInfo pinfo = plugin.getPlotInfo(ph.getWorldName(), plot.getId());
		if (pinfo.ownerID == null) {
			sender.sendMessage(ChatColor.RED + "This plot does already not have an owner");
		} else {
			plot.getOwners().removePlayer(UUID.fromString(pinfo.ownerID));
			plugin.setPlotOwner(ph.getWorldName(), plot.getId(), null);
			plugin.saveConfig();
			sender.sendMessage(ChatColor.GREEN + pinfo.ownerName + " removed from plot " + plot.getId());
		}
	}
	
	
	@Override
	public String getName() {
		return name;
	}
	

	@Override
	public void printUsage(CommandSender sender) {
		if(sender.hasPermission(permission)) {
			sender.sendMessage(ChatColor.GREEN + "/" + plugin.getName() + " " + name);
		}
	}

}
