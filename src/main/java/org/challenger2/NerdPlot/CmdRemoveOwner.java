package org.challenger2.NerdPlot;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.util.concurrent.FutureCallback;
import com.sk89q.worldguard.domains.DefaultDomain;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class CmdRemoveOwner extends NerdPlotCommand {

	private final String permission = "nerdplot.removeowner";

	public CmdRemoveOwner(NerdPlotPlugin plugin) {
		super(plugin, "removeowner");
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
		final String removePlayerName = args[0];

		// Checkout all the prerequisites
		// Are we a player?
    	if (!(sender instanceof Player)) {
    		sender.sendMessage("Who are you? Your not a player.");
    		return;
    	}
    	final Player player = (Player)sender;
    	final String worldName = player.getWorld().getName();

    	// Is World Guard Enabled?
    	RegionManager manager = plugin.getWG().getRegionManager(player.getWorld());
		if (manager == null) {
			sender.sendMessage(ChatColor.RED + "WorldGuard is not enabled in this world");
			return;
		}

		// Find the plot we are standing in
		ApplicableRegionSet regions = manager.getApplicableRegions(player.getLocation());
		ProtectedRegion plotTmp = null;
		for (ProtectedRegion check : regions) {
			if (plugin.isPlot(worldName, check.getId())) {
				// We found what we are looking for
				plotTmp = check;
				break;
			}
		}
		if (plotTmp == null) {
			sender.sendMessage(ChatColor.RED + "You must stand in the plot you want to remove the owner from");
			return;
		}
		final ProtectedRegion plot = plotTmp;

		plugin.lookupPlayerUUID(removePlayerName, new FutureCallback<DefaultDomain>() {

		    @Override
		    public void onSuccess(DefaultDomain result) {
		    	for (UUID uuid : result.getUniqueIds()) {
		    		plot.getOwners().removePlayer(uuid);
		    		plugin.setPlotOwner(worldName, plot.getId(), null);
					player.sendMessage(ChatColor.GREEN + removePlayerName + " removed from plot " + plot.getId());
					plugin.saveMyConfig();
		    	}
			}

		    @Override
		    public void onFailure(Throwable throwable) {
		    	player.sendMessage(ChatColor.RED + "Unknown player: " + removePlayerName);
		    }
		});

	}

	@Override
	public void printUsage(CommandSender sender) {
		if(sender.hasPermission(permission)) {
			sender.sendMessage(ChatColor.GREEN + "/" + plugin.getName() + " " + this.name);
		}
	}

}
