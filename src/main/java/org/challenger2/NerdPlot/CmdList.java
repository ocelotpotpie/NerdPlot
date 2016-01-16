package org.challenger2.NerdPlot;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class CmdList extends NerdPlotCommand {
	
	private final String permission = "nerdplot.list";
	private final String listallPermission = "nerdplot.listall";

	public CmdList(NerdPlotPlugin plugin) {
		super(plugin, "list");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		
		if (args.length == 0 && !sender.hasPermission(permission) ||
	        args.length == 1 && !sender.hasPermission(listallPermission) ||
	        args.length > 1){
			plugin.printUsage(sender);
			return;
		}
		
		String name;
		if (args.length == 0) {
			name = sender.getName();
		} else {
			name = args[0];
		}

		List<String> plots = plugin.getAllPlayerPlots(name);
		for (String plot : plots) {
			sender.sendMessage(ChatColor.GREEN + plot);
		}
		
		if (args.length == 0) {
			sender.sendMessage(ChatColor.GREEN + "You have " + plots.size() + " out of " + plugin.getMaxPlots() + " maximum.");
		} else {
			sender.sendMessage(ChatColor.GREEN + name + " has " + plots.size() + " out of " + plugin.getMaxPlots() + " maximum.");
		}
	}

	@Override
	public void printUsage(CommandSender sender) {
		if(sender.hasPermission(permission)) {
			sender.sendMessage(ChatColor.GREEN + "/" + plugin.getName() + " " + this.name);
		}
		if(sender.hasPermission(listallPermission)) {
			sender.sendMessage(ChatColor.GREEN + "/" + plugin.getName() + " " + this.name + " <player>");
		}
	}
}
