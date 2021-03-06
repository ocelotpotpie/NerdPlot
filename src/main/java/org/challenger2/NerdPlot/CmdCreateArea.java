package org.challenger2.NerdPlot;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class CmdCreateArea implements NerdPlotCommand {

	
	private final NerdPlotPlugin plugin;
	private static final String name = "createarea";
	private static final String permission = "nerdplot.createarea";
	
	
	public CmdCreateArea(NerdPlotPlugin plugin) {
		this.plugin = plugin;
	}
	
	
	@Override
	public void execute(CommandSender sender, String[] args) {
		// Check arguments and permissions
		if (!sender.hasPermission(permission)) {
			plugin.printUsage(sender);
			return;
		}
		
		if (args.length != 1) {
			printUsage(sender);
			return;
		}
		
		// Setup player helper
		PlayerHelper ph = new PlayerHelper(plugin, sender);
		if (!ph.isInitialized()) {
			return;
		}
		
		String areaName = args[0];
		if (!areaName.equals(areaName.toLowerCase())) {
			sender.sendMessage(ChatColor.RED + "Area names must be in all lower case letters");
			return;
		}
		
		if (plugin.createArea(ph.getWorldName(), areaName)) {
			sender.sendMessage(ChatColor.GREEN + "Area created.");
			plugin.saveConfig();
		} else {
			sender.sendMessage(ChatColor.RED + "Area already exists.");
		}
		
	}

	@Override
	public String getName() {
		return name;
	}

	
	@Override
	public void printUsage(CommandSender sender) {
		if(sender.hasPermission(permission)) {
			sender.sendMessage(ChatColor.GREEN + "/" + plugin.getName() + " " + name + " <area name>");
		}
	}

}
