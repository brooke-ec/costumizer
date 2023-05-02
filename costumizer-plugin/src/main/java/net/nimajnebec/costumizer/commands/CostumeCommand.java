package net.nimajnebec.costumizer.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.nimajnebec.costumizer.Costumizer;
import net.nimajnebec.costumizer.commands.brigadier.BrigadierCommand;

public class CostumeCommand extends BrigadierCommand {

    @Override
    public void define(LiteralArgumentBuilder<CommandSourceStack> root) {
        CostumeEquipCommand equipCommand = new CostumeEquipCommand();
        root.then(literal("equip").then(argument("costume_name", StringArgumentType.word())
                .suggests(equipCommand).executes(equipCommand))).then(literal("clear"));
    }
}
