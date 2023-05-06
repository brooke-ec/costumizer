package net.nimajnebec.costumizer.commands.utils;

import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import net.nimajnebec.costumizer.Costumizer;
import org.bukkit.Bukkit;

import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;

public class SuggestionCache {
    private static final long CACHE_DELAY = 30 * 1000;
    private long lastFetched = 0;
    private String[] suggestions = null;
    private final Callable<String[]> fetcher;

    public SuggestionCache(Callable<String[]> fetcher) {
        this.fetcher = fetcher;
    }

    public CompletableFuture<Suggestions> get(SuggestionsBuilder builder) {
        CompletableFuture<Suggestions> future = new CompletableFuture<>();

        if (System.currentTimeMillis() - lastFetched > CACHE_DELAY
                && (suggestions == null || builder.getRemaining().isEmpty())) {
            Bukkit.getScheduler().runTaskAsynchronously(Costumizer.getInstance(), () -> {
                try {
                    this.suggestions = fetcher.call();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                future.complete(filter(builder));
            });
        } else future.complete(filter(builder));

        return future;
    }

    public Suggestions filter(SuggestionsBuilder builder) {
        String remaining = builder.getRemainingLowerCase();
        for (final String suggestion : this.suggestions) {
            if (suggestion.toLowerCase().startsWith(remaining))
                builder.suggest(suggestion);
        }
        return builder.build();
    }
}
