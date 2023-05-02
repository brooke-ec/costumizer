package net.nimajnebec.costumizer.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import net.minecraft.commands.CommandSourceStack;
import net.nimajnebec.costumizer.Costumizer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.net.MalformedURLException;
import java.net.URL;

public record UiCommand(Costumizer plugin) implements Command<CommandSourceStack> {
    static final Component ONLY_PLAYERS_MESSAGE =
            Component.text("This command can only be used by players.", NamedTextColor.RED);
    static final String LOGIN_URL = "/login?token=";

    @Override
    public int run(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSender sender = ctx.getSource().getBukkitSender();
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.getChatPrefix().append(ONLY_PLAYERS_MESSAGE));
            return 0;
        }

        String token = plugin.getAuthenticationService().generateToken(player.getUniqueId());
        String url = constructLoginUrl(token);

        player.sendMessage(plugin.getChatPrefix()
                .append(Component.text("Click the link below to open the Costumizer UI:")
                .appendNewline()
                .append(Component.text(truncate(url, 40))
                        .clickEvent(ClickEvent.openUrl(url)).decorate(TextDecoration.UNDERLINED)
                        .color(NamedTextColor.AQUA))));

        return 1;
    }

    private String truncate(String string, int length) {
        if (string.length() > length) return string.substring(0, length - 3) + "...";
        return string;
    }

    private String constructLoginUrl(String token) {
        try {
            return new URL(plugin.getConfiguration().getUiUrl(), LOGIN_URL) + token;
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }
}
