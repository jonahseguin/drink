package com.jonahseguin.drink.parametric;

import com.google.common.base.Preconditions;
import com.jonahseguin.drink.command.CommandArgs;
import com.jonahseguin.drink.command.DrinkCommand;
import com.jonahseguin.drink.exception.CommandArgumentException;
import com.jonahseguin.drink.exception.CommandExitMessage;
import com.jonahseguin.drink.internal.CommandExecution;
import com.jonahseguin.drink.internal.DrinkCommandService;

import javax.annotation.Nonnull;
import java.util.Arrays;
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
        CommandArgs immutableArgs = args.clone();
        Object[] arguments = new Object[command.getMethod().getParameterCount()];
        for (int i = 0; i < command.getParameters().getParameters().length; i++) {
            CommandParameter param = command.getParameters().getParameters()[i];
            boolean skipOptional = false; // dont complete exceptionally if true if the arg is missing
            if (param.isOptional()) {
                String defaultValue = param.getDefaultOptionalValue();
                if (!args.hasNext()) {
                    if (defaultValue.length() > 0) {
                        args.getArgs().add(defaultValue);
                    }
                    else {
                        skipOptional = true;
                    }
                }
            }
            DrinkProvider<?> provider = command.getProviders()[i];
            Optional<?> o = provider.provide(provider.doesConsumeArgument() ? args : immutableArgs.clone(), Arrays.asList(param.getAllAnnotations()));
            o = commandService.getModifierService().executeModifiers(execution, param, o.orElse(null));
            if (o.isPresent()) {
                arguments[i] = o.get();
            }
            else {
                if (skipOptional) {
                    arguments[i] = null;
                }
                else {
                    throw new CommandArgumentException("Missing argument for: " + provider.argumentDescription());
                }
            }
        }
        return arguments;
    }

}
