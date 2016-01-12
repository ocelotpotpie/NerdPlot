package org.challenger2.NerdPlot.Commands;

import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.challenger2.NerdPlot.NerdPlotPlugin;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.bukkit.selections.Selection;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class CommandCreate extends NerdPlotCommand {

	public CommandCreate(NerdPlotPlugin plugin) {
		super(plugin,
		    "create",
		    "create <region_prefix>",
		    "nerdplot.create"
		);
	}

	@Override
	public void execute(CommandSender sender, String[] args) {

		// Checkout all the prerequisites
		// Are we a player?
    	if (!(sender instanceof Player)) {
    		sender.sendMessage("Who are you? Your not a player.");
    		return;
    	}
    	Player player = (Player)sender;
    	
    	// Is World Guard Enabled?
		//RegionContainer container = plugin.getWG().getRegionContainer();
		//RegionManager regions = container.get(player.getWorld());
    	RegionManager manager = plugin.getWG().getRegionManager(player.getWorld());
		if (manager == null) {
			sender.sendMessage(ChatColor.RED + "WorldGuard is not enabled in this world");
			return;
		}

		// Do we have the right arguments?
    	if (args.length != 1) {
    		printUsage(sender);
    		return;
    	}
    	if(!ProtectedRegion.isValidId(args[0])) {
    		sender.sendMessage(ChatColor.RED + "Invalid region name");
    	}
    	
    	// Generate region name
    	String regionName = generatePlotName(args[0], manager);

		// Create a region from the players selection
		Selection selection = plugin.getWE().getSelection(player);
		if (selection == null) {
			sender.sendMessage(ChatColor.RED + "You must make a World Edit selection first");
			return;
		}
		if (!(selection instanceof CuboidSelection)) {
			sender.sendMessage(ChatColor.RED + "Only Cuboid selections are supported");;
			return;
		}
		Location loc;
		loc = selection.getMinimumPoint();
		BlockVector min = new BlockVector(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
		loc = selection.getMaximumPoint();
		BlockVector max = new BlockVector(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
		ProtectedRegion protectedRegion = new ProtectedCuboidRegion(regionName, min, max);
		protectedRegion.setFlag(NerdPlotPlugin.NERD_PLOT, StateFlag.State.ALLOW);

		// Put the region into world guard
		manager.addRegion(protectedRegion);

		sender.sendMessage(ChatColor.GREEN + "Plot " + regionName + " Created");
	}

	/**
	 * Generate a region name
	 * 
	 * @param prefix plot name prefix
	 * @param world world the plot is in
	 * @return
	 */
	private String generatePlotName(String prefix, RegionManager manager) {
		Map<String,ProtectedRegion> map = manager.getRegions();

		for (int i = 0; i < 10000; i++) {
			String name = String.format("%s_%04d", prefix, i);
			if (!map.containsKey(name)) {
				return name;
			}
		}
		return null;
	}


}
