package net.nimajnebec.costumizer.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.kyori.adventure.text.Component;
import net.minecraft.commands.CommandSourceStack;
import net.nimajnebec.costumizer.Costumizer;
import net.nimajnebec.costumizer.api.json.CostumeName;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class CostumeEquipCommand implements Command<CommandSourceStack>, SuggestionProvider<CommandSourceStack> {

    private final Map<UUID, SuggestionCache> userMap = new HashMap<>();

    private final Costumizer plugin;

    public CostumeEquipCommand() {
        this.plugin = Costumizer.getInstance();
    }

    @Override
    public int run(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
        CommandSender sender = ctx.getSource().getBukkitSender();
        if (!(sender instanceof Player player)) {
            sender.sendMessage(plugin.getChatPrefix().append(plugin.ONLY_PLAYERS_MESSAGE));
            return 0;
        }

        sender.sendMessage(Component.text("Equip!"));
        return 0;
    }

    @Override
    public CompletableFuture<Suggestions> getSuggestions(
            CommandContext<CommandSourceStack> ctx,
            SuggestionsBuilder builder) throws CommandSyntaxException {
        CompletableFuture<Suggestions> future = new CompletableFuture<>();
        CommandSender sender = ctx.getSource().getBukkitSender();
        if (!(sender instanceof Player player)) return null;
        UUID uuid = player.getUniqueId();

        if (!userMap.containsKey(uuid)) {
            userMap.put(uuid, new SuggestionCache(() -> {
                CostumeName[] costumes = plugin.getApiService().listCostumes(uuid);
                String[] result = new String[costumes.length];
                for (int i = 0; i < result.length; i++) {
                    result[i] = costumes[i].name;
                }
                return result;
            }));
        }

        return userMap.get(uuid).get(builder);
    }
}
