package net.nimajnebec.costumizer.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.nimajnebec.costumizer.Costumizer;
import net.nimajnebec.costumizer.api.json.CostumeData;
import net.nimajnebec.costumizer.api.json.CostumeName;
import net.nimajnebec.costumizer.commands.utils.CommandMessage;
import net.nimajnebec.costumizer.commands.utils.SuggestionCache;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

public class CostumeEquipCommand implements Command<CommandSourceStack>, SuggestionProvider<CommandSourceStack> {
    private static final CommandMessage ERROR_COSTUME_NOT_FOUND = new CommandMessage("Could not find a costume with that name.");
    private static final CommandMessage SUCCESS_EQUIPPED = new CommandMessage("Costume Equipped!");
    private static final Map<UUID, SuggestionCache> userMap = new HashMap<>();
    private final Costumizer plugin;

    public CostumeEquipCommand() {
        this.plugin = Costumizer.getInstance();
    }

    @Override
    public int run(CommandContext<CommandSourceStack> ctx) {
        Player player = (Player) ctx.getSource().getBukkitSender();

        Bukkit.getScheduler().runTaskAsynchronously(plugin, () -> {
            String name = ctx.getArgument("name", String.class);

            try {
                CostumeData data = plugin.getApiService().getCostumeData(player.getUniqueId(), name);
                Bukkit.getScheduler().runTask(plugin, () -> {
                    plugin.getCostumeService().equip(player, data);
                    SUCCESS_EQUIPPED.success(ctx);
                });
            } catch (FileNotFoundException e) {
                ERROR_COSTUME_NOT_FOUND.fail(ctx);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        });

        return 0;
    }

    @Override
    public CompletableFuture<Suggestions> getSuggestions(CommandContext<CommandSourceStack> ctx, SuggestionsBuilder builder) {
        Player player = (Player) ctx.getSource().getBukkitSender();
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
