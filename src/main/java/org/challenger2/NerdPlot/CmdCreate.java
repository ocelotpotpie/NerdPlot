package org.challenger2.NerdPlot;

import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.bukkit.selections.Selection;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion.CircularInheritanceException;

public class CmdCreate extends NerdPlotCommand {
	
	private final String permission = "nerdplot.create";

	public CmdCreate(NerdPlotPlugin plugin) {
		super(plugin, "create");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		
		if (!sender.hasPermission(permission)) {
			plugin.printUsage(sender);
			return;
		}

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
    	if (args.length != 1 && args.length != 2) {
    		printUsage(sender);
    		return;
    	}

    	ProtectedRegion parent = null;
		if (args.length >= 2) {
			String parentName = args[1];
			parent = manager.getRegion(parentName);
			if (parent == null) {
				sender.sendMessage(ChatColor.RED + "Parent region does not exist");
				return;
			}
		}

    	// Generate region name
    	String plotName = generatePlotName(args[0], manager);

    	if(!ProtectedRegion.isValidId(args[0])) {
    		sender.sendMessage(ChatColor.RED + "Invalid region name");
    		return;
    	}

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
		ProtectedRegion protectedRegion = new ProtectedCuboidRegion(plotName, min, max);

		if (parent != null) {
			try {
				protectedRegion.setParent(parent);
			} catch (CircularInheritanceException e) {
				e.printStackTrace();
				return; // This shouldn't be possible
			}
		}

		// Put the region into world guard
		manager.addRegion(protectedRegion);
		plugin.addPlot(player.getWorld().getName(), plotName);
		plugin.saveMyConfig();

		sender.sendMessage(ChatColor.GREEN + "Plot " + plotName + " Created");
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

	@Override
	public void printUsage(CommandSender sender) {
		if(sender.hasPermission(permission)) {
			sender.sendMessage(ChatColor.GREEN + "/" + plugin.getName() + " " + this.name + " <plot_name> [<parent_name>]");
		}
	}
}
