package com.jonahseguin.drink.command;

import com.google.common.base.Preconditions;
import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.Iterator;
import java.util.List;

@Getter
public class CommandArgs {

    private final CommandSender sender;
    private final List<String> args;
    private final Iterator<String> iterator;

    public CommandArgs(@Nonnull CommandSender sender, @Nonnull List<String> args) {
        Preconditions.checkNotNull(sender, "CommandSender cannot be null");
        Preconditions.checkNotNull(args, "Command args cannot be null");
        this.sender = sender;
        this.args = args;
        this.iterator = args.iterator();
    }

    public CommandArgs clone() {
        return new CommandArgs(sender, args);
    }

    public boolean hasNext() {
        return iterator.hasNext();
    }

    public String next() {
        return iterator.next();
    }

    public boolean isSenderPlayer() {
        return sender instanceof Player;
    }

    public Player getSenderAsPlayer() {
        return (Player) sender;
    }



}
