package org.challenger2.NerdPlot;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class CmdSetOwner implements NerdPlotCommand {

	private final NerdPlotPlugin plugin;
	private static final String name = "setowner";
	private static final String permission = "nerdplot.setowner";
	

	public CmdSetOwner(NerdPlotPlugin plugin) {
		this.plugin = plugin;
	}
	

	@Override
	public void execute(CommandSender sender, String[] args) {

		if (!sender.hasPermission(permission)) {
			plugin.printUsage(sender);
			return;
		}

		if (args.length != 1) {
			printUsage(sender);
			return;
		}

		final String newOwnerName = args[0];
		
		// Setup player helper
		PlayerHelper ph = new PlayerHelper(plugin, sender);
		if (!ph.isInitialized()) {
			return;
		}

		// Find the plot we are standing in
		ProtectedRegion plot = ph.getPlot();
		if (plot == null) {
			sender.sendMessage(ChatColor.RED + "You must stand in the plot you want to add the owner to.");
			return;
		}
	
		@SuppressWarnings("deprecation")
		OfflinePlayer owner = plugin.getServer().getOfflinePlayer(newOwnerName);
		if (owner == null) {
			sender.sendMessage(ChatColor.RED + "Unknown player \"" + newOwnerName + "\"");
			return;
		}
		
		// Remove the old owner if there was one.
		PlotInfo plotInfo = this.plugin.getPlotInfo(ph.getWorldName(), plot.getId());
		if (plotInfo.ownerID != null) {
			UUID currentOwnerUUID = UUID.fromString(plotInfo.ownerID);
			plot.getOwners().removePlayer(currentOwnerUUID);
		}

		// Set the new owner.
		plugin.setPlotOwner(ph.getWorldName(), plot.getId(), owner.getName(), owner.getUniqueId());
		plot.getOwners().addPlayer(owner.getUniqueId());

		// All done
		sender.sendMessage(ChatColor.GREEN + "New owner set");
		plugin.saveConfig();
		
	}
	
	
	@Override
	public String getName() {
		return name;
	}
	

	@Override
	public void printUsage(CommandSender sender) {
		if(sender.hasPermission(permission)) {
			sender.sendMessage(ChatColor.GREEN + "/" + plugin.getName() + " " + name + " <new owner>");
		}
	}

}
