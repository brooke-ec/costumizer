package net.nimajnebec.costumizer.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.nimajnebec.costumizer.commands.brigadier.BrigadierCommand;
import org.bukkit.command.CommandSender;

public class CostumizerCommand extends BrigadierCommand {

    @Override
    public void define(LiteralArgumentBuilder<CommandSourceStack> root) {
        root.then(literal("ui").executes(ctx -> {
            CommandSender sender = ctx.getSource().getBukkitSender();
            sender.sendMessage("Costumizer UI!");
            return 1;
        }));
    }
}
