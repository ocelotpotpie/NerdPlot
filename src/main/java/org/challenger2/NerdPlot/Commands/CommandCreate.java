package org.challenger2.NerdPlot.Commands;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.challenger2.NerdPlot.NerdPlotPlugin;

import com.sk89q.worldedit.BlockVector;
import com.sk89q.worldedit.bukkit.BukkitUtil;
import com.sk89q.worldedit.bukkit.selections.CuboidSelection;
import com.sk89q.worldedit.bukkit.selections.Selection;
import com.sk89q.worldguard.bukkit.RegionContainer;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedCuboidRegion;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

public class CommandCreate extends NerdPlotCommand {

	private final String name = "create";
	private final String usage = "create <region_prefix>";
	private final String permission = "nerdplot.create";

	public CommandCreate(NerdPlotPlugin plugin) {
		super(plugin);
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
		RegionContainer container = plugin.getWG().getRegionContainer();
		RegionManager regions = container.get(player.getWorld());
		if (regions == null) {
			sender.sendMessage(ChatColor.RED + "WorldGuard is not enabled in this world");
			return;
		}

		// Do we have the right arguments?
    	if (args.length != 1) {
    		printUsage(sender);
    		return;
    	}
    	String regionName = args[0];
    	if(!ProtectedRegion.isValidId(regionName)) {
    		sender.sendMessage(ChatColor.RED + "Invalid region name");
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
		ProtectedRegion protectedRegion = new ProtectedCuboidRegion(regionName, min, max);

		// Put the region into world guard
		regions.addRegion(protectedRegion);

		sender.sendMessage(ChatColor.GREEN + "Plot " + regionName + " Created");
	}

	public void printUsage(CommandSender sender) {
		sender.sendMessage(ChatColor.GREEN + plugin.getName() + " " + usage);
	}
	
	@Override
	public String getName() {
		return name;
	}

	@Override
	public String getUsage() {
		return usage;
	}

	@Override
	public String getPermission() {
		return permission;
	}
}
