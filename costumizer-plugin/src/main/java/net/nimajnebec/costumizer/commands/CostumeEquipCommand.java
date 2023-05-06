package net.nimajnebec.costumizer.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import io.papermc.paper.adventure.AdventureComponent;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.minecraft.commands.CommandSourceStack;
import net.nimajnebec.costumizer.Costumizer;
import net.nimajnebec.costumizer.api.json.CostumeData;
import net.nimajnebec.costumizer.api.json.CostumeName;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.FileNotFoundException;
import java.io.IOException;
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

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            String name = ctx.getArgument("name", String.class);

            try {
                CostumeData data = plugin.getApiService().getCostumeData(player.getUniqueId(), name);
                Bukkit.getScheduler().runTask(plugin, () -> {
                    plugin.getCostumeService().equip(player, data);
                });
            } catch (FileNotFoundException e) {
                player.sendMessage(plugin.getChatPrefix()
                        .append(Component.text("Costume not found '"+name+"'")
                        .color(NamedTextColor.RED)));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        });

        return 0;
    }

    @Override
    public CompletableFuture<Suggestions> getSuggestions(
            CommandContext<CommandSourceStack> ctx,
            SuggestionsBuilder builder) throws CommandSyntaxException {
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
