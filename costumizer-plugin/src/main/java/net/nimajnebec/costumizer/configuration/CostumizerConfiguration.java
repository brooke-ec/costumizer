package net.nimajnebec.costumizer.configuration;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.nimajnebec.costumizer.Costumizer;
import org.bukkit.configuration.file.FileConfiguration;
import org.slf4j.Logger;

import javax.annotation.Nullable;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class CostumizerConfiguration {

    private final Costumizer plugin;
    private FileConfiguration file;
    private final Logger logger;

    // Config options
    private URL uiUrl;
    private byte[] secret;
    private Component chatPrefix;
    private Component namePrefix;

    public CostumizerConfiguration(Costumizer plugin) {
        this.logger = plugin.getSLF4JLogger();
        this.plugin = plugin;
    }

    public void load() throws ConfigurationException {
        this.file = plugin.getConfig();

        this.uiUrl = getURL("url");
        this.secret = getString("secret").getBytes(StandardCharsets.UTF_8);
        this.chatPrefix = getComponent("chat-prefix").append(Component.text(" "));
        this.namePrefix = getComponent("name-prefix");
    }

    private Component getComponent(String path) throws ConfigurationException {
        MiniMessage mm = MiniMessage.miniMessage();
        return mm.deserialize(getString(path));
    }
    private URL getURL(String path) throws ConfigurationException {
        try {
            return new URL(getString(path));
        } catch (MalformedURLException e) {
            throw new ConfigurationException("Option '%s' is not a valid url", path);
        }
    }
    private String getString(String path) throws ConfigurationException {
        if (file.isString(path)) return file.getString(path);
        throw new ConfigurationException("Option '%s' is not a string", path);
    }

    // Getters

    public URL getUiUrl() {
        return this.uiUrl;
    }

    public byte[] getSecret() {
        return this.secret;
    }

    public Component getChatPrefix() {
        return this.chatPrefix;
    }

    public Component getNamePrefix() {
        return this.namePrefix;
    }
}
