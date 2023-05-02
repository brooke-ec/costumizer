package net.nimajnebec.costumizer;

import net.nimajnebec.costumizer.commands.CostumizerCommand;
import net.nimajnebec.costumizer.commands.brigadier.BrigadierRegister;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;

public final class Costumizer extends JavaPlugin {
    Logger logger = this.getSLF4JLogger();
    BrigadierRegister brigadier = new BrigadierRegister(this);

    @Override
    public void onEnable() {
        brigadier.setup();
        brigadier.register("costumizer", new CostumizerCommand());
        logger.info("{} {} Loaded!", this.getName(), this.getPluginMeta().getVersion());
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
