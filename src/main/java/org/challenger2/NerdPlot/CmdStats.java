package org.challenger2.NerdPlot;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class CmdStats implements NerdPlotCommand {

	private final NerdPlotPlugin plugin;
	private static final String name = "stats";
	private static final String permission = "nerdplot.stats";

	
	public CmdStats(NerdPlotPlugin plugin) {
		this.plugin = plugin;
	}

	
	@Override
	public void execute(CommandSender sender, String[] args) {
		if (!sender.hasPermission(permission)) {
			plugin.printUsage(sender); 
			return;
		}
		
		plugin.printPlotStats(sender);
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
