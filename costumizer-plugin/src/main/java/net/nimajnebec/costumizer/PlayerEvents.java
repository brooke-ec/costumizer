package net.nimajnebec.costumizer;

import net.minecraft.server.level.ServerPlayer;
import org.bukkit.craftbukkit.v1_19_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerEvents implements Listener {
    private final Costumizer plugin;

    public PlayerEvents(Costumizer plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        this.initialisePlayer(event.getPlayer());
    }

    public void initialisePlayer(Player player) {
        ServerPlayer handle = ((CraftPlayer) player).getHandle();
        handle.connection.send(plugin.getCostumeService().getTeamPacket());
        PacketInterceptor.injectInterceptor(player);
    }

    @EventHandler
    public void onLeave(PlayerQuitEvent event) {
        CostumeService service = plugin.getCostumeService();
        Player player = event.getPlayer();
        if (service.inCostume(player))
            service.cleanupPlayer(event.getPlayer().getUniqueId());
    }

}
