package org.challenger2.NerdPlot;

import org.bukkit.plugin.java.JavaPlugin;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.Plugin;

public class NerdPlotPlugin extends JavaPlugin {

	private static final Logger log = Logger.getLogger("Minecraft");
	private static final String cmdName = "nerdplot";

	private WorldGuardPlugin wg;
	private WorldEditPlugin we;
	private Map<String, NerdPlotCommand> plotCommands;
	private Map<String, Map<String, String>> worldPlots;

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

		loadConfig();

		// Add commands
		plotCommands = new LinkedHashMap<String, NerdPlotCommand>();
		addCommand(new CmdClaim(this));
		addCommand(new CmdList(this));
		addCommand(new CmdMax(this));
		addCommand(new CmdCreate(this));
		addCommand(new CmdAddOwner(this));
		addCommand(new CmdRemoveOwner(this));
		addCommand(new CmdCleanup(this));
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
    	saveMyConfig();
    	worldPlots = null;
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
     * Load config from disk
     */
    public void loadConfig() {
    	this.saveDefaultConfig();
    	
    	worldPlots = new HashMap<String, Map<String,String>>();
    	ConfigurationSection worlds = this.getConfig().getConfigurationSection("worlds");
    	if (worlds != null) {
	    	for (String world : worlds.getKeys(false)) {
	    		ConfigurationSection plotSection = worlds.getConfigurationSection(world);
	    		if (plotSection != null) {
		    		Map<String, String> plotMap = new HashMap<String, String>();
		    		worldPlots.put(world, plotMap);
		    		for (String plot : plotSection.getKeys(false)) {
		    			String owner = plotSection.getString(plot);
		    			plotMap.put(plot, owner);
		    		}
	    		}
	    	}
    	}
    }

    /**
     * Save config to disk
     */
    public void saveMyConfig() {
    	
    	ConfigurationSection worlds = this.getConfig().createSection("worlds");
    	for (String world : worldPlots.keySet()) {
    		Map<String, String> plotMap = worldPlots.get(world);
    		ConfigurationSection plots = worlds.createSection(world);
    		for (String plot : plotMap.keySet()) {
    			plots.set(plot, plotMap.get(plot));
    		}
    	}
    	this.getConfig().set("Foo", "Bar");

    	this.saveConfig();
    }
    
    /**
     * Get the maximum number of plots a player can have
     * @return
     */
    public int getMaxPlots() {
    	int max = this.getConfig().getInt("maxPlots");
    	return max;
    }

    /**
     * Set the maximum number of plots a player can have
     * 
     * @param max
     */
    public void setMaxPlots(int max) {
    	this.getConfig().set("maxPlots", max);
    }
    
    /**
     * Add a new plot to the plot database
     * 
     * @param world
     * @param plot
     */
    public void addPlot(String world, String plot) {
    	if (!worldPlots.containsKey(world)) {
    		worldPlots.put(world,  new HashMap<String, String>());
    	}
    	worldPlots.get(world).put(plot, "");
    }

    /**
     * Remove a plot from the plot database
     * 
     * @param world
     * @param plot
     */
    public void removePlot(String world, String plot) {
    	Map<String, String> plotMap = worldPlots.get(world);
    	if (plotMap != null) {
    		plotMap.remove(plot);
    		if (plotMap.isEmpty()) {
    				worldPlots.remove(world);
    		}
    	}
    }
    
    /**
     * Check to see if the requested name is in the plot database
     * 
     * @param world
     * @param plot
     * @return
     */
    public boolean isPlot(String world, String plot) {
    	Map<String, String> plotMap = worldPlots.get(world);
    	if (plotMap != null) {
    		return plotMap.containsKey(plot);
    	}
    	return false;
    }
    
    /**
     * Set the owner of a plot in the database
     * 
     * @param world
     * @param plot
     * @param player
     */
    public void setPlotOwner(String world, String plot, String player) {
    	Map<String, String> plotMap = worldPlots.get(world);
    	if (plotMap != null) {
    		plotMap.put(plot, player);
    	}
    }
    
    /**
     * Remove an owner from the plot database
     * 
     * @param world
     * @param plot
     * @param player
     */
    public void removePlotOwner(String world, String plot) {
    	Map<String, String> plotMap = worldPlots.get(world);
    	if (plotMap != null) {
    		plotMap.put(plot, "");
    	}
    }

    public String getPlotOwner(String world, String plot) {
    	Map<String, String> plotMap = worldPlots.get(world);
    	if (plotMap == null) {
    		return null;
    	} else {
    		String owner = plotMap.get(plot);
    		if (owner == "") {
    			return null;
    		}
    		return owner;
    	}
    }
  
    /**
     * Return a list of all the plots a player has
     * 
     * This command has to check all the plots to generate the list
     * 
     * @param player
     * @return
     */
    public List<String> getAllPlayerPlots(String player) {
    	List<String> list = new LinkedList<String>();

    	for (Map<String, String> plotMap : worldPlots.values()) {
    		for (String plot : plotMap.keySet()) {
    			if (plotMap.get(plot).equalsIgnoreCase(player)) {
    				list.add(plot);
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
    public void cleanupDatabase(CommandSender sender) {
    	for (String worldName : worldPlots.keySet()) {
    		
    		// List of all plots and their owners for this world
    		Map<String, String> plotMap = worldPlots.get(worldName);
 
    		// Lookup the bukkit world with this name
    		List<World> worlds = this.getServer().getWorlds();
    		World world = null;
    		for (World w : worlds) {
    			if (w.getName().equalsIgnoreCase(worldName)) {
    				world = w;
    				break;
    			}
    		}
    		if (world == null) {
    			// World does not exist. Delete it
    			worldPlots.remove(worldName);
    		} else {
    			RegionManager manager = wg.getRegionManager(world);
    			
    			// At this point, we have a region manager and a world and a list of plots to check
    			Iterator<String> itr = plotMap.keySet().iterator();
    			while (itr.hasNext()) {
    				String plot = itr.next();
    				ProtectedRegion rg = manager.getRegion(plot);
    				if (rg == null) {
    					// The region does not exist in WG. Remove the plot
    					itr.remove();
    					if (sender != null) {
    						sender.sendMessage(ChatColor.RED + "Removed plot " + worldName + ":" + plot);
    					}
    				} else {
    					String ownerName = plotMap.get(plot);
    					if (ownerName.equals("")) {
    						// Ignore the empty string owner
    						continue;
    					}
    					if (!rg.getOwners().contains(ownerName)) {
    						// This region has a new owner. Remove the old one from the plotdb
    						plotMap.put(plot, "");
    						if(sender != null) {
    							sender.sendMessage(ChatColor.RED + "Removed owner \"" + ownerName + "\" from plot " + worldName + ":" + plot);
    						}
    					}
    				}
    			}
    			if (plotMap.isEmpty()) {
    				worldPlots.remove(worldName);
    			}
    		}
    	}
    }

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
 
