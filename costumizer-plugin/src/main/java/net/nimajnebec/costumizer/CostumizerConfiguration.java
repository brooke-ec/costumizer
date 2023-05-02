package net.nimajnebec.costumizer;

import org.bukkit.configuration.file.FileConfiguration;

import java.net.MalformedURLException;
import java.net.URL;

public class CostumizerConfiguration {

    private final Costumizer plugin;
    private final FileConfiguration file;

    public CostumizerConfiguration(Costumizer plugin) {
        this.file = plugin.getConfig();
        this.plugin = plugin;
    }

    public boolean validate() {
        return isString("ui-url") &
               isString("secret");
    }

    private boolean isString(String path) {
        if (file.isString(path)) return true;
        plugin.logger.error("Configuration option '{}' is not a string", path);
        return false;
    }

    public URL getUiUrl() {
        try {
            return new URL(file.getString("ui-url"));
        } catch (MalformedURLException e) {
            plugin.logger.error("Configuration option 'ui-url' is malformed");
            throw new RuntimeException(e);
        }
    }

    public String getSecret() {
        return file.getString("secret");
    }
}
