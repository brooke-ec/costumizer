package net.nimajnebec.costumizer.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.nimajnebec.costumizer.Costumizer;
import net.nimajnebec.costumizer.commands.brigadier.BrigadierCommand;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class CostumeCommand extends BrigadierCommand {

    @Override
    public void define(LiteralArgumentBuilder<CommandSourceStack> root) {
        CostumeEquipCommand equipCommand = new CostumeEquipCommand();
        root.then(literal("equip").then(argument("name", StringArgumentType.word())
                .suggests(equipCommand).executes(equipCommand))).then(literal("clear").executes(c -> {
            Costumizer.getInstance().getCostumeService().clear((Player) c.getSource().getBukkitSender());
            return 0;
        }));
    }
}
