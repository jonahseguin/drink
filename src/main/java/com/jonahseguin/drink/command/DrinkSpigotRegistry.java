package com.jonahseguin.drink.command;

import com.jonahseguin.drink.exception.CommandRegistrationException;
import com.jonahseguin.drink.internal.DrinkCommandService;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandMap;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;

public class DrinkSpigotRegistry {

    private final DrinkCommandService commandService;
    private CommandMap commandMap;

    public DrinkSpigotRegistry(DrinkCommandService commandService) {
        this.commandService = commandService;
        try {
            Field field = Bukkit.getServer().getClass().getDeclaredField("commandMap");
            field.setAccessible(true);
            commandMap = (CommandMap)field.get(Bukkit.getServer());
        }
        catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    private Map<String, Command> getKnownCommands() throws NoSuchFieldException, IllegalAccessException {
        Object map = getPrivateField(commandMap, "knownCommands");
        @SuppressWarnings("unchecked")
        HashMap<String, Command> knownCommands = (HashMap<String, Command>) map;
        return knownCommands;
    }

    private Object getPrivateField(Object object, String field) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        final String v = Bukkit.getVersion();
        Class<?> clazz = object.getClass();
        Field objectField = field.equals("commandMap") ? clazz.getDeclaredField(field) : field.equals("knownCommands") ? v.contains("1.13.1") ? clazz.getSuperclass().getDeclaredField(field) : clazz.getDeclaredField(field) : null;
        objectField.setAccessible(true);
        Object result = objectField.get(object);
        objectField.setAccessible(false);
        return result;
    }


    public boolean register(@Nonnull DrinkCommandContainer container, boolean unregisterExisting) throws CommandRegistrationException {
        if (unregisterExisting) {
            try {
                Map<String, Command> knownCommands = getKnownCommands();
                if (knownCommands.containsKey(container.getName().toLowerCase())) {
                    knownCommands.remove(container.getName().toLowerCase()).unregister(commandMap);
                }
                for (String s : container.getDrinkAliases()) {
                    if (knownCommands.containsKey(s.toLowerCase())) {
                        knownCommands.remove(s).unregister(commandMap);
                    }
                }
            } catch (IllegalAccessException | NoSuchFieldException e) {
                throw new CommandRegistrationException("Couldn't access knownCommands field in Bukkit CommandMap to unregister existing command(s)");
            }
        }

        return commandMap.register(container.getCommandService().getPlugin().getName(), container);
    }



}
