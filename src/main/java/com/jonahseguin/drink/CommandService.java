package com.jonahseguin.drink;

import com.jonahseguin.drink.command.DrinkCommandContainer;
import com.jonahseguin.drink.internal.DrinkAuthorizer;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public interface CommandService {

    DrinkCommandContainer register(@Nonnull Object handler, @Nonnull String name, @Nullable String... aliases);

    DrinkCommandContainer registerSub(@Nonnull String root, @Nonnull Object handler);

    void registerCommands();

    @Nullable
    DrinkCommandContainer get(@Nonnull String name);

    void setAuthorizer(@Nonnull DrinkAuthorizer authorizer);

}
