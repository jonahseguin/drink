package com.jonahseguin.drink.command;

import com.jonahseguin.drink.exception.MissingProviderException;
import com.jonahseguin.drink.internal.DrinkCommandService;
import com.jonahseguin.drink.parametric.CommandParameters;
import com.jonahseguin.drink.parametric.DrinkProvider;
import lombok.Getter;

import java.lang.reflect.Method;
import java.util.Set;

@Getter
public class DrinkCommand {

    private final DrinkCommandService commandService;
    private final String name;
    private final Set<String> aliases;
    private final String description;
    private final String usage;
    private final String permission;
    private final Object handler;
    private final Method method;
    private final CommandParameters parameters;
    private final DrinkProvider<?>[] providers;
    private final DrinkProvider<?>[] consumingProviders;
    private final int consumingArgCount;
    private final boolean requiresAsync;

    public DrinkCommand(DrinkCommandService commandService, String name, Set<String> aliases, String description, String usage, String permission, Object handler, Method method) throws MissingProviderException {
        this.commandService = commandService;
        this.name = name;
        this.aliases = aliases;
        this.description = description;
        this.usage = usage;
        this.permission = permission;
        this.handler = handler;
        this.method = method;
        this.parameters = new CommandParameters(method);
        this.providers = commandService.getProviderAssigner().assignProvidersFor(this);
        this.consumingArgCount = calculateConsumingArgCount();
        this.consumingProviders = calculateConsumingProviders();
        this.requiresAsync = calculateRequiresAsync();
    }

    private boolean calculateRequiresAsync() {
        for (DrinkProvider<?> provider : providers) {
            if (provider.isAsync()) {
                return true;
            }
        }
        return false;
    }

    private DrinkProvider<?>[] calculateConsumingProviders() {
        DrinkProvider<?>[] consumingProviders = new DrinkProvider<?>[consumingArgCount];
        int x = 0;
        for (DrinkProvider<?> provider : providers) {
            if (provider.doesConsumeArgument()) {
                consumingProviders[x] = provider;
                x++;
            }
        }
        return consumingProviders;
    }

    private int calculateConsumingArgCount() {
        int count = 0;
        for (DrinkProvider<?> provider : providers) {
            if (provider.doesConsumeArgument()) {
                count++;
            }
        }
        return count;
    }

}
