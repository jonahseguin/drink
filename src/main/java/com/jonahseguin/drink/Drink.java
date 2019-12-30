package com.jonahseguin.drink;

import com.jonahseguin.drink.internal.DrinkCommandService;
import org.bukkit.plugin.java.JavaPlugin;

import javax.annotation.Nonnull;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public class Drink {

    private static final ConcurrentMap<String, CommandService> services = new ConcurrentHashMap<>();

    public static CommandService get(@Nonnull JavaPlugin javaPlugin) {
        return services.computeIfAbsent(javaPlugin.getName(), name -> new DrinkCommandService(javaPlugin));
    }

}
