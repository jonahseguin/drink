package com.jonahseguin.drink.command;

import com.google.common.base.Preconditions;
import lombok.Getter;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.ReentrantLock;

@Getter
public class CommandArgs {

    private final CommandSender sender;
    private final ArrayList<String> args;
    private final ReentrantLock lock = new ReentrantLock();
    private int index = 0;

    public CommandArgs(@Nonnull CommandSender sender, @Nonnull List<String> args) {
        Preconditions.checkNotNull(sender, "CommandSender cannot be null");
        Preconditions.checkNotNull(args, "Command args cannot be null");
        this.sender = sender;
        this.args = new ArrayList<>(args);
    }

    public boolean hasNext() {
        lock.lock();
        try {
            return args.size() > index;
        }
        finally {
            lock.unlock();
        }
    }

    public String next() {
        lock.lock();
        try {
            return args.get(index++);
        }
        finally {
            lock.unlock();
        }
    }

    public boolean isSenderPlayer() {
        return sender instanceof Player;
    }

    public Player getSenderAsPlayer() {
        return (Player) sender;
    }



}
