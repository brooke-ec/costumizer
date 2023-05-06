package net.nimajnebec.costumizer.commands.utils;

import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.builder.RequiredArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;

public abstract class BrigadierCommand {

    public abstract void define(LiteralArgumentBuilder<CommandSourceStack> root);

    public static LiteralArgumentBuilder<CommandSourceStack> literal(String literal) {
        return LiteralArgumentBuilder.literal(literal);
    }

    public static <T> RequiredArgumentBuilder<CommandSourceStack, T> argument(String name, ArgumentType<T> type) {
        return RequiredArgumentBuilder.argument(name, type);
    }

}
