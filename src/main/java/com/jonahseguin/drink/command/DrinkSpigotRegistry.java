package com.jonahseguin.drink.command;

import com.jonahseguin.drink.internal.DrinkCommandService;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandMap;

import javax.annotation.Nonnull;
import java.lang.reflect.Field;

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

    public void register(@Nonnull DrinkCommandContainer container) {
        commandMap.register(container.getCommandService().getPlugin().getName(), container);
    }



}
