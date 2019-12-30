package com.jonahseguin.drink.provider;

import com.jonahseguin.drink.command.CommandArgs;
import com.jonahseguin.drink.exception.CommandExitMessage;
import com.jonahseguin.drink.parametric.DrinkProvider;

import javax.annotation.Nonnull;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class LongProvider extends DrinkProvider<Long> {

    public static final LongProvider INSTANCE = new LongProvider();

    @Override
    public boolean doesConsumeArgument() {
        return true;
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    public Optional<Long> provide(@Nonnull CommandArgs args, @Nonnull List<? extends Annotation> annotations) throws CommandExitMessage {
        String s = args.next();
        try {
            Long i = Long.parseLong(s);
            return Optional.of(i);
        }
        catch (NumberFormatException ex) {
            throw new CommandExitMessage("Required: Long Number, Given: '" + s + "'");
        }
    }

    @Override
    public String argumentDescription() {
        return "long number";
    }

    @Override
    public List<String> getSuggestions(@Nonnull String prefix) {
        return Collections.emptyList();
    }
}
