package net.nimajnebec.costumizer.configuration;

public class ConfigurationException extends Exception {

    public ConfigurationException(String message, Object... args) {
        super(message.formatted(args));
    }
}
