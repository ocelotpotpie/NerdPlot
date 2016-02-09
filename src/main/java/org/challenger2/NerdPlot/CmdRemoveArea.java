package org.challenger2.NerdPlot;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class CmdRemoveArea implements NerdPlotCommand {

	
	private final NerdPlotPlugin plugin;
	private static final String name = "removearea";
	private static final String permission = "nerdplot.removearea";
	
	
	public CmdRemoveArea(NerdPlotPlugin plugin) {
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
		if (plugin.removeArea(ph.getWorldName(), areaName)) {
			sender.sendMessage(ChatColor.GREEN + "Area remove");
			plugin.saveConfig();
		} else {
			sender.sendMessage(ChatColor.RED + "Area does not exist");
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
