package net.nimajnebec.costumizer;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.nimajnebec.costumizer.api.CostumizerApiService;
import net.nimajnebec.costumizer.authentication.AuthenticationService;
import net.nimajnebec.costumizer.commands.CostumeCommand;
import net.nimajnebec.costumizer.commands.CostumizerCommand;
import net.nimajnebec.costumizer.commands.brigadier.BrigadierRegister;
import net.nimajnebec.costumizer.configuration.ConfigurationException;
import net.nimajnebec.costumizer.configuration.CostumizerConfiguration;
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
    private final CostumeService costumeService = new CostumeService(this);
    private CostumizerApiService apiService;

    public Costumizer() {
        instance = this;
    }

    @Override
    public void onEnable() {
        // Setup Configuration
        this.saveDefaultConfig();

        try {
            configuration.load();
        }
        catch (ConfigurationException e) {
            logger.error("Configuration Error: {}", e.getMessage());
            logger.error("{} will now disable", this.getName());
            return;
        }

        // Setup API Service
        try {
            this.apiService = new CostumizerApiService(this);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
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
        return this.configuration.getChatPrefix();
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

    public CostumeService getCostumeService() {
        return this.costumeService;
    }
}
