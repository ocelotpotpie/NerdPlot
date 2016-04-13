package org.challenger2.NerdPlot;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class CmdRemove implements NerdPlotCommand {
	
	private final NerdPlotPlugin plugin;
	private static final String name = "remove";
	private static final String permission = "nerdplot.remove";

	
	public CmdRemove(NerdPlotPlugin plugin) {
		this.plugin = plugin;
	}

	
	@Override
	public void execute(CommandSender sender, String[] args) {

		if (!sender.hasPermission(permission)) {
			plugin.printUsage(sender);
			return;
		}
		
		if (args.length != 1 || !args[0].equalsIgnoreCase("-f")) {
			sender.sendMessage(ChatColor.RED + "Use -f if you are SURE you want to delete this plot forever!?!?");
			sender.sendMessage(ChatColor.RED + "(Forever is a long time)");
			printUsage(sender);
			return;
		}
		
		// Setup player helper
		PlayerHelper ph = new PlayerHelper(plugin, sender);
		if (!ph.isInitialized()) {
			return;
		}

		// Find the plot we are standing in
		ProtectedRegion plot = ph.getPlot();
		if (plot == null) {
			sender.sendMessage(ChatColor.RED + "You must stand in the plot you want to remove");
			return;
		}
		String plotName = plot.getId();
    	
    	if (plugin.isPlot(ph.getWorldName(), plotName)) {
    		plugin.removePlot(ph.getWorldName(), plotName);
    		ph.getManager().removeRegion(plotName);
    		sender.sendMessage(ChatColor.GREEN + "Plot " + plotName + " removed");
    	} else {
    		sender.sendMessage(ChatColor.RED + "Plot " + plotName + " does not exist");
    	}

	}
	
	
	@Override
	public String getName() {
		return name;
	}
	
	
	@Override
	public void printUsage(CommandSender sender) {
		if(sender.hasPermission(permission)) {
			sender.sendMessage(ChatColor.GREEN + "/" + plugin.getName() + " " + name + " -f");
		}
	}

}
