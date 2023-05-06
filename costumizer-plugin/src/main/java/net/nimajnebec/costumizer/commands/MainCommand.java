package net.nimajnebec.costumizer.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.commands.CommandSourceStack;
import net.nimajnebec.costumizer.CostumeService;
import net.nimajnebec.costumizer.Costumizer;
import net.nimajnebec.costumizer.commands.utils.BrigadierCommand;
import net.nimajnebec.costumizer.commands.utils.CommandMessage;
import org.bukkit.entity.Player;

public class MainCommand extends BrigadierCommand {
    private static final CommandMessage ERROR_NAME_OMITTED = new CommandMessage("Incomplete Command - Please specify the name of the costume to equip.");
    private static final CommandMessage SUBCOMMAND_OMITTED = new CommandMessage("Incomplete Command - Please specify a subcommand.");
    private static final CommandMessage ERROR_NOT_IN_COSTUME = new CommandMessage("Could not Clear - You are not currently in costume.");
    private static final CommandMessage SUCCESS_COSTUME_CLEARED = new CommandMessage("Costume Cleared!");
    private final Costumizer plugin;

    public MainCommand(Costumizer plugin) {
        this.plugin = plugin;
    }

    @Override
    public void define(LiteralArgumentBuilder<CommandSourceStack> root) {
        CostumeEquipCommand equipCommand = new CostumeEquipCommand();

        root.requires(this::isPlayer).executes(SUBCOMMAND_OMITTED::fail)
            .then(literal("ui").executes(new CostumizerUiCommand()))
            .then(literal("equip").executes(ERROR_NAME_OMITTED::fail).then(argument("name", StringArgumentType.word()).suggests(equipCommand).executes(equipCommand)))
            .then(literal("clear").executes(this::clearCostume));
    }

    private boolean isPlayer(CommandSourceStack source) {
        return source.isPlayer();
    }

    private int clearCostume(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        Player player = (Player) ctx.getSource().getBukkitSender();
        CostumeService service = plugin.getCostumeService();
        if (!service.inCostume(player)) {
            ERROR_NOT_IN_COSTUME.fail(ctx);
            return 0;
        }
        service.clear(player);
        SUCCESS_COSTUME_CLEARED.success(ctx);
        return 1;
    }

}
