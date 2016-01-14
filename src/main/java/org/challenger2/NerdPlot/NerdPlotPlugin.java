package org.challenger2.NerdPlot;

import org.bukkit.plugin.java.JavaPlugin;
import org.challenger2.NerdPlot.Commands.CmdClaim;
import org.challenger2.NerdPlot.Commands.CmdCreate;
import org.challenger2.NerdPlot.Commands.NerdPlotCommand;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;

import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import org.bukkit.ChatColor;
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

		// Add commands
		plotCommands = new LinkedHashMap<String, NerdPlotCommand>();

		CmdCreate commandCreate = new CmdCreate(this);
		plotCommands.put(commandCreate.getName(), commandCreate);

		CmdClaim commandClaim = new CmdClaim(this);
		plotCommands.put(commandClaim.getName(), commandClaim);

		loadConfig();
    }

    @Override
    public void onDisable() {
    	saveMyConfig();
    	worldPlots = null;
    	plotCommands = null;
    	wg = null;
    	we = null;
    }

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
    		PrintUsage(sender);
    		return true;
    	}
    	
    	String subCommand = args[0].toLowerCase();

    	if (plotCommands.containsKey(subCommand)) {
    		//logInfo("Command Found");
    		NerdPlotCommand cmd = plotCommands.get(subCommand);
    		if (sender.hasPermission(cmd.getPermission())) {
    			cmd.execute(sender, Arrays.copyOfRange(args, 1, args.length));
    		} else {
    			//logInfo("Permission denied");
    			PrintUsage(sender);
    		}
    	} else {
    		//logInfo("Command Not Found");
    		PrintUsage(sender);
    	}

    	return true;
    }

    public void PrintUsage(CommandSender sender){
    	for (NerdPlotCommand cmd : plotCommands.values()) {
    		if (sender.hasPermission(cmd.getPermission())) {
    			cmd.printUsage(sender);
    		}
    	}
    	sender.sendMessage(ChatColor.GREEN + "/" + cmdName + " help");
    }

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
    
    public void addPlot(String world, String plot) {
    	if (!worldPlots.containsKey(world)) {
    		worldPlots.put(world,  new HashMap<String, String>());
    	}
    	worldPlots.get(world).put(plot, "");
    }

    public void removePlot(String world, String plot) {
    	Map<String, String> plotMap = worldPlots.get(world);
    	if (plotMap != null) {
    		plotMap.remove(plot);
    		if (plotMap.isEmpty()) {
    				worldPlots.remove(world);
    		}
    	}
    }
    
    public boolean isPlot(String world, String plot) {
    	Map<String, String> plotMap = worldPlots.get(world);
    	if (plotMap != null) {
    		return plotMap.containsKey(plot);
    	}
    	return false;
    }
    
    public void setPlotOwner(String world, String plot, String player) {
    	Map<String, String> plotMap = worldPlots.get(world);
    	if (plotMap != null) {
    		plotMap.put(plot, player);
    	}
    }
    
    public void removePlotOwner(String world, String plot, String player) {
    	Map<String, String> plotMap = worldPlots.get(world);
    	if (plotMap != null) {
    		if (plotMap.get(plot).equalsIgnoreCase(player)) {
    			plotMap.put(plot, "");
    		}
    	}
    }
    
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
 
