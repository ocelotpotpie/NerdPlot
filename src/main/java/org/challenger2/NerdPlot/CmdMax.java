package org.challenger2.NerdPlot;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class CmdMax extends NerdPlotCommand {
	
	private final String permission = "nerdplot.max";
	private final String setmaxPermission = "nerdplot.setmax";

	public CmdMax(NerdPlotPlugin plugin) {
		super(plugin, "max");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (args.length == 0) {
			if (!sender.hasPermission(permission)) {
				plugin.printUsage(sender);
				return;
			}
			sender.sendMessage(ChatColor.GREEN + "You can have a maximum of " + plugin.getMaxPlots() + " plots.");
		} else if (args.length == 1) {
			try {
				int newMax = Integer.parseInt(args[0]);
				if (newMax < 0) {
					sender.sendMessage(ChatColor.RED + "It is not possible for players to have a maximum of a negative number of plots! That would break the space time continuum!");
					return;
				}
				plugin.setMaxPlots(newMax);
				plugin.saveMyConfig();
				sender.sendMessage(ChatColor.GREEN + "New maximum set to " + newMax);
			} catch (NumberFormatException e) {
				sender.sendMessage(ChatColor.RED + "Invalid argument");
			}
		} else {
			plugin.printUsage(sender);
		}
	}

	@Override
	public void printUsage(CommandSender sender) {
		if(sender.hasPermission(permission)) {
			sender.sendMessage(ChatColor.GREEN + "/" + plugin.getName() + " " + this.name);
		}
		if(sender.hasPermission(setmaxPermission)) {
			sender.sendMessage(ChatColor.GREEN + "/" + plugin.getName() + " " + this.name + " <newMax>");
		}
	}
}
