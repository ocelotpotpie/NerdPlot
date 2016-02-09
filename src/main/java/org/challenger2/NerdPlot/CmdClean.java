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

		if (args.length != 0) {
			printUsage(sender);
			return;
		}
		sender.sendMessage(ChatColor.RED + "This command does not exist yet");		
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
