package org.challenger2.NerdPlot;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class CmdInfo implements NerdPlotCommand {

	private final NerdPlotPlugin plugin;
	private static final String name = "info";
	private static final String permission = "nerdplot.info";
	private static final String permissionAny = "nerdplot.infoany";

	
	public CmdInfo(NerdPlotPlugin plugin) {
		this.plugin = plugin;
	}

	
	@Override
	public void execute(CommandSender sender, String[] args) {
		boolean permA = sender.hasPermission(permission);
		boolean permB = sender.hasPermission(permissionAny);

		if( !((permA && args.length == 0) ||
		      (permB && args.length == 1))) {
			if (permA || permB) {
				sender.sendMessage(ChatColor.RED + "ERROR: Bad arguments");
				printUsage(sender); // show usage for this command
			} else {
				sender.sendMessage(ChatColor.RED + "ERROR: Invalid Command");
				plugin.printUsage(sender); 
			}
			return;
		}

		// Setup player helper
		PlayerHelper ph = new PlayerHelper(plugin, sender);
		if (!ph.isInitialized()) {
			return;
		}

		ProtectedRegion plot = null;
		if (args.length == 0) {
			// Find the plot we are standing in
			plot = ph.getPlot();
			if (plot == null) {
				sender.sendMessage(ChatColor.RED + "You must stand in a plot to get plot info");
				return;
			}
		} else {
			String plotName = args[0];
			plot = ph.getManager().getRegion(plotName);
			if (plot == null) {
				sender.sendMessage(ChatColor.RED + "Unknown region \"" + plotName + "\".");
				return;
			}
		}

		PlotInfo info = plugin.getPlotInfo(ph.getWorldName(), plot.getId());
		if (info ==  null) {
			sender.sendMessage(ChatColor.RED + "Unknown plot");
		} else {
			info.print(sender);
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
		if(sender.hasPermission(permissionAny)) {
			sender.sendMessage(ChatColor.GREEN + "/" + plugin.getName() + " " + name + " <plot name>");
		}
	}

}
