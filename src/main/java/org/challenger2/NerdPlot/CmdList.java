package org.challenger2.NerdPlot;

import java.util.List;
import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class CmdList implements NerdPlotCommand {
	
	
	private final NerdPlotPlugin plugin;
	private static final String name = "list";
	private static final String permission = "nerdplot.list";
	private static final String listAnyPermission = "nerdplot.listany";

	public CmdList(NerdPlotPlugin plugin) {
		this.plugin = plugin;
	}

	
	@Override
	public void execute(CommandSender sender, String[] args) {

		if (args.length == 0 && !sender.hasPermission(permission) ||
	        args.length == 1 && !sender.hasPermission(listAnyPermission) ||
	        args.length > 1){
			plugin.printUsage(sender);
			return;
		}
		
		// Setup player helper
		PlayerHelper ph = new PlayerHelper(plugin, sender);
		if (!ph.isInitialized()) {
			return;
		}

		if(args.length == 0) {
			List<PlotInfo> plotInfo = plugin.getAllOwnerPlots(ph.getPlayerID());
			if (plotInfo.size() == 0) {
				sender.sendMessage(ChatColor.GREEN + "You do not have any plots");
			} else {
				for (PlotInfo info : plotInfo ) {
					sender.sendMessage(ChatColor.GREEN + "  Area: " + info.areaName + ", " + info.plotName);
				}
			}
		} else if (args.length == 1) {
			List<PlotInfo> plotInfo;
			try {
				UUID playerID = UUID.fromString(args[0]);
				plotInfo = plugin.getAllOwnerPlots(playerID);
			} catch (IllegalArgumentException e) {
				//TODO: Find a way to convert player name to UUID
				// The below uses the players last known name
				plotInfo = plugin.getAllOwnerPlots(args[0]);
			}
			sender.sendMessage(ChatColor.RED + "NOTE: This command does not take player renames into account!");
			if (plotInfo.size() == 0) {
				sender.sendMessage(ChatColor.GREEN + args[1] + " does not have any plots");
			} else {
				for (PlotInfo info : plotInfo ) {
					sender.sendMessage(ChatColor.GREEN + "  Area: " + info.areaName + ", Plot: " + info.plotName);
				}
			}
		} else {
			plugin.printUsage(sender);
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
		if(sender.hasPermission(listAnyPermission)) {
			sender.sendMessage(ChatColor.GREEN + "/" + plugin.getName() + " " + name + " <player>");
		}
	}

}
