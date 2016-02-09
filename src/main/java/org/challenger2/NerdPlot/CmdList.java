package org.challenger2.NerdPlot;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class CmdList implements NerdPlotCommand {
	
	
	private final NerdPlotPlugin plugin;
	private static final String name = "list";
	private static final String permission = "nerdplot.list";
	private static final String listallPermission = "nerdplot.listall";

	public CmdList(NerdPlotPlugin plugin) {
		this.plugin = plugin;
	}

	
	@Override
	public void execute(CommandSender sender, String[] args) {

		if (args.length == 0 && !sender.hasPermission(permission) ||
	        args.length == 1 && !sender.hasPermission(listallPermission) ||
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
			//TODO: Find a way to convert name to UUID
			List<PlotInfo> plotInfo = plugin.getAllOwnerPlots(args[1]);
			sender.sendMessage(ChatColor.RED + "NOTE: This command does not take player renames into account!");
			if (plotInfo.size() == 0) {
				sender.sendMessage(ChatColor.GREEN + args[1] + " does not have any plots");
			} else {
				for (PlotInfo info : plotInfo ) {
					sender.sendMessage(ChatColor.GREEN + "  Area: " + info.areaName + ", Plot: " + info.plotName);
				}
			}
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
		if(sender.hasPermission(listallPermission)) {
			sender.sendMessage(ChatColor.GREEN + "/" + plugin.getName() + " " + name + " <player>");
		}
	}

}
