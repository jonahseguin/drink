package com.jonahseguin.drink.internal;

import com.jonahseguin.drink.command.DrinkCommand;
import com.jonahseguin.drink.command.DrinkCommandContainer;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.Map;

public class DrinkCommandExecutor implements CommandExecutor {

    private final DrinkCommandService commandService;
    private final DrinkCommandContainer container;

    public DrinkCommandExecutor(DrinkCommandService commandService, DrinkCommandContainer container) {
        this.commandService = commandService;
        this.container = container;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase(container.getName())) {
            try {
                Map.Entry<DrinkCommand, String[]> data = container.getCommand(args);
                if (data != null && data.getKey() != null) {
                    commandService.executeCommand(sender, data.getKey(), data.getValue());
                } else {
                    if (args.length > 0) {
                        if (args[0].equalsIgnoreCase("help")) {
                            commandService.getHelpService().sendHelpFor(sender, container);
                        } else {
                            sender.sendMessage(ChatColor.RED + "Unknown sub-command: " + args[0] + ".  Use '/" + label + " help' for available commands.");
                        }
                    } else {
                        sender.sendMessage(ChatColor.RED + "Please choose a sub-command.  Use '/" + label + " help' for available commands.");
                    }
                }
                return true;
            }
            catch (Exception ex) {
                sender.sendMessage(ChatColor.RED + "An exception occurred while performing this command.");
                ex.printStackTrace();
            }
        }
        return false;
    }
}
