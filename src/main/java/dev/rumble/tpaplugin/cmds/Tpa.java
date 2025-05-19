package dev.rumble.tpaplugin.cmds;

import dev.rumble.tpaplugin.TpaPlugin;
import dev.rumble.utils.ColoredMsg;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;


public class Tpa implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {
        if (!(sender instanceof Player)) {
            ColoredMsg.sendToConsole(TpaPlugin.prefix + "&cNo puedes usar este comando desde la consola!");
            return true;
        }
        if (args.length == 1) {
            Player to = Bukkit.getPlayer(args[0]);
            if (to == null) {
                sender.sendMessage("El jugador está desconectado o no existe");
                return true;
            }
            if (to == sender) {
                sender.sendMessage("No puedes mandarte tpa a ti mismo.");
                return true;
            } else {
                if(TpaPlugin.tpaRequests.get(to.getUniqueId()) == null){
                    TpaPlugin.tpaRequests.put(to.getUniqueId(),((Player) sender).getUniqueId());
                    ColoredMsg.sendToPlayer(to,"Hey, " + "&e" +to.getName() + "\n"
                            +"&6" + sender.getName() + " &fte envió una solicitud para teletransportarse hacia ti, ¿Qué harás con la solicitud?");
                    TextComponent space = new TextComponent(" ");
                    TextComponent accept = new TextComponent(ChatColor.translateAlternateColorCodes('&', "&a[La acepto]"));
                    accept.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpaaccept"));
                    TextComponent deny = new TextComponent(ChatColor.translateAlternateColorCodes('&', "&c[La rechazo]"));
                    deny.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/tpadeny"));
                    BaseComponent[] components = {accept,space,deny};
                    to.spigot().sendMessage(components);
                    ColoredMsg.sendToPlayer(((Player) sender), TpaPlugin.prefix + "&aHas enviado una solicitud de tp exitosamente a " + to.getName());
                }else ColoredMsg.sendToPlayer(((Player) sender),  TpaPlugin.prefix +"&cEste jugador ya tiene una solicitud de tp!");
            }
        } else ColoredMsg.sendToPlayer(((Player) sender),"&cUso correcto: /tpa <jugador>");
        return true;
        }

    }
