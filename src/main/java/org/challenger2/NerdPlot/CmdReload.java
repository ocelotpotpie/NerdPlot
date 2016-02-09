package org.challenger2.NerdPlot;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class CmdReload implements NerdPlotCommand {

	
	private final NerdPlotPlugin plugin;
	private static final String name = "reload";
	private static final String permission = "nerdplot.reload";
	
	
	public CmdReload(NerdPlotPlugin plugin) {
		this.plugin = plugin;
	}
	
	
	@Override
	public void execute(CommandSender sender, String[] args) {
		// Check arguments and permissions
		if (!sender.hasPermission(permission)) {
			plugin.printUsage(sender);
			return;
		}
		
		if (args.length != 0) {
			printUsage(sender);
			return;
		}
		plugin.reloadConfig();
		sender.sendMessage(ChatColor.GREEN + "Configuration reloaded");
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
