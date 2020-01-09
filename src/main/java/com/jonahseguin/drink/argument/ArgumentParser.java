package com.jonahseguin.drink.argument;

import com.google.common.base.Preconditions;
import com.jonahseguin.drink.annotation.Flag;
import com.jonahseguin.drink.command.CommandExecution;
import com.jonahseguin.drink.command.CommandFlag;
import com.jonahseguin.drink.command.DrinkCommand;
import com.jonahseguin.drink.command.DrinkCommandService;
import com.jonahseguin.drink.exception.CommandArgumentException;
import com.jonahseguin.drink.exception.CommandExitMessage;
import com.jonahseguin.drink.parametric.CommandParameter;
import com.jonahseguin.drink.parametric.DrinkProvider;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class ArgumentParser {

    private final DrinkCommandService commandService;

    public ArgumentParser(DrinkCommandService commandService) {
        this.commandService = commandService;
    }

    public List<String> combineMultiWordArguments(List<String> args) {
        List<String> argList = new ArrayList<>(args.size());
        for (int i = 0; i < args.size(); i++) {
            String arg = args.get(i);
            if (!arg.isEmpty()) {
                final char c = arg.charAt(0);
                if (c == '"' || c == '\'') {
                    final StringBuilder builder = new StringBuilder();
                    int endIndex;
                    for (endIndex = i; endIndex < args.size(); endIndex++) {
                        final String arg2 = args.get(endIndex);
                        if (arg2.charAt(arg2.length() - 1) == c && arg2.length() > 1) {
                            if (endIndex != i) {
                                builder.append(' ');
                            }
                            builder.append(arg2.substring(endIndex == i ? 1 : 0, arg2.length() - 1));
                            break;
                        } else if (endIndex == i) {
                            builder.append(arg2.substring(1));
                        } else {
                            builder.append(' ').append(arg2);
                        }
                    }
                    if (endIndex < args.size()) {
                        arg = builder.toString();
                        i = endIndex;
                    }
                }
            }
            if (!arg.isEmpty()) {
                argList.add(arg);
            }
        }
        return argList;
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
            String value = null;

            if (param.isFlag()) {
                Flag flag = param.getFlag();
                CommandFlag commandFlag = args.getFlags().get(flag.value());
                if (commandFlag != null) {
                    value = commandFlag.getValue();
                } else {
                    value = null;
                }
            } else {
                if (!args.hasNext()) {
                    if (provider.doesConsumeArgument()) {
                        if (param.isOptional()) {
                            String defaultValue = param.getDefaultOptionalValue();
                            if (defaultValue != null && defaultValue.length() > 0) {
                                value = defaultValue;
                            } else {
                                skipOptional = true;
                            }
                        } else {
                            throw new CommandArgumentException("Missing argument for: " + provider.argumentDescription());
                        }
                    }
                }
                if (provider.doesConsumeArgument() && value == null && args.hasNext()) {
                    value = args.next();
                }
                if (provider.doesConsumeArgument() && value == null && !skipOptional) {
                    throw new CommandArgumentException("Argument already consumed for next argument: " + provider.argumentDescription() + " (this is a provider error!)");
                }
            }

            if (param.isFlag() && !param.getType().isAssignableFrom(Boolean.class) && !param.getType().isAssignableFrom(boolean.class)
                    && value == null && !provider.allowNullArgument()) {
                arguments[i] = provider.defaultNullValue();
            }
            else {
                if (!skipOptional) {
                    Object o = provider.provide(new CommandArg(args.getSender(), value, args), param.getAllAnnotations());
                    o = commandService.getModifierService().executeModifiers(execution, param, o);
                    arguments[i] = o;
                } else {
                    arguments[i] = null;
                }
            }
        }
        return arguments;
    }

}
