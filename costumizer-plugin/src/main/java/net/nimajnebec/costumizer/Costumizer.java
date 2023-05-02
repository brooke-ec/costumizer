package net.nimajnebec.costumizer;

import net.nimajnebec.costumizer.commands.CostumizerCommand;
import net.nimajnebec.costumizer.commands.brigadier.BrigadierRegister;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;

public final class Costumizer extends JavaPlugin {
    final Logger logger = this.getSLF4JLogger();
    final BrigadierRegister brigadier = new BrigadierRegister(this);
    CostumizerConfiguration configuration = new CostumizerConfiguration(this);

    @Override
    public void onEnable() {
        // Setup Configuration
        this.saveDefaultConfig();
        if (!configuration.validate()) {
            logger.error("{} will now disable", this.getName());
            return;
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

    public CostumizerConfiguration getConfiguration() {
        return this.configuration;
    }
}
