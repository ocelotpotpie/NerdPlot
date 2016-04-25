package org.challenger2.NerdPlot;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion.CircularInheritanceException;

public class CmdCreate implements NerdPlotCommand {
	
	
	private final NerdPlotPlugin plugin;
	private static final String name = "create";
	private static final String permission = "nerdplot.create";

	
	public CmdCreate(NerdPlotPlugin plugin) {
		this.plugin = plugin;
	}

	
	@Override
	public void execute(CommandSender sender, String[] args) {
		
		if (!sender.hasPermission(permission)) {
			plugin.printUsage(sender);
			return;
		}
		
		// Do we have the right arguments?
    	if (args.length != 1 && args.length != 2) {
    		printUsage(sender);
    		return;
    	}
    	
    	// Check the area name
    	String areaName = args[0];
    	
		// Setup player helper
		PlayerHelper ph = new PlayerHelper(plugin, sender);
		if (!ph.isInitialized()) {
			return;
		}

    	if (!plugin.isArea(ph.getWorldName(), areaName)) {
    		sender.sendMessage(ChatColor.RED + "Area does not exist.");
    		return;
    	}

		// Get the requested parent region
    	ProtectedRegion parent = null;
		if (args.length == 2) {
			String parentName = args[1];
			parent = ph.getManager().getRegion(parentName);
			if (parent == null) {
				sender.sendMessage(ChatColor.RED + "Parent region does not exist.");
				return;
			}
		}

    	// Generate region name
    	String plotName = Util.generatePlotName(areaName, ph.getManager().getRegions());
    	if (plotName == null) {
    		sender.sendMessage(ChatColor.RED + "ERROR: Could not generate plot name!!11!!");
    		return;
    	}

    	if(!ProtectedRegion.isValidId(plotName)) {
    		sender.sendMessage(ChatColor.RED + "Invalid region/plot name.");
    		return;
    	}

		ProtectedRegion protectedRegion = ph.regionFromSelection(plotName);
		if (protectedRegion == null ) {
			return;
		}

		if (parent != null) {
			try {
				protectedRegion.setParent(parent);
			} catch (CircularInheritanceException e) {
				sender.sendMessage(ChatColor.RED + "Invalid parent. This will cause circulair dependencies cause a causality loop in the space time continuum!");
				return;
			}
		}

		// Put the region into world guard
		ph.getManager().addRegion(protectedRegion);
		plugin.addPlot(ph.getWorldName(), plotName, areaName);
		plugin.saveConfig();

		sender.sendMessage(ChatColor.GREEN + "Plot " + plotName + " Created.");

	}
	
	
	@Override
	public String getName() {
		return name;
	}

	
	@Override
	public void printUsage(CommandSender sender) {
		if(sender.hasPermission(permission)) {
			sender.sendMessage(ChatColor.GREEN + "/" + plugin.getName() + " " + name + " <area> [<parent>]");
		}
	}
}
