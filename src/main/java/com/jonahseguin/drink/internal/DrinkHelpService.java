package com.jonahseguin.drink.internal;

import com.jonahseguin.drink.command.DrinkCommand;
import com.jonahseguin.drink.command.DrinkCommandContainer;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class DrinkHelpService {

    private final DrinkCommandService commandService;

    public DrinkHelpService(DrinkCommandService commandService) {
        this.commandService = commandService;
    }

    public void sendHelpFor(CommandSender sender, DrinkCommandContainer container) {

    }

    public void sendUsageMessage(CommandSender sender, DrinkCommandContainer container, DrinkCommand command) {
        if (command.getUsage() != null && command.getUsage().length() > 0) {
            String usage = ChatColor.RED + "Command Usage: /" + container.getName() + " ";
            if (command.getName().length() > 0) {
                usage += command.getName() + " ";
            }
            usage += command.getUsage();
            sender.sendMessage(usage);
        }
        else {
            sender.sendMessage(ChatColor.RED + "Not enough arguments.  Required: " + command.getConsumingArgCount());
        }
    }

}
