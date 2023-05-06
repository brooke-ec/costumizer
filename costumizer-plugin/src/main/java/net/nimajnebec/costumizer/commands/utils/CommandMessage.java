package net.nimajnebec.costumizer.commands.utils;

import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.adventure.PaperAdventure;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.commands.CommandSourceStack;
import net.nimajnebec.costumizer.Costumizer;

public class CommandMessage {
    private final Component message;

    public CommandMessage(String message) {
        this.message = Component.text(message);
    }

    public CommandMessage(Component message) {
        this.message = message;
    }

    public net.minecraft.network.chat.Component create() {
        return this.create(false);
    }

    public net.minecraft.network.chat.Component create(boolean error) {
        Component prefix = Costumizer.getInstance().getChatPrefix();
        Component message = this.message;
        if (error) message = message.color(NamedTextColor.RED);
        return PaperAdventure.asVanilla(prefix.append(message));
    }

    public int fail(CommandContext<CommandSourceStack> ctx) {
        ctx.getSource().sendFailure(this.create(true), false);
        return 0;
    }

    public int success(CommandContext<CommandSourceStack> ctx, boolean broadcastToOps) {
        ctx.getSource().sendSuccess(this.create(), broadcastToOps);
        return 1;
    }

    public int success(CommandContext<CommandSourceStack> ctx) {
        ctx.getSource().sendSuccess(this.create(), false);
        return 1;
    }
}
