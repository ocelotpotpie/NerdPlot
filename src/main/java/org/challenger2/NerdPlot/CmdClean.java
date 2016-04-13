package org.challenger2.NerdPlot;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class CmdClean implements NerdPlotCommand {

	private final NerdPlotPlugin plugin;
	private static final String name = "clean";
	private static final String permission = "nerdplot.clean";

	public CmdClean(NerdPlotPlugin plugin) {
		this.plugin = plugin;
	}

	
	@Override
	public void execute(CommandSender sender, String[] args) {
		
		if (!sender.hasPermission(permission)) {
			plugin.printUsage(sender);
			return;
		}

		if (args.length > 1) {
			printUsage(sender);
			return;
		}
		
		boolean force = false;
		if (args.length == 1) {
			if (args[0].equals("-f")) {
				force = true;
			} else {
				printUsage(sender);
				return;
			}
		}

		plugin.cleanupDatabase(sender, force);
		if (force) {
			plugin.saveConfig();
		}
		sender.sendMessage(ChatColor.GREEN + "Cleanup Complete.");		
	}
	
	
	@Override
	public String getName() {
		return name;
	}
	

	@Override
	public void printUsage(CommandSender sender) {
		if(sender.hasPermission(permission)) {
			sender.sendMessage(ChatColor.GREEN + "/" + plugin.getName() + " " + name + " [-f]");
		}
	}
}
