package org.challenger2.NerdPlot;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;

public class CmdVersion extends NerdPlotCommand {

	private final String permission = "nerdplot.version";

	public CmdVersion(NerdPlotPlugin plugin) {
		super(plugin, "version");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
		if (!sender.hasPermission(permission)) {
			plugin.printUsage(sender);
			return;
		}
		
		PluginDescriptionFile desc = plugin.getDescription();
		sender.sendMessage(String.format("%sAuthor: %s, Version: %s",
				ChatColor.GREEN,
				desc.getAuthors(),
				desc.getVersion()
				));
		return;
	}

	@Override
	public void printUsage(CommandSender sender) {
		if(sender.hasPermission(permission)) {
			sender.sendMessage(ChatColor.GREEN + "/" + plugin.getName() + " " + this.name);
		}
	}

}
