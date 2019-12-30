package com.jonahseguin.drink.command;

import com.jonahseguin.drink.exception.CommandArgumentException;

import javax.annotation.Nonnull;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class FlagExtractor {

    private final DrinkCommandService commandService;

    public FlagExtractor(DrinkCommandService commandService) {
        this.commandService = commandService;
    }

    public Map<Character, CommandFlag> extractFlags(List<String> args) throws CommandArgumentException {
        Map<Character, CommandFlag> flags = new HashMap<>();
        Iterator<String> it = args.iterator();
        Character currentFlag = null;
        while (it.hasNext()) {
            String arg = it.next();
            if (currentFlag != null) {
                if (!isFlag(arg)) {
                    // Value flag
                    flags.put(currentFlag, new CommandFlag(currentFlag, arg));
                    currentFlag = null;
                } else {
                    // Boolean flag
                    flags.put(currentFlag, new CommandFlag(currentFlag, null));
                    currentFlag = null;
                }
            } else {
                if (isFlag(arg)) {
                    char f = getFlag(arg);
                    if (!flags.containsKey(f)) {
                        currentFlag = f;
                        if (!it.hasNext()) {
                            // Boolean flag
                            flags.put(currentFlag, new CommandFlag(currentFlag, null));
                            currentFlag = null;
                        }
                    } else {
                        throw new CommandArgumentException("The flag '-" + f + "' has already been provided in this command.");
                    }
                    it.remove();
                }
            }
        }
        return flags;
    }

    private char getFlag(@Nonnull String arg) {
        return arg.charAt(1);
    }

    private boolean isFlag(@Nonnull String arg) {
        return arg.length() == 2 && arg.charAt(0) == CommandFlag.FLAG_PREFIX;
    }

}
