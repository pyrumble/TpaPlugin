package dev.rumble.utils;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class UsefulMethods {
    public static boolean isNotPlayer(CommandSender  sender){
        return !(sender instanceof Player);
    }
}