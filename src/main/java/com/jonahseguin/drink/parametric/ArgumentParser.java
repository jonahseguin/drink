package com.jonahseguin.drink.parametric;

import com.google.common.base.Preconditions;
import com.jonahseguin.drink.command.CommandArgs;
import com.jonahseguin.drink.command.CommandExecution;
import com.jonahseguin.drink.command.DrinkCommand;
import com.jonahseguin.drink.command.DrinkCommandService;
import com.jonahseguin.drink.exception.CommandArgumentException;
import com.jonahseguin.drink.exception.CommandExitMessage;

import javax.annotation.Nonnull;
import java.util.Optional;

public class ArgumentParser {

    private final DrinkCommandService commandService;

    public ArgumentParser(DrinkCommandService commandService) {
        this.commandService = commandService;
    }

    @Nonnull
    public Object[] parseArguments(@Nonnull CommandExecution execution, @Nonnull DrinkCommand command, @Nonnull CommandArgs args) throws CommandExitMessage, CommandArgumentException {
        Preconditions.checkNotNull(command, "DrinkCommand cannot be null");
        Preconditions.checkNotNull(args, "CommandArgs cannot be null");
        Object[] arguments = new Object[command.getMethod().getParameterCount()];
        for (int i = 0; i < command.getParameters().getParameters().length; i++) {
            CommandParameter param = command.getParameters().getParameters()[i];
            boolean skipOptional = false; // dont complete exceptionally if true if the arg is missing

            DrinkProvider<?> provider = command.getProviders()[i];

            if (!args.hasNext()) {
                if (provider.doesConsumeArgument()) {
                    if (param.isOptional()) {
                        String defaultValue = param.getDefaultOptionalValue();
                        if (defaultValue != null && defaultValue.length() > 0) {
                            args.getLock().lock();
                            try {
                                args.getArgs().add(defaultValue);
                            }
                            finally {
                                args.getLock().unlock();
                            }
                        } else {
                            skipOptional = true;
                        }
                    } else {
                        throw new CommandArgumentException("Missing argument for: " + provider.argumentDescription());
                    }
                }
            }

            if (!skipOptional) {
                if (provider.doesConsumeArgument() && !args.hasNext()) {
                    throw new CommandArgumentException("Argument already consumed for next argument: " + provider.argumentDescription() + " (this is a provider error!)");
                }
                Optional<?> o = provider.provide(args, param.getAllAnnotations());
                o = commandService.getModifierService().executeModifiers(execution, param, o.orElse(null));
                if (o.isPresent()) {
                    arguments[i] = o.get();
                } else {
                    throw new CommandArgumentException("Missing argument for: " + provider.argumentDescription());
                }
            }
            else {
                arguments[i] = null;
            }
        }
        return arguments;
    }

}
