package net.nimajnebec.costumizer;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.nimajnebec.costumizer.api.CostumizerApiService;
import net.nimajnebec.costumizer.authentication.AuthenticationService;
import net.nimajnebec.costumizer.commands.CostumeCommand;
import net.nimajnebec.costumizer.commands.CostumizerCommand;
import net.nimajnebec.costumizer.commands.brigadier.BrigadierRegister;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;

import java.net.MalformedURLException;

public final class Costumizer extends JavaPlugin {
    public final Component ONLY_PLAYERS_MESSAGE =
            Component.text("This command can only be used by players.", NamedTextColor.RED);

    private static Costumizer instance;
    private final Logger logger = this.getSLF4JLogger();
    private final BrigadierRegister brigadier = new BrigadierRegister(this);
    private final CostumizerConfiguration configuration = new CostumizerConfiguration(this);
    private final AuthenticationService authentication = new AuthenticationService(this.getConfiguration());
    private CostumizerApiService apiService;
    private Component chatPrefix;

    public Costumizer() {
        instance = this;
    }

    @Override
    public void onEnable() {
        // Setup Configuration
        this.saveDefaultConfig();
        if (!configuration.validate()) {
            logger.error("{} will now disable", this.getName());
            return;
        }

        // Setup API Service
        try {
            this.apiService = new CostumizerApiService(this);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
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
        brigadier.register("costumizer", new CostumizerCommand());
        brigadier.register("costume", new CostumeCommand());

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

    public static Costumizer getInstance() {
        return instance;
    }

    public CostumizerApiService getApiService() {
        return this.apiService;
    }
}
