package com.jonahseguin.drink.provider;

import com.jonahseguin.drink.command.CommandArgs;
import com.jonahseguin.drink.exception.CommandExitMessage;
import com.jonahseguin.drink.parametric.DrinkProvider;

import javax.annotation.Nonnull;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class StringProvider extends DrinkProvider<String> {

    public static final StringProvider INSTANCE = new StringProvider();

    @Override
    public boolean doesConsumeArgument() {
        return true;
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    public Optional<String> provide(@Nonnull CommandArgs args, @Nonnull List<? extends Annotation> annotations) throws CommandExitMessage {
        return Optional.of(args.next());
    }

    @Override
    public String argumentDescription() {
        return "string";
    }

    @Override
    public List<String> getSuggestions(@Nonnull String prefix) {
        return Collections.emptyList();
    }
}
