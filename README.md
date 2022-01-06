# drink
Dependency Injection-based Command Library for Spigot plugins inspired by Google's Guice and sk89q's Intake.

## What is drink?
drink is a command library designed to remove the repetitive code involved with writing commands for Spigot plugins specifically.

drink takes an IoC (Inversion of Control) based approach using a very simple Dependency-Injection design pattern inspired by 
Google's Guice library and sk89q's Intake command library.

## Installing
You can either shade this repository into your plugin, or run it as a plugin by itself.

1. Clone this repository: `git clone git@github.com:jonahseguin/drink.git`
2. Enter the directory: `cd drink`
3. Build & install with Maven: `mvn clean package install`

Next, add drink to your project's dependencies via Maven

Add this to your `pom.xml` `<dependencies>`:
```xml
<dependency>
  <groupId>com.jonahseguin</groupId>
  <artifactId>drink</artifactId>
  <version>1.0.3</version> <!-- At time of writing, 1.0.3 is latest version.  See the pom.xml for the latest version -->
  <scope>compile</scope> <!-- Change scope to 'provided' if you are running drink as a plugin rather than shading it -->
</dependency>
```

## Using Drink
drink is designed to be simple and straightforward to use.

You do not need to register commands in your plugin.yml, as drink will dynamically add them into Bukkit's `CommandMap`.

### Acquire a `CommandService` instance for your plugin
The `CommandService` class is how you will be registering commands for your plugin.

```java
CommandService drink = Drink.get(this); // Assuming 'this' is your JavaPlugin instance
```

### Creating a command

Creating a command with drink is simple.  You can put a command in any class.  You don't have to extend or implement any classes.

Simply annotate a method with `@Command`:

```java
import com.jonahseguin.drink.annotation.Command;
import com.jonahseguin.drink.annotation.Sender;
import org.bukkit.command.CommandSender;

public class ExampleCommand {
    // The reason we are leaving the name blank is that we want this to be the default command.
    // So when we use /example, it will execute this.
    // A default command is optional.
    @Command(name = "", aliases = {}, desc = "An example command.", usage = "")
    @Require("example.use")
    public void exampleRoot(@Sender CommandSender sender) {
        sender.sendMessage("You used the example command!");
    }
    
    // Easy argument parsing
    
    @Command(name = "name", desc = "Set a player's display name.", usage = "<player> <name>")
    public void setName(@Sender CommandSender sender, Player target, String name) {
        target.setDisplayName(name);
    }
    
    // Text
    
    @Command(name = "msg", desc = "Send a message to a player.", usage = "<player> <message..>")
    public void msg(@Sender CommandSender sender, Player target, @Text String message) {
        target.sendMessage("Message from " + sender.getName() + ": " + message);
    }
    
    // Optional arguments
    
    @Command(name = "setlevel", desc = "Set your XP level.") // usage will be auto-generated to look like: [level = 1]
    @Require("example.setlevel")
    public void setLevel(@Sender Player player, @OptArg("1") int level) {
        player.setLevel(level);
    }
    
    // Flags
    
    @Command(name = "flagb", desc = "Test boolean flag.", usage = "[-f: flag]")
    public void flagBoolean(@Sender CommandSender sender, @Flag('f') boolean flag) {
        sender.sendMessage("flag: " + (flag ? "true" : "false"));
    }

    @Command(name = "flago", desc = "Test object flag.", usage = "[-t: player]")
    public void flagObject(@Sender Player sender, @Flag('t') Player target) {
        if (target == null) {
            target = sender;
        }
        sender.sendMessage("Target: " + target.getName());
    }
    
}
```

### Registering your commands

Registering your commands with drink is easy.

```java
    @Override
    public void onEnable() {
        CommandService drink = Drink.get(this);

        drink.register(new ExampleCommand(), "example", "some-alias")
            .registerSub(new SomeOtherCommand()); // if you want to register a sub-command
        // or
        drink.registerSub(drink.get("example"), new SomeOtherCommand());

        // Make sure you call drink.registerCommands() after you're done registering your commands to register them
        // With spigot.
        drink.registerCommands();
    }
```

Do not forget to call `drink.registerCommands()`!

### Binding providers
Providers are the basis for how drink works.  If you are familar with Google's Guice or sk89q's Intake, this syntax will be very familiar.

You simply tell drink how to acquire an instance of a specific class type, by binding a provider for it.

drink comes with the following providers out of the box:
- CommandSender
- Player Sender
- Player
- Primitives (boolean, double, integer, long)
- String
- Text (multi-argument string)
- Instances of any type
- Any enum

All providers must extend the ` DrinkProvider<T>` class.

An example provider:

