package com.jonahseguin.drink.internal;

import com.jonahseguin.drink.command.DrinkCommandContainer;
import org.bukkit.command.CommandSender;

public interface HelpFormatter {

    void sendHelpFor(CommandSender sender, DrinkCommandContainer container);
}
