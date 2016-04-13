package org.challenger2.NerdPlot;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class CmdMax implements NerdPlotCommand {
	
	private final NerdPlotPlugin plugin;
	private static final String name = "max";
	private static final String permission = "nerdplot.max";
	private static final String setmaxPermission = "nerdplot.setmax";

	public CmdMax(NerdPlotPlugin plugin) {
		this.plugin = plugin;
	}
	

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (args.length == 0) {
			// Get max
			if (!sender.hasPermission(permission)) {
				plugin.printUsage(sender);
				return;
			}
			PlayerHelper ph = new PlayerHelper(plugin, sender);
			if (!ph.isInitialized()) {
				return;
			}
			listMaxPlots(ph);
		} else if (args.length == 2 || args.length == 3) {
			// Set max
			if (!sender.hasPermission(setmaxPermission)) {
				plugin.printUsage(sender);
				return;
			}
			PlayerHelper ph = new PlayerHelper(plugin, sender);
			if (!ph.isInitialized()) {
				return;
			}
			
			// If the sender did not specify a world name, use the one they are in.
			String worldName;
			if (args.length == 3) {
				worldName = args[2];
			} else {
				worldName = ph.getWorldName();
			}
			setMaxPlots(ph, args[0], args[1], worldName);
		} else {
			plugin.printUsage(sender);
		}
	}
	
	
	/**
	 * Show all max plot data
	 */
	private void listMaxPlots(PlayerHelper ph) {
		List<MaxInfo> infoList = plugin.getMaxPlotInfo();
		if (infoList.size() == 0) {
			ph.sendMessage(ChatColor.GREEN + "No maximum data is set");
		} else {
			for (MaxInfo info : infoList) {
				info.print(ph.getPlayer());
			}
		}
	}
	
	
	/**
	 * Set the maximum number of plots for an area
	 */
	private void setMaxPlots(PlayerHelper ph, String areaName, String maxPlotsStr, String worldName) {
		int maxPlots;
		try {
			maxPlots = Integer.parseInt(maxPlotsStr);
		} catch (NumberFormatException e) {
			ph.sendMessage(ChatColor.RED + "Invalid number");
			return;
		}
		if (maxPlots < 0) {
			ph.sendMessage(ChatColor.RED + "It is not possible for players to have a maximum of a negative number of plots! That would break the space time continuum!");
			return;
		}
		if(plugin.setMaxPlots(worldName, areaName, maxPlots)) {
			ph.sendMessage(ChatColor.GREEN + "New maximum set");
			plugin.saveConfig();
		} else {
			ph.sendMessage(ChatColor.RED + "That area does not exist");
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
		if(sender.hasPermission(setmaxPermission)) {
			sender.sendMessage(ChatColor.GREEN + "/" + plugin.getName() + " " + name + " <area name> <new max> [<worldName>]");
		}
	}
}
