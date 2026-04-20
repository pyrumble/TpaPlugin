package dev.rumble.tpaplugin.cmds;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class TPP implements CommandExecutor {
    JavaPlugin plugin;
    public TPP(JavaPlugin plugin){
        this.plugin = plugin;
    }
    @Override
    public boolean onCommand(CommandSender sender, Command command, String s, String[] args) {
        if (!command.getName().equalsIgnoreCase("tpaplugin")) return false;

        if (args.length == 0) {
            sender.sendMessage(ChatColor.RED + "/tpaplugin <subcommand>");
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {

            if (!sender.hasPermission("tpaplugin.reload")) {
                sender.sendMessage(ChatColor.RED + "No permission.");
                return true;
            }

            plugin.reloadConfig();

            sender.sendMessage("§aSuccess.");
            return true;
        }
        return true;
    }
}