```java
import com.jonahseguin.drink.argument.CommandArg;
import com.jonahseguin.drink.exception.CommandExitMessage;
import com.jonahseguin.drink.parametric.DrinkProvider;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.annotation.Annotation;
import java.util.List;
import java.util.stream.Collectors;

public class PlayerProvider extends DrinkProvider<Player> {

    private final Plugin plugin;

    public PlayerProvider(Plugin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean doesConsumeArgument() {
        return true; // Does this provider consume an argument provided by the command sender?
    }

    @Override
    public boolean isAsync() {
        return false; // Should this provider be executed asynchronously? (for database operations or similar)
    }

    @Override
    public boolean allowNullArgument() {
        return false; // Should this provider allow a null argument (the value for arg.next() in #provide()) (i.e when this is optional or in a flag)
    }

    @Nullable
    @Override
    public Player defaultNullValue() {
        return null; // The value to use when the arg.next() value is null (before it gets passed to #provide()) (when #allowNullArgument() is true)
    }

    @Nullable
    @Override
    public Player provide(@Nonnull CommandArg arg, @Nonnull List<? extends Annotation> annotations) throws CommandExitMessage {
        String name = arg.get();
        Player p = plugin.getServer().getPlayer(name);
        if (p != null) {
            return p;
        }
        // The CommandExitMessage exception should be used any time you want to cancel execution of a command with a provided error message.
        throw new CommandExitMessage("No player online with name '" + name + "'.");
    }

    @Override
    public String argumentDescription() {
        return "player"; // The description to be used for this argument, when generating the usage message for the argument etc 
    }

    @Override
    public List<String> getSuggestions(@Nonnull String prefix) {
        // This returns a list of suggestions to be used when Tab-completing this argument with a specified prefix
        // the Prefix can be empty ("") 
        // If no suggestions are possible for your provider, use Collections.emptyList()
        final String finalPrefix = prefix.toLowerCase();
        return plugin.getServer().getOnlinePlayers().stream().map(p -> p.getName().toLowerCase()).filter(s -> finalPrefix.length() == 0 || s.startsWith(finalPrefix)).collect(Collectors.toList());
    }
```

### Binding your provider

Now we want to tell drink how to use our provider.

```java
// Binding to a provider
drink.bind(Player.class).toProvider(new PlayerProvider(this));
// You can also bind to an instance if you want
drink.bind(MyPlugin.class).toInstance(this);
```

You can have multiple bindings for the same class using `@Classifier` annotations.
For example, if you wanted to use a seperate provider for when the Player is the CommandSender, you can use
```java
drink.bind(Player.class).annotatedWith(Sender.class).toProvider(new PlayerSenderProvider());
```

If you want to make your own `@Classifier` annotation, make sure it has the following properties:
- `RetentionPolicy` should be `RUNTIME` (`@Retention(RetentionPolicy.RUNTIME)` annotated on your annotation's class)
- `@Target` should be `ElementType.PARAMETER`
- Must have a `@Classifier` annotation on your annotation's class

### Permissions

Requiring a permission to execute a command with drink is also simple.
Simply add a `@Require("your.permission")` annotation to your command's method.

```java
    @Command(name = "", aliases = {}, desc = "An example command.", usage = "")
    @Require("example.use")
    public void exampleRoot(@Sender CommandSender sender) {
    ...
```

### The best part of drink: easy argument parsing

Parsing arguments with drink is easy.  As long as there is a provider for your argument type 
(which there is by default for all primitives, strings, players, and the CommandSender), you can simply add it to your
method's parameters.

```java
    @Command(name = "name", desc = "Set a player's display name.", usage = "<player> <name>")
    public void setName(@Sender CommandSender sender, Player target, String name) {
        target.setDisplayName(name);
    }
```

### `@Text`

The '@Text' modifier annotation allows the remaining arguments in a command to be combined into one String.

For example:
```java
    @Command(name = "msg", desc = "Send a message to a player.", usage = "<player> <message..>")
    public void msg(@Sender CommandSender sender, Player target, @Text String message) {
        target.sendMessage("Message from " + sender.getName() + ": " + message);
    }
```
Note that it is important that the argument with @Text must be the last parameter in your method, 
otherwise your command will fail to register.

### `@OptArg`

The `@OptArg("<default value>")` annotation allows you to have an optional argument with an optional default value.
The value provided in the `@OptArg()` annotation will be passed to the provider for whatever the argument's type is.
If nothing is provided, null is passed (or the `DrinkProvider`'s `#defaultNull()` method)
Obviously if the command sender provides an argument that is applicable, that will be used.

For example:

```java
    @Command(name = "setlevel", desc = "Set your XP level.") // usage will be auto-generated to look like: [level = 1]
    @Require("example.setlevel")
    public void setLevel(@Sender Player player, @OptArg("1") int level) {
        player.setLevel(level);
    }
```

### Flags / `@Flag('f')`

Flags are optional additions to commands that can be used for handy extras.

Flags can be boolean-based or object/string-based.

Flags can be used in a command like so: 
- `/example flagb -f` (f = true), if -f wasn't provided, f = false
- `/example flago -s player` (s = 'player')

For example:

```java
    @Command(name = "flagb", desc = "Test boolean flag.", usage = "[-f: flag]")
    public void flagBoolean(@Sender CommandSender sender, @Flag('f') boolean flag) {
        sender.sendMessage("flag: " + (flag ? "true" : "false"));
    }

    @Command(name = "flago", desc = "Test object flag.", usage = "[-t: player]")
    public void flagObject(@Sender Player sender, @Flag('t') Player target) {
        if (target == null) {
            target = sender;
        }
        sender.sendMessage("Target: " + target.getName());
    }
```


