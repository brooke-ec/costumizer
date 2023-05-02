package net.nimajnebec.costumizer.commands.brigadier;

import com.destroystokyo.paper.event.brigadier.CommandRegisteredEvent;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class BrigadierRegister implements Listener {

    Map<Command, BrigadierCommand> commandMap = new HashMap<>();
    JavaPlugin plugin;

    public BrigadierRegister(JavaPlugin plugin) {
        this.plugin = plugin;
    }

    public void setup() {
        plugin.getServer().getPluginManager().registerEvents(this, this.plugin);
    }

    public void register(String name, BrigadierCommand builder) {
        PluginCommand command = this.plugin.getCommand(name);
        command.register(this.plugin.getServer().getCommandMap());
        this.commandMap.put(command, builder);
    }

    @EventHandler
    public void onRegisterCommand(CommandRegisteredEvent<CommandSourceStack> event) {
        Command command = event.getCommand();
        if (commandMap.containsKey(command)) {
            LiteralArgumentBuilder<CommandSourceStack> root = BrigadierCommand.literal(command.getLabel());
            commandMap.get(command).define(root);
            event.setLiteral(root.build());
        }
    }
}
