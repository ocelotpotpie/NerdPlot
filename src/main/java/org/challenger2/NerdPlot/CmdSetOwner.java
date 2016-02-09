package org.challenger2.NerdPlot;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

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
			sender.sendMessage(ChatColor.RED + "You must stand in the plot you want to add the owner to");
			return;
		}
		
		// FIXME Find a better way to get the UUID from the player name
		//
		// One Idea. Create an Async task and query WorldEdit for the player to UUID lookup.
		// Then, put a task in a synchronized queue.
		// Next, schedule an Bukkit event to dispatch the queue every second or something
		// synchronously.
		//
		// Right now we will query bukkit and use whatever it gives us.
		@SuppressWarnings("deprecation")
		Player owner = plugin.getServer().getPlayer(newOwnerName);
		if (owner == null) {
			sender.sendMessage(ChatColor.RED + "Unknown player");
			sender.sendMessage(ChatColor.RED + "The player may have to be logged in for this to work (Known Bug)");
			return;
		} else {
			plugin.setPlotOwner(ph.getWorldName(), plot.getId(), owner);
			sender.sendMessage(ChatColor.GREEN + "New owner set");
			plugin.saveConfig();
		}
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
