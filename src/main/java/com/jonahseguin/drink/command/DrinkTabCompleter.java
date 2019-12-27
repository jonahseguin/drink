package com.jonahseguin.drink.command;

import com.jonahseguin.drink.internal.DrinkCommandService;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.Collections;
import java.util.List;
import java.util.Map;

public class DrinkTabCompleter implements TabCompleter {

    private final DrinkCommandService commandService;
    private final DrinkCommandContainer container;

    public DrinkTabCompleter(DrinkCommandService commandService, DrinkCommandContainer container) {
        this.commandService = commandService;
        this.container = container;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equalsIgnoreCase(container.getName())) {
            Map.Entry<DrinkCommand, String[]> data = container.getCommand(args);
            if (data != null && data.getKey() != null) {
                String tabCompleting = "";
                if (data.getValue().length > 0) {
                    tabCompleting = data.getValue()[data.getValue().length - 1];
                }
                int tabCompletingIndex = data.getValue().length - 1;
                DrinkCommand drinkCommand = data.getKey();
                if (drinkCommand.getConsumingProviders().length >= (tabCompletingIndex + 1)) {
                    return drinkCommand.getConsumingProviders()[tabCompletingIndex].getSuggestions(tabCompleting);
                }
            }
        }
        return Collections.emptyList();
    }
}
