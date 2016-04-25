package org.challenger2.NerdPlot;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;


public class CmdClaim implements NerdPlotCommand {

	private final NerdPlotPlugin plugin;
	private static final String name = "claim";
	private static final String permission = "nerdplot.claim";

	public CmdClaim(NerdPlotPlugin plugin) {
		this.plugin = plugin;
	}

	
	@Override
	public void execute(CommandSender sender, String[] args) {
		
		// Check arguments and permissions
		if (!sender.hasPermission(permission)) {
			plugin.printUsage(sender);
			return;
		}

		if (args.length != 0) {
			printUsage(sender);
			return;
		}
		
		// Setup player helper
		PlayerHelper ph = new PlayerHelper(plugin, sender);
		if (!ph.isInitialized()) {
			return;
		}

		// Get the plot we want from PH.
		ProtectedRegion plot = ph.getPlot();
		if (plot == null) {
			sender.sendMessage(ChatColor.RED + "You must stand in a plot to claim it.");
			return;
		}
		if (plot.getOwners().contains(ph.getPlayerID())) {
			sender.sendMessage(ChatColor.GREEN + "You already own this plot.");
			return;
		}
		if(plot.getOwners().size() != 0) {
			sender.sendMessage(ChatColor.RED + "This plot already has an owner.");
			return;
		}
		int claimedPlots = plugin.countPlayerAreaPlots(ph.getWorldName(), ph.getPlayerID(), ph.getAreaName());
		int maxPlots     = plugin.getMaxPlots(ph.getWorldName(), ph.getAreaName());
		if (claimedPlots >= maxPlots){
			sender.sendMessage(ChatColor.RED + "You already have the maximum number of plots. Please contact a moderator.");
			return;
		}
		plugin.setPlotOwner(ph.getWorld().getName(), ph.getPlot().getId(), ph.getPlayer().getName(), ph.getPlayer().getUniqueId());
		plugin.saveConfig();
		plot.getOwners().addPlayer(ph.getPlayerID());
		sender.sendMessage(ChatColor.GREEN + "Plot " + plot.getId() + " has been granted!");

		// Ensure the player UUID and name is in the World Guard database, else /rg info shows the UUID only.
		AsyncUUIDLookup lookup = new AsyncUUIDLookup(plugin, ph.getPlayerName());
		lookup.runTaskAsynchronously(plugin);
	}
	
	
	@Override
	public String getName() {
		return name;
	}

	
	@Override
	public void printUsage(CommandSender sender) {
		if(sender.hasPermission(permission)) {
			sender.sendMessage(ChatColor.GREEN + "/" + plugin.getName() + " " + name + "  (While standing in a plot)");
		}
	}
}
