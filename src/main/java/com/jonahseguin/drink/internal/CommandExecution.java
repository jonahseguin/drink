package com.jonahseguin.drink.internal;

import com.jonahseguin.drink.command.CommandArgs;
import com.jonahseguin.drink.command.DrinkCommand;
import lombok.Getter;
import org.bukkit.command.CommandSender;

@Getter
public class CommandExecution {

    private final DrinkCommandService commandService;
    private final CommandSender sender;
    private final String[] args;
    private final CommandArgs commandArgs;
    private final DrinkCommand command;
    private boolean canExecute = true;

    public CommandExecution(DrinkCommandService commandService, CommandSender sender, String[] args, CommandArgs commandArgs, DrinkCommand command) {
        this.commandService = commandService;
        this.sender = sender;
        this.args = args;
        this.commandArgs = commandArgs;
        this.command = command;
    }

    public void preventExecution() {
        canExecute = false;
    }

}
