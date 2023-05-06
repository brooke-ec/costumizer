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

public class CostumizerUiCommand implements Command<CommandSourceStack> {

    public Costumizer plugin;

    public CostumizerUiCommand() {
        plugin = Costumizer.getInstance();
    }

    @Override
    public int run(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSender sender = ctx.getSource().getBukkitSender();
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.getChatPrefix().append(plugin.ONLY_PLAYERS_MESSAGE));
            return 0;
        }

        String url = plugin.getApiService().getLoginUrl(player.getUniqueId());

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
}
