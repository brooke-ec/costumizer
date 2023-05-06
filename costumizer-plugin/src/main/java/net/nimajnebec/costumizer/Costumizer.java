package net.nimajnebec.costumizer;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerPlayer;
import net.nimajnebec.costumizer.api.CostumizerApiService;
import net.nimajnebec.costumizer.commands.CostumeCommand;
import net.nimajnebec.costumizer.commands.CostumizerCommand;
import net.nimajnebec.costumizer.commands.brigadier.BrigadierRegister;
import net.nimajnebec.costumizer.configuration.ConfigurationException;
import net.nimajnebec.costumizer.configuration.CostumizerConfiguration;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.slf4j.Logger;

import java.net.MalformedURLException;
import java.util.NoSuchElementException;

public final class Costumizer extends JavaPlugin {
    public final Component ONLY_PLAYERS_MESSAGE =
            Component.text("This command can only be used by players.", NamedTextColor.RED);

    private static Costumizer instance;
    private final Logger logger = this.getSLF4JLogger();
    private final BrigadierRegister brigadier = new BrigadierRegister(this);
    private final CostumizerConfiguration configuration = new CostumizerConfiguration(this);
    private final CostumizerApiService apiService = new CostumizerApiService(this);
    private final CostumeService costumeService = new CostumeService(this);
    private final PlayerEvents playerEvents = new PlayerEvents(this);

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

        // Setup Costume Service
        this.costumeService.initialise();

        // Setup API Service
        try {
            this.apiService.initialise();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }

        // Register Commands
        brigadier.setup();
        brigadier.register("costumizer", new CostumizerCommand());
        brigadier.register("costume", new CostumeCommand());

        // Register Events
        PluginManager manager = this.getServer().getPluginManager();
        manager.registerEvents(playerEvents, this);

        // Initialise Online Players
        for (Player player : this.getServer().getOnlinePlayers()) {
            playerEvents.initialisePlayer(player);
        }

        logger.info("{} {} Loaded!", this.getName(), this.getPluginMeta().getVersion());
    }

    @Override
    public void onDisable() {
        for (Player player : this.getServer().getOnlinePlayers()) {
            if (costumeService.inCostume(player)) this.costumeService.clear(player);
            try {
                PacketInterceptor.removeInterceptor(player);
            } catch (NoSuchElementException e) {
                logger.warn("Could not remove packet interceptor from {}", player.getName());
            }
        }
    }

    public void broadcast(Packet<?> packet) {
        for (Player player : this.getServer().getOnlinePlayers()) {
            ServerPlayer handle = ((CraftPlayer) player).getHandle();
            handle.connection.send(packet);
        }
    }

    public Component getChatPrefix() {
        return this.configuration.getChatPrefix();
    }

    public CostumizerConfiguration getConfiguration() {
        return this.configuration;
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
