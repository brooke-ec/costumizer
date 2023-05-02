package net.nimajnebec.costumizer.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.kyori.adventure.text.Component;
import net.minecraft.commands.CommandSourceStack;
import net.nimajnebec.costumizer.Costumizer;
import net.nimajnebec.costumizer.commands.brigadier.BrigadierCommand;
import org.bukkit.command.CommandSender;

public class CostumizerCommand extends BrigadierCommand {

    public final Costumizer plugin;

    public CostumizerCommand(Costumizer plugin) {
        this.plugin = plugin;
    }

    @Override
    public void define(LiteralArgumentBuilder<CommandSourceStack> root) {
        root.then(literal("ui").executes(this :: sendLogin));
    }

    private int sendLogin(CommandContext<CommandSourceStack> ctx) {
        CommandSender sender = ctx.getSource().getBukkitSender();
        String url = plugin.getConfiguration().getUiUrl().toString();

        Component message = Component.text(url);
        sender.sendMessage(message);
        return 1;
    }
}
