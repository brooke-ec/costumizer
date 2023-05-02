package net.nimajnebec.costumizer;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.nimajnebec.costumizer.authentication.AuthenticationService;
import net.nimajnebec.costumizer.commands.CostumizerCommand;
import net.nimajnebec.costumizer.commands.brigadier.BrigadierRegister;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;

public final class Costumizer extends JavaPlugin {
    private final Logger logger = this.getSLF4JLogger();
    private final BrigadierRegister brigadier = new BrigadierRegister(this);
    private final CostumizerConfiguration configuration = new CostumizerConfiguration(this);
    private final AuthenticationService authentication = new AuthenticationService(this.getConfiguration());
    private Component chatPrefix;

    @Override
    public void onEnable() {
        // Setup Configuration
        this.saveDefaultConfig();
        if (!configuration.validate()) {
            logger.error("{} will now disable", this.getName());
            return;
        }

        // Setup Chat Prefix
        if (configuration.getPrefix() == null)
            chatPrefix = Component.text("[")
                .append(Component.text(this.getName().toUpperCase()).color(NamedTextColor.GREEN))
                .append(Component.text("] "));
        else {
            MiniMessage mm = MiniMessage.miniMessage();
            chatPrefix = mm.deserialize(configuration.getPrefix());
        }

        // Register Commands
        brigadier.setup();
        brigadier.register("costumizer", new CostumizerCommand(this));

        logger.info("{} {} Loaded!", this.getName(), this.getPluginMeta().getVersion());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public Component getChatPrefix() {
        return this.chatPrefix;
    }

    public CostumizerConfiguration getConfiguration() {
        return this.configuration;
    }

    public AuthenticationService getAuthenticationService() {
        return this.authentication;
    }
}
