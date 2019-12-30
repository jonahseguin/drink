package com.jonahseguin.drink.provider;

import com.google.common.collect.ImmutableList;
import com.jonahseguin.drink.command.CommandArgs;
import com.jonahseguin.drink.exception.CommandExitMessage;
import com.jonahseguin.drink.parametric.DrinkProvider;

import javax.annotation.Nonnull;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class BooleanProvider extends DrinkProvider<Boolean> {

    public static final BooleanProvider INSTANCE = new BooleanProvider();

    private static final List<String> SUGGEST = ImmutableList.of("true", "false");
    private static final List<String> SUGGEST_TRUE = ImmutableList.of("true");
    private static final List<String> SUGGEST_FALSE = ImmutableList.of("false");

    @Override
    public boolean doesConsumeArgument() {
        return true;
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    public Optional<Boolean> provide(@Nonnull CommandArgs args, @Nonnull List<? extends Annotation> annotations) throws CommandExitMessage {
        String s = args.next();
        try {
            Boolean i = Boolean.parseBoolean(s);
            return Optional.of(i);
        }
        catch (NumberFormatException ex) {
            throw new CommandExitMessage("Required: Boolean (true/false), Given: '" + s + "'");
        }
    }

    @Override
    public String argumentDescription() {
        return "true/false";
    }

    @Override
    public List<String> getSuggestions(@Nonnull String prefix) {
        prefix = prefix.toLowerCase();
        if (prefix.length() == 0) {
            return SUGGEST;
        }
        if ("true".startsWith(prefix)) {
            return SUGGEST_TRUE;
        }
        else if ("false".startsWith(prefix)) {
            return SUGGEST_FALSE;
        }
        else {
            return Collections.emptyList();
        }
    }
}
