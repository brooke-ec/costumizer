package net.nimajnebec.costumizer;

import org.bukkit.configuration.file.FileConfiguration;

import javax.annotation.Nullable;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

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
        plugin.getSLF4JLogger().error("Configuration option '{}' is not a string", path);
        return false;
    }

    public URL getUiUrl() {
        try {
            return new URL(file.getString("ui-url"));
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] getSecret() {
        return file.getString("secret").getBytes(StandardCharsets.UTF_8);
    }

    public @Nullable String getPrefix() {
        if (file.isString("prefix")) return file.getString("prefix");
        return null;
    }
}
