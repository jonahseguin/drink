package com.jonahseguin.drink.command;

import com.google.common.base.Preconditions;
import com.google.common.base.Supplier;
import com.jonahseguin.drink.exception.CommandRegistrationException;
import com.jonahseguin.drink.internal.DrinkCommandExecutor;
import com.jonahseguin.drink.internal.DrinkCommandService;
import lombok.Getter;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.PluginIdentifiableCommand;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

@Getter
public class DrinkCommandContainer extends Command implements PluginIdentifiableCommand {

    private final DrinkCommandService commandService;
    private final Object object;
    private final String name;
    private final Set<String> aliases;
    private final Map<String, DrinkCommand> commands;
    private final DrinkCommand defaultCommand;
    private final DrinkCommandExecutor executor;
    private final DrinkTabCompleter tabCompleter;

    public DrinkCommandContainer(DrinkCommandService commandService, Object object, String name, Set<String> aliases, Map<String, DrinkCommand> commands) {
        super(name, "", "/" + name, new ArrayList<>(aliases));
        this.commandService = commandService;
        this.object = object;
        this.name = name;
        this.aliases = aliases;
        this.commands = commands;
        this.defaultCommand = calculateDefaultCommand();
        this.executor = new DrinkCommandExecutor(commandService, this);
        this.tabCompleter = new DrinkTabCompleter(commandService, this);
    }

    private DrinkCommand calculateDefaultCommand() {
        for (DrinkCommand dc : commands.values()) {
            if (dc.getName().length() == 0) {
                // assume default!
                return dc;
            }
        }
        return commands.values().stream().findFirst().orElseThrow((Supplier<CommandRegistrationException>) () -> new CommandRegistrationException("No default command found for command " + name));
    }

    public DrinkCommand get(@Nonnull String name) {
        Preconditions.checkNotNull(name, "Name cannot be null");
        return commands.get(commandService.getCommandKey(name));
    }

    @Nullable
    public Map.Entry<DrinkCommand, String[]> getCommand(String[] args) {
        for (int i = 0; i < args.length; i++) {
            String s = args[i];
            String key = commandService.getCommandKey(s);
            if (commands.containsKey(key)) {
                return new AbstractMap.SimpleEntry<>(commands.get(key), Arrays.copyOfRange(args, 1, args.length));
            }
        }
        return new AbstractMap.SimpleEntry<>(getDefaultCommand(), args);
    }

    @Nullable
    public DrinkCommand getDefaultCommand() {
        return commands.get(DrinkCommandService.DEFAULT_KEY);
    }

    @Override
    public boolean execute(CommandSender commandSender, String s, String[] strings) {
        return executor.onCommand(commandSender, this, s, strings);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args) throws IllegalArgumentException {
        return tabCompleter.onTabComplete(sender, this, alias, args);
    }

    @Override
    public List<String> tabComplete(CommandSender sender, String alias, String[] args, Location location) throws IllegalArgumentException {
        return tabCompleter.onTabComplete(sender, this, alias, args);
    }

    @Override
    public Plugin getPlugin() {
        return commandService.getPlugin();
    }
}
