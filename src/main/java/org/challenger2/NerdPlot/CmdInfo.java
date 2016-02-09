package org.challenger2.NerdPlot;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class CmdInfo implements NerdPlotCommand {

	private final NerdPlotPlugin plugin;
	private static final String name = "info";
	private static final String permission = "nerdplot.info";

	
	public CmdInfo(NerdPlotPlugin plugin) {
		this.plugin = plugin;
	}

	
	@Override
	public void execute(CommandSender sender, String[] args) {
		if (!sender.hasPermission(permission)) {
			plugin.printUsage(sender);
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
			sender.sendMessage(ChatColor.RED + "You must stand in the plot to get plot info");
			return;
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
	}

}
