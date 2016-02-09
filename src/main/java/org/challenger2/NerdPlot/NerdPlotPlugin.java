package org.challenger2.NerdPlot;

import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class NerdPlotPlugin extends JavaPlugin {

	private static final Logger log = Logger.getLogger("Minecraft");
	private static final String cmdName = "nerdplot";

	private WorldGuardPlugin wg;
	private WorldEditPlugin we;
	private Map<String, NerdPlotCommand> plotCommands;
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");


	/**
	 * Enable the plugin.
	 * 
	 * Grab WE, WG references
	 * Load all commands and config file
	 * 
	 */
    @Override
    public void onEnable() {
		Plugin plugin = getServer().getPluginManager().getPlugin("WorldGuard");
		if (plugin == null || !(plugin instanceof WorldGuardPlugin)) {
			logSevere("Failed to load WorldGuard.");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		wg = (WorldGuardPlugin)plugin;

		plugin = getServer().getPluginManager().getPlugin("WorldEdit");
		if (plugin == null || !(plugin instanceof WorldEditPlugin)) {
			logSevere("Failed to load WorldEdit.");
			getServer().getPluginManager().disablePlugin(this);
			return;
		}
		we = (WorldEditPlugin)plugin;

		this.saveDefaultConfig();
		this.getConfig().set("fileFormatVersion", "1.0"); // Set the format version for future use

		// Add commands (in the order we want USAGE to pring)
		plotCommands = new LinkedHashMap<String, NerdPlotCommand>();
		addCommand(new CmdClaim(this));
		addCommand(new CmdInfo(this));
		addCommand(new CmdList(this));
		addCommand(new CmdMax(this));
		addCommand(new CmdCreate(this));
		addCommand(new CmdRemove(this));
		addCommand(new CmdSetOwner(this));
		addCommand(new CmdRemoveOwner(this));
		addCommand(new CmdCreateArea(this));
		addCommand(new CmdRemoveArea(this));
		addCommand(new CmdClean(this));
		addCommand(new CmdReload(this));
		addCommand(new CmdVersion(this));
    }
    
    
    private void addCommand(NerdPlotCommand cmd) {
    	plotCommands.put(cmd.getName(), cmd);
    }

    
    /**
     * Shutdown our plugin
     */
    @Override
    public void onDisable() {
    	this.saveConfig();
    	plotCommands = null;
    	wg = null;
    	we = null;
    }

    
    /**
     * Process user commands
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String name, String[] args) {

    	//logInfo("Received command: " + name + " numArgs = " + args.length);

    	if (!name.equalsIgnoreCase(cmdName)) {
    		return false;
    	}

    	if (we == null) {
    		sender.sendMessage(ChatColor.RED + "WorldEdit cannot be found. Please contact an administrator");
    		return false;
    	}
    	
    	if (wg == null) {
    		sender.sendMessage(ChatColor.RED + "WorldGuard cannot be found. Please contact an administrator");
    		return false;
    	}
    	
    	if (args.length < 1) {
    		printUsage(sender);
    		return true;
    	}
    	
    	String subCommand = args[0].toLowerCase();

    	NerdPlotCommand cmd = plotCommands.get(subCommand);
    	if (cmd != null) {
    		cmd.execute(sender, Arrays.copyOfRange(args, 1, args.length));
    	} else {
    		//logInfo("Command Not Found");
    		printUsage(sender);
    	}

    	return true;
    }
    

    /**
     * Print usage of all commands
     * 
     * @param sender
     */
    public void printUsage(CommandSender sender){
    	for (NerdPlotCommand cmd : plotCommands.values()) {
    		cmd.printUsage(sender);
    	}
    	sender.sendMessage(ChatColor.GREEN + "/" + cmdName + " help");
    }


    /**
     * Set the maximum number of plots a player can have
     * 
     */
    public boolean setMaxPlots(String worldName, String areaName, int maxPlots) {
    	String path = String.format("areas.%s.%s", worldName, areaName);
    	if(this.getConfig().contains(path)) {
    		ConfigurationSection area = this.getConfig().getConfigurationSection(path);
    		area.set("maxPlots", maxPlots);
    		return true;
    	} else {
    		return false;
    	}
    }
    
    
    /**
     * Get the maximum number of plots for an area per player
     */
    public int getMaxPlots(String worldName, String areaName) {
    	String path = String.format("areas.%s.%s.maxPlots", worldName, areaName);
    	return this.getConfig().getInt(path, -1);
    }
    
    
    /**
     * Create a new plot area
     * 
     * @return Return true of a new area was created, otherwise, return fasle
     */
    public boolean createArea(String worldName, String areaName) {
    	String path = String.format("areas.%s.%s.maxPlots", worldName, areaName);
    	boolean exists = this.getConfig().contains(path);
    	if (exists) {
    		return false;
    	}    	
    	ConfigurationSection areas = Util.getOrCreateSection(this.getConfig(), "areas");
    	ConfigurationSection world = Util.getOrCreateSection(areas, worldName);
    	ConfigurationSection area = Util.getOrCreateSection(world, areaName);
    	area.set("maxPlots", -1);
    	return true;
    }
    
    
    /**
     * Remove a plot area
     * 
     * @return Returns true of the area was removed
     */
    public boolean removeArea(String worldName, String areaName) {
    	String path = String.format("areas.%s.%s", worldName, areaName);
    	if (this.getConfig().contains(path)) {
    		this.getConfig().set(path, null);
    		return true;
    	} else {
    		return false;
    	}
    }
    
    
    /**
     * Check to see if an area exists
     */
    public boolean isArea(String worldName, String areaName) {
    	String path = String.format("areas.%s.%s", worldName, areaName);
    	return this.getConfig().contains(path);
    }
    
    
    /**
     * Get a list of all the maxPlot data from the DB
     */
    public List<MaxInfo> getMaxPlotInfo() {
    	List<MaxInfo> list = new ArrayList<MaxInfo>();
    	ConfigurationSection worlds = this.getConfig().getConfigurationSection("areas");
    	if (worlds == null) {
    		return list;
    	}
    	for (String worldName : worlds.getKeys(false)) {
    		ConfigurationSection world = worlds.getConfigurationSection(worldName);
	    	for (String areaName : world.getKeys(false)) {
	    		ConfigurationSection area = world.getConfigurationSection(areaName);
	    		list.add(new MaxInfo(worldName, areaName, area.getInt("maxPlots")));
	    	}
    	}
    	return list;
    }

    
    /**
     * Add a new plot to the plot database
     * 
     */
    public void addPlot(String worldName, String plotName, String areaName) {
    	ConfigurationSection plots = Util.getOrCreateSection(this.getConfig(), "plots");
    	ConfigurationSection world = Util.getOrCreateSection(plots, worldName);
    	ConfigurationSection plot = Util.getOrCreateSection(world, plotName);
    	plot.set("areaName", areaName);
    	plot.set("dateCreated", dateFormat.format(new Date()));
    }

    
    /**
     * Remove a plot from the plot database
     * 
     */
    public void removePlot(String worldName, String plotName) {
    	String path = String.format("plots.%s.%s", worldName, plotName);
    	if (this.getConfig().contains(path)) {
    		this.getConfig().set(path, null);
    	}
    }
    
    
    /**
     * Check to see if the requested name is in the plot database
     * 
     * Plots are located at
     * 
     * worlds.WORLD.AREA.plotname
     * 
     */
    public boolean isPlot(String worldName, String plotName) {
    	String path = String.format("plots.%s.%s", worldName, plotName);
    	return this.getConfig().contains(path);
    }
    
    
    /**
     * Set the owner of a plot in the database.
     * Set the owner to null to make the plot /claim-able
     * 
     */
    public void setPlotOwner(String worldName, String plotName, Player player) {
    	String path = String.format("plots.%s.%s", worldName, plotName);
    	ConfigurationSection plot = this.getConfig().getConfigurationSection(path);
    	if (plot == null) {
    		throw new IllegalArgumentException("worldName and plotName do not specifiy a plot");
    	}
    	if (player == null) {
	    	plot.set("ownerName", null);
	    	plot.set("ownerID", null);
	    	plot.set("dateClaimed", null);
    	} else {
	    	plot.set("ownerName", player.getName());
	    	plot.set("ownerID", player.getUniqueId().toString());
	    	plot.set("dateClaimed", dateFormat.format(new Date()));
    	}
    }

  
    /**
     * Given a player, world, and area tuple, count the number of plots the player has
     * of this type
     * 
     * @return
     */
    public int countPlayerAreaPlots(String worldName, UUID playerID, String areaName) {
    	String path = String.format("plots.%s", worldName);
    	ConfigurationSection plots = this.getConfig().getConfigurationSection(path);
    	if (plots == null) {
    		return 0;
    	}

    	int count = 0;
    	String id = playerID.toString();
    	for (String plotName : plots.getKeys(false)) {
    		ConfigurationSection plot = plots.getConfigurationSection(plotName);
    		if (id.equals(plot.getString("ownerID")) && areaName.equals(plot.getString("areaName"))) {
    			count++;
    		}
    	}
    	return count;
    }
    
    
    /**
     * Get information about a plot
     */
    public PlotInfo getPlotInfo(String worldName, String plotName) {
    	String path = String.format("plots.%s.%s", worldName, plotName);
    	if (this.getConfig().contains(path)) {
	    	ConfigurationSection plot = this.getConfig().getConfigurationSection(path);
	    	return new PlotInfo(
	    			worldName,
	    			plotName,
	    			plot.getString("areaName"),
	    			plot.getString("ownerName"),
	    			plot.getString("ownerID"),
	    			plot.getString("dateCreated"),
	    			plot.getString("dateClaimed")
	    			);
    	} else {
    		return null;
    	}
    }

    
    /**
     * List all the plots a player has.
     * 
     * This command has to check all the plots to generate the list
     * 
     */
    public List<PlotInfo> getAllOwnerPlots(UUID playerID) {
    	List<PlotInfo> list = new ArrayList<PlotInfo>();
    	if (playerID == null) {
    		return list;
    	}
    	String id = playerID.toString();
    	ConfigurationSection proot = this.getConfig().getConfigurationSection("plots");
    	for (String worldName : proot.getKeys(false)) {
    		ConfigurationSection world = proot.getConfigurationSection(worldName);
			for (String plotName : world.getKeys(false)) {
				ConfigurationSection plot = world.getConfigurationSection(plotName);
				String ownerName = plot.getString("ownerName");
				String ownerID = plot.getString("ownerID");
				if (ownerName == null || ownerID == null) {
					continue;
				}
				if (ownerID.equals(id)) {
					list.add(new PlotInfo(
							worldName,
							plotName,
							plot.getString("areaName"),
							ownerName,
							ownerID,
							plot.getString("dateCreated"),
							plot.getString("dateClaimed")
							));
				}
			}
    	}
    	return list;
    }
    
    
    /**
     * List all the plots a player has.
     * 
     * This command has to check all the plots to generate the list
     * 
     * TODO: This routine is slightly broken in that the most recent player name
     * is not used. If a player has changed their name, this routine won't work
     * correctly
     * 
     */
    public List<PlotInfo> getAllOwnerPlots(String playerName) {
    	List<PlotInfo> list = new ArrayList<PlotInfo>();
    	if (playerName == null) {
    		return list;
    	}
    	ConfigurationSection proot = this.getConfig().getConfigurationSection("plots");
    	for (String worldName : proot.getKeys(false)) {
    		ConfigurationSection world = proot.getConfigurationSection(worldName);
			for (String plotName : world.getKeys(false)) {
				ConfigurationSection plot = world.getConfigurationSection(plotName);
				String ownerName = plot.getString("ownerName");
				String ownerID = plot.getString("ownerID");
				if (ownerName == null || ownerID == null) {
					continue;
				}
				if (ownerID.equalsIgnoreCase(playerName)) {
					list.add(new PlotInfo(
							worldName,
							plotName,
							plot.getString("areaName"),
							ownerName,
							ownerID,
							plot.getString("dateCreated"),
							plot.getString("dateClaimed")
							));
				}
			}
    	}
    	return list;
    }
 
    /**
     * Clean the worldPlots database.
     * 
     * Check for plots that do not have WG regions
     *   We fix by removing the plot.
     * 
     * Check for plots with owners that are not in WG
     *   We fix by removing the player from the plot.
     *   
     */
//    public void cleanupDatabase(CommandSender sender) {
//    	ConfigurationSection worlds = this.getConfig().getConfigurationSection("worlds");
//    	for (String worldName : worlds.getKeys(false)) {
//    		
//            // Check out each world. Each world must exist
//    		ConfigurationSection world = worlds.getConfigurationSection(worldName);
//    		World bukkitWorld = this.getServer().getWorld(worldName);
//    		if (bukkitWorld == null) {
//    			if (sender != null) {
//    				sender.sendMessage(ChatColor.RED + "Removing world: " + worldName);
//    			}
//    			worlds.set(worldName, null);
//    			continue;
//    		}
//    		
//    		// Get the WG manager
//    		RegionManager manager = wg.getRegionManager(bukkitWorld);
//    		if (manager == null) {
//    			if (sender != null) {
//    				sender.sendMessage(ChatColor.RED + "No World Guard manager for " + worldName + ": skipping ...");
//    			}
//    			continue;
//    		}
//    		
//    		// Enumerate every area and plot in this world
//    		for (String areaName : world.getKeys(false)) {
//    			ConfigurationSection area = world.getConfigurationSection(areaName);
//    			for (String plotName : area.getKeys(false)) {
//    				ConfigurationSection plot = area.getConfigurationSection(plotName);
//    				String ownerID = plot.getString("ownerID");
//    				
//    				// Attempt to get the WG plot
//    				ProtectedRegion rg = manager.getRegion(plotName);
//    				if (rg == null) {
//    					// The region does not exist in WG. Remove the plot
//    					area.set(plotName, null);
//    					if (sender != null) {
//    						sender.sendMessage(ChatColor.RED + "Removed plot " + worldName + ":" + areaName + ":" + plot);
//    					}
//    					continue;
//    				}
//    				
//    				// If this plot does not have an owner, we are set
//    				if (ownerID == null) {
//    					continue;
//    				}
//
//    				// This plot has an owner. See if it is in WG
//    				UUID ownerUUID = UUID.fromString(ownerID);
//    				if(!rg.getOwners().contains(ownerUUID)) {
//						// This region has a different or no owner. Remove the old one from the plotdb
//						plot.set("ownerID", null);
//						plot.set("dateClaimed", null);
//						if(sender != null) {
//							Player p = this.getServer().getPlayer(ownerUUID);
//							String ownerName;
//							if (p == null) {
//								ownerName = "<unknown>";
//							} else {
//								ownerName = p.getName();
//							}
//							sender.sendMessage(ChatColor.RED + "Removed owner \"" + ownerName + "\" from plot " + worldName + ":" + areaName + ":" + plot);
//						}
//    				}
//    			}
//    		}
//    	}
//    }


    public String getCmdName() {
    	return cmdName;
    }
    
    
    public Logger getLog() {
    	return log;
    }

    
    public WorldGuardPlugin getWG() {
    	return wg;
    }
    
    
    public WorldEditPlugin getWE() {
    	return we;
    }
    
    
    public void logInfo(String msg) {
    	log.info("[" + cmdName + "] " + msg);
    }
    
    
    public void logWarning(String msg) {
    	log.warning("[" + cmdName + "] " + msg);
    }
    
    
    public void logSevere(String msg) {
    	log.severe("[" + cmdName + "] " + msg);
    }

}
 
