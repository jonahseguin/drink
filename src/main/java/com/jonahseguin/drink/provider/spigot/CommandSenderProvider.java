package com.jonahseguin.drink.provider.spigot;

import com.jonahseguin.drink.command.CommandArgs;
import com.jonahseguin.drink.exception.CommandExitMessage;
import com.jonahseguin.drink.parametric.DrinkProvider;
import org.bukkit.command.CommandSender;

import javax.annotation.Nonnull;
import java.lang.annotation.Annotation;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class CommandSenderProvider extends DrinkProvider<CommandSender> {

    public static final CommandSenderProvider INSTANCE = new CommandSenderProvider();

    @Override
    public boolean doesConsumeArgument() {
        return false;
    }

    @Override
    public boolean isAsync() {
        return false;
    }

    @Override
    public Optional<CommandSender> provide(@Nonnull CommandArgs args, @Nonnull List<? extends Annotation> annotations) throws CommandExitMessage {
        return Optional.of(args.getSender());
    }

    @Override
    public String argumentDescription() {
        return "sender";
    }

    @Override
    public List<String> getSuggestions(@Nonnull String prefix) {
        return Collections.emptyList();
    }
}
