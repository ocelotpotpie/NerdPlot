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
		plugin.setPlotOwner(ph.getWorldName(), ph.getPlot().getId(), ph.getPlayerName(), ph.getPlayerID());
		plugin.saveConfig();

		// Fire off a command to WorldGuard to add players. This ensures the WG
		// UUID cache is properly updated and a UUID isn't displayed instead of the
		// player name.
		
		// Option A
		// Just set the owner. But WorldGuard will not cache the user name
		//plot.getOwners().addPlayer(ph.getPlayerID());
		//sender.sendMessage(ChatColor.GREEN + "Plot " + plot.getId() + " has been granted!");
		
		// Option B
		String cmd = "region addowner -w " + ph.getWorldName() + " " + plot.getId() + " " + ph.getPlayerName();
		plugin.getLogger().info("Running command: /" + cmd);
		plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), cmd);
		sender.sendMessage(ChatColor.GREEN + "Plot " + plot.getId() + " has been granted!");

//		 /// Option C
//		 /// ( Code adopted from World Guard own add owner command )
//         // Resolve owners asynchronously
		// Gets "Unknown function: getProfileService() error"
//		String[] ids = new String[1];
//		ids[0] = ph.getPlayerID().toString();
//
//        DomainInputResolver resolver = new DomainInputResolver(
//                plugin.getWG().getProfileService(), ids);
//        resolver.setLocatorPolicy(UserLocatorPolicy.UUID_ONLY);
//
//        // Then add it to the owners
//        ListenableFuture<DefaultDomain> future = Futures.transform(
//                plugin.getWG().getExecutorService().submit(resolver),
//                resolver.createAddAllFunction(plot.getOwners()));
//        
//        AsyncCommandHelper.wrap(future, plugin.getWG(), sender)
//        .formatUsing(plot.getId(), ph.getWorldName())
//        .registerWithSupervisor("Adding owners to the plot '%s' on '%s'")
//        .sendMessageAfterDelay("(Please wait... querying player names...)")
//        .thenRespondWith("Plot '%s' updated with new owners.", "Failed to add new owners. Contact a mod for assistance.");
//
//        sender.sendMessage(ChatColor.GREEN + "Plot " + plot.getId() + " has been granted!");
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
