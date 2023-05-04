package net.nimajnebec.costumizer.commands;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.nimajnebec.costumizer.commands.brigadier.BrigadierCommand;
import org.bukkit.Location;

public class CostumeCommand extends BrigadierCommand {

    @Override
    public void define(LiteralArgumentBuilder<CommandSourceStack> root) {
        CostumeEquipCommand equipCommand = new CostumeEquipCommand();
        root.then(literal("equip").then(argument("name", StringArgumentType.word())
                .suggests(equipCommand).executes(equipCommand))).then(literal("clear")).executes(this::test);
    }

    private int test(CommandContext<CommandSourceStack> ctx) {
        Player player = ctx.getSource().getPlayer();
        Level level = ctx.getSource().getLevel();
        Location loc = player.getBukkitEntity().getLocation();

        return 0;
    }
}
