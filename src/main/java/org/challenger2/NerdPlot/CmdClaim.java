package org.challenger2.NerdPlot;

import java.util.UUID;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.google.common.util.concurrent.FutureCallback;
import com.sk89q.worldguard.domains.DefaultDomain;

public class CmdClaim extends NerdPlotCommand {
	
	private final String permission = "nerdplot.claim";

	public CmdClaim(NerdPlotPlugin plugin) {
		super(plugin, "claim");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		
		if (!sender.hasPermission(permission)) {
			plugin.printUsage(sender);
			return;
		}

		if (args.length != 0) {
			printUsage(sender);
			return;
		}
		
		// Checkout all the prerequisites
		// Are we a player?
    	if (!(sender instanceof Player)) {
    		sender.sendMessage("Who are you? Your not a player.");
    		return;
    	}
    	final Player player = (Player)sender;
    	final String playerName = player.getName();
    	final String worldName = player.getWorld().getName();

    	// Is World Guard Enabled?
    	RegionManager manager = plugin.getWG().getRegionManager(player.getWorld());
		if (manager == null) {
			sender.sendMessage(ChatColor.RED + "WorldGuard is not enabled in this world");
			return;
		}
		
		if (plugin.getAllPlayerPlots(player.getUniqueId()).size() >= plugin.getMaxPlots()) {
			sender.sendMessage(ChatColor.RED + "You already have the maximum number of plots. Please contact a moderator");
			return;
		}

		// Find the first plot that we can claim.
		ApplicableRegionSet regions = manager.getApplicableRegions(player.getLocation());
		ProtectedRegion plotTmp = null;
		for (ProtectedRegion check : regions) {
			if (plugin.isPlot(worldName, check.getId())) {
				if(check.getOwners().getPlayers().size() == 0) {
					// We found what we are looking for
					plotTmp = check;
					break;
				}
			}
		}
		final ProtectedRegion plot = plotTmp;

		if (plot == null) {
			sender.sendMessage(ChatColor.RED + "There are not any available plots where you are standing");
			return;
		}

		if (plugin.getPlotOwner(worldName, plot.getId()) != null){
			sender.sendMessage(ChatColor.RED + "This plot is already taken");
			return;
		}

		// Get player ID via callback
		plugin.lookupPlayerUUID(playerName, new FutureCallback<DefaultDomain>() {

		    @Override
		    public void onSuccess(DefaultDomain result) {
				plot.getOwners().addAll(result);
				for (UUID uuid : result.getUniqueIds()) {
					plugin.setPlotOwner(worldName, plot.getId(), uuid);
				}
				plugin.saveMyConfig();
				player.sendMessage(ChatColor.GREEN + "Plot " + plot.getId() + " has been granted!");
		    }

		    @Override
		    public void onFailure(Throwable throwable) {
		    	player.sendMessage(ChatColor.RED + "Unknown player: " + playerName);
		    }
		});

	}

	@Override
	public void printUsage(CommandSender sender) {
		if(sender.hasPermission(permission)) {
			sender.sendMessage(ChatColor.GREEN + "/" + plugin.getName() + " claim (While standing in a plot)");
		}
	}
}
